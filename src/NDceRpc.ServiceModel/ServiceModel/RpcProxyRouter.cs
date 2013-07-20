using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.IO;
using System.Linq;
using System.Reflection;
using System.Runtime.InteropServices;
using System.Runtime.Remoting.Messaging;
using System.Runtime.Remoting.Proxies;
using System.ServiceModel;
using System.ServiceModel.Channels;
using System.Threading;
using System.Threading.Tasks;

using NDceRpc.ExplicitBytes;
using NDceRpc.Interop;
using NDceRpc.ServiceModel.Channels;


namespace NDceRpc.ServiceModel
{
    public class RpcProxyRouter : IDisposable
    {
        private object _service;
        private IExplicitBytesClient _client;
        private RpcServerStub _dipatcher;
        private object _remote;
        private DispatchTable _operations = new DispatchTable();
        private string _session;
        private BinaryObjectSerializer _serializer;
        private Type _typeOfService;
        private InstanceContext _context;
        private readonly Type _generatedProxyType;
        private bool _disposed;
        private string _address;
        private ManualResetEvent _operationPending = new ManualResetEvent(true);
        private Binding _binding;
        private Guid _uuid;
        private SynchronizationContext _syncContext;

        //TODO: split RpcProxyRouter and RpcCallbackProxy
        public RpcProxyRouter(string address, Type typeOfService, Binding binding, bool callback = false, InstanceContext context = null, Guid customUuid = default(Guid), Type generatedProxyType = null)
        {
            _serializer = binding.Serializer;
            _typeOfService = typeOfService;
            _context = context;
            if (_context != null && _context._useSynchronizationContext)
            {
                _syncContext = SynchronizationContext.Current;
            }
            _generatedProxyType = generatedProxyType;
            _binding = binding;
            _address = address;

            _operations = DispatchTableFactory.GetOperations(_typeOfService);


            _uuid = EndpointMapper.CreateUuid(_address, typeOfService);
            if (customUuid != Guid.Empty) // callback proxy
            {
                _uuid = customUuid;
            }

            //TODO: got to Mono and reuse their approach for WCF


            var serviceContract = _typeOfService.GetCustomAttributes(typeof(ServiceContractAttribute), false).SingleOrDefault() as ServiceContractAttribute;
            //TODO: null check only for duplex callback, really should always be here
            if (serviceContract != null && serviceContract.SessionMode == SessionMode.Required)
            {
                _session = Guid.NewGuid().ToString();
            }
            //TODO: allow to be initialized with pregenerated proxy
            var realProxy = new RpcRealProxy(_typeOfService, this); ;
            _remote = realProxy.GetTransparentProxy();
            _client = TransportFactory.CreateClient(_binding, _uuid, _address);


        }

        internal class RpcRealProxy : RealProxy, System.Runtime.Remoting.IRemotingTypeInfo, IContextChannel
        {


            private readonly Type _service;
            private readonly RpcProxyRouter _router;
            private string _typeName;
            private CommunicationState _state = CommunicationState.Created;
            private TimeSpan _operationTimeout = TimeSpan.FromSeconds(60);

            public RpcRealProxy(Type service, RpcProxyRouter router)
                : base(service)
            {
                _service = service;
                _router = router;
                _typeName = string.Format("{0}`[{1}]", typeof(RpcRealProxy).FullName, service.FullName);
            }

            public override IMessage Invoke(IMessage msg)
            {

                IMethodCallMessage input = (IMethodCallMessage)msg;


                //TODO: move to RpcCallbackProxy
                if (_router._context != null)
                    _router._context.Initialize(_router._typeOfService, _router._address, _router._binding, _router._session, _router._syncContext);

                if (input.TypeName.StartsWith(typeof(ICommunicationObject).FullName))
                {
                    //TODO: use somthing faster than string comparison
                    if (input.MethodName == "get_State")
                    {
                        return new ReturnMessage(State, null, 0, input.LogicalCallContext, input);
                    }
                }

                MethodResponse methodReturn;

                OperationDispatchBase op;
                if (_router._operations.TryGetValue(OperationDispatchBase.GetIdentifier(input.MethodBase), out op))
                {
                    var r = new MessageRequest();
                    if (_router._session != null)
                    {
                        r.Session = _router._session;
                    }
                    r.Operation = op.Identifier;
                    var ps = new List<RpcParamData>();
                    for (int i = 0; i < op.Params.Count; i++)
                    {
                        var paramIdentifier = i;//TODO: try to make this connection with more inderect way
                        var stream = new MemoryStream();
                        _router._serializer.WriteObject(stream, input.GetInArg(i));
                        ps.Add(new RpcParamData { Identifier = paramIdentifier, Data = stream.ToArray()  });
                    }
                    r.Data.AddRange(ps.ToArray());
                    var rData = new MemoryStream();
                    _router._serializer.WriteObject(rData, r);
                    if (op is AsyncOperationDispatch)
                    {
                        object asyncState = input.GetInArg(op.Params.Count + 1);
                        var asyncCallback = (AsyncCallback)input.GetInArg(op.Params.Count);
                        Task task = Tasks.Factory.StartNew((x) =>
                        {
                            try
                            {
                                _router._operationPending.Reset();

                                byte[] result = null;


                                result = ExecuteRequest(rData);

                                var response =
                                    (MessageResponse)
                                    ProtobufMessageEncodingBindingElement.ReadObject(new MemoryStream(result),
                                                                                     typeof(MessageResponse));
                                if (response.Error != null)
                                {
                                    throw new Exception(response.Error.Type + response.Error.Message);
                                }
                            }
                            catch (ExternalException ex)
                            {
                                throw HandleCommunicationError(ex);
                            }
                            catch (Exception ex)
                            {
                                bool handled = ErrorHandler.Handle(ex);
                                if (!handled) 
                                    throw;
                            }
                            finally
                            {
                                _router._operationPending.Set();
                            }
                            return new ReturnMessage(null, null, 0, null, input);
                        }, asyncState);

                        task.ContinueWith(x =>
                            {
                                //TODO: do exception handling like in WCF
                                RpcTrace.Error(x.Exception);

                                if (asyncCallback != null)
                                {
                                    asyncCallback(x);
                                }
                            }, TaskContinuationOptions.OnlyOnFaulted);

                        task.ContinueWith(x =>
                        {
                            if (asyncCallback != null)
                            {
                                asyncCallback(x);
                            }
                        }, TaskContinuationOptions.OnlyOnRanToCompletion);
                        return new ReturnMessage(task, null, 0, null, input);

                    }
                    else if (op.Operation.IsOneWay)
                    {
                        Debug.Assert(op.MethodInfo.ReturnType == typeof(void));

                        Task task = Tasks.Factory.StartNew(() =>
                        {
                            try
                            {
                                _router._operationPending.Reset();

                                byte[] result = null;


                                result = ExecuteRequest(rData);

                                var response = (MessageResponse)ProtobufMessageEncodingBindingElement.ReadObject(new MemoryStream(result), typeof(MessageResponse));
                                if (response.Error != null)
                                {
                                    return new ReturnMessage(new Exception(response.Error.Type + response.Error.Message), input);
                                }
                            }
                            catch (ExternalException ex)
                            {
                                throw  HandleCommunicationError(ex);
                            }
                            catch (Exception ex)
                            {
                                bool handled = ErrorHandler.Handle(ex);
                                if (!handled) 
                                    throw;
                            }
                            finally
                            {
                                _router._operationPending.Set();
                            }
                            return new ReturnMessage(null, null, 0, null, input);
                        }
                        );
                        //TODO: do exception handling like in WCF
                        task.ContinueWith(x => RpcTrace.Error(x.Exception), TaskContinuationOptions.OnlyOnFaulted);
                        return new ReturnMessage(null, null, 0, null, input);
                    }

                    try
                    {
                        _router._operationPending.Reset();

                        byte[] result = null;

                        //BUG: using tasks adds  30% to simple local calls with bytes, and 10% longer then WCF...
                        //TODO: use native MS-RPC timeouts
                        //Task operation = Task.Factory.StartNew(() =>
                        //    {
                                result = ExecuteRequest(rData);
                        //    });
                        //var ended = operation.Wait(_operationTimeout);
                        //if (!ended)
                        //{
                        //    var timeourError =
                        //        new TimeoutException(
                        //            string.Format("The request channel timed out attempting to send after {0}",
                        //                          _operationTimeout));
                        //    return new ReturnMessage(timeourError,input);
                        //}
                           
                        var response = (MessageResponse)ProtobufMessageEncodingBindingElement.ReadObject(new MemoryStream(result), typeof(MessageResponse));
                        if (response.Error != null)
                        {
                            throw new Exception(response.Error.Type + response.Error.Message);
                        }
                        if (op.MethodInfo.ReturnType != typeof(void))
                        {
                            var retVal = _router._serializer.ReadObject(new MemoryStream(response.Data), op.MethodInfo.ReturnType);
                            var ret = new ReturnMessage(retVal, null, 0, null, input);
                            return ret;
                        }
                    }
                    catch (ExternalException ex)
                    {
                        var wrappedException = HandleCommunicationError(ex);
                        return new ReturnMessage(wrappedException, input);
                    }
                    catch (Exception ex)
                    {
                        bool handled = ErrorHandler.Handle(ex);
                        if (!handled) return new ReturnMessage(ex, input);
                    }
                    finally
                    {
                        _router._operationPending.Set();
                    }
                    return new ReturnMessage(null, null, 0, null, input);
                }
                throw new InvalidOperationException(string.Format("Cannot find operation {0} on service {1}", input.MethodName, _service));

            }

            private Exception HandleCommunicationError(ExternalException ex)
            {
                //TODO: not all RPC errors means this - can fail in local memory and thread inside RPC - should interpret accordingly
                var oldState = State;
                setState(CommunicationState.Faulted);
                switch (oldState)
                {
                    case CommunicationState.Created:
                        return new EndpointNotFoundException(string.Format("Failed to connect to {0}", _router._address));
                    case CommunicationState.Opened:
                        return new CommunicationException(string.Format("Failed to request {0}", _router._address));
                }
                return ex;
            }

            private byte[] ExecuteRequest(MemoryStream rData)
            {
                var result = _router._client.Execute(rData.ToArray());
                setState(CommunicationState.Opened);
                return result;
            }

            private void setState(CommunicationState newState)
            {
                if (State == CommunicationState.Created && newState == CommunicationState.Opened)
                {
                    State = newState;
                }
                else if (newState == CommunicationState.Faulted)
                {
                    State = CommunicationState.Faulted;

                }
            }

            public bool CanCastTo(Type fromType, object o)
            {
                if (fromType == _service

                    )
                {
                    return true;
                }
                if (
                     fromType == typeof (IContextChannel) ||
                     fromType == typeof (IChannel) || 
                    fromType == typeof(ICommunicationObject))
                {
                    return true;
                }
                return false;
            }

            public string TypeName
            {
                get { return _typeName; }
                set { _typeName = value; }
            }

            public void Abort()
            {

            }

            public void Close()
            {

            }

            public void Close(TimeSpan timeout)
            {

            }

            public IAsyncResult BeginClose(AsyncCallback callback, object state)
            {
                throw new NotImplementedException();
            }

            public IAsyncResult BeginClose(TimeSpan timeout, AsyncCallback callback, object state)
            {
                throw new NotImplementedException();
            }

            public void EndClose(IAsyncResult result)
            {
                throw new NotImplementedException();
            }

            public void Open()
            {
                throw new NotImplementedException();
            }

            public void Open(TimeSpan timeout)
            {
                throw new NotImplementedException();
            }

            public IAsyncResult BeginOpen(AsyncCallback callback, object state)
            {
                throw new NotImplementedException();
            }

            public IAsyncResult BeginOpen(TimeSpan timeout, AsyncCallback callback, object state)
            {
                throw new NotImplementedException();
            }

            public void EndOpen(IAsyncResult result)
            {
                throw new NotImplementedException();
            }

            public CommunicationState State
            {
                get { return _state; }
                private set { _state = value; }
            }

            public event EventHandler Closed;
            public event EventHandler Closing;
            public event EventHandler Faulted;
            public event EventHandler Opened;
            public event EventHandler Opening;
            public T GetProperty<T>() where T : class
            {
                throw new NotImplementedException();
            }

            public IExtensionCollection<IContextChannel> Extensions { get; private set; }
            public bool AllowOutputBatching { get; set; }
            public IInputSession InputSession { get; private set; }
            public System.ServiceModel.EndpointAddress LocalAddress { get; private set; }
            public TimeSpan OperationTimeout
            {
                get { return _operationTimeout; }
                set { _operationTimeout = value; }
            }

            public IOutputSession OutputSession { get; private set; }
            public System.ServiceModel.EndpointAddress RemoteAddress { get; private set; }
            public string SessionId { get; private set; }
        }


        public RpcProxyRouter(Uri address, Type typeOfService, Binding binding, bool callback = false, InstanceContext context = null)
            : this(address.ToString(), typeOfService, binding, callback, context)
        {

        }




        public IEnumerable<Type> GetRequiredInterfaces()
        {
            return Type.EmptyTypes;
        }

        public object Channell
        {
            get { return _remote; }
        }


        public void Close(TimeSpan closeTimeout)
        {
            _operationPending.WaitOne(closeTimeout);
            if (_context != null)
            {
                _context.Dispose();
                _context = null;
            }
            if (_client != null)
            {
                _client.Dispose();
                _client = null;
            }

        }

        public void Dispose()
        {
            Close(_binding.CloseTimeout);
        }
    }



}
