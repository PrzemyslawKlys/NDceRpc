using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.IO;
using System.Linq;
using System.Reflection;
using System.Runtime.Remoting.Messaging;
using System.Runtime.Remoting.Proxies;
using System.ServiceModel;
using System.ServiceModel.Channels;
using System.Threading;
using System.Threading.Tasks;

using NDceRpc.ExplicitBytes;
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

            _operations = DispatchFactory.GetOperations(_typeOfService);


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

        internal class RpcRealProxy : RealProxy, System.Runtime.Remoting.IRemotingTypeInfo, IChannel, ICommunicationObject
        {


            private readonly Type _service;
            private readonly RpcProxyRouter _router;
            private string _typeName;

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
                        ps.Add(new RpcParamData { Identifier = paramIdentifier, Data = stream.ToArray() });
                    }
                    r.Data.AddRange(ps.ToArray());
                    var rData = new MemoryStream();
                    _router._serializer.WriteObject(rData, r);
                    if (op is AsyncOperationDispatch)
                    {
                        object asyncState = input.GetInArg(op.Params.Count + 1);
                        var task = Tasks.Factory.StartNew((x) =>
                        {
                            try
                            {
                                _router._operationPending.Reset();

                                byte[] result = null;


                                result = _router._client.Execute(rData.ToArray());


                                var response = (MessageResponse)ProtobufMessageEncodingBindingElement.ReadObject(new MemoryStream(result), typeof(MessageResponse));
                                if (response.Error != null)
                                {
                                    throw new Exception(response.Error.Type + response.Error.Message);
                                }
                            }
                            catch (Exception ex)
                            {
                                bool handled = ErrorHandler.Handle(ex);
                                if (!handled) throw;
                            }
                            finally
                            {
                                _router._operationPending.Set();
                            }
                        }, asyncState);

                        task.ContinueWith(x =>
                        {
                            var asyncCallback = (AsyncCallback)input.GetInArg(op.Params.Count);

                            if (asyncCallback != null)
                            {
                                asyncCallback(task);
                            }
                        });
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


                                result = _router._client.Execute(rData.ToArray());


                                var response = (MessageResponse)ProtobufMessageEncodingBindingElement.ReadObject(new MemoryStream(result), typeof(MessageResponse));
                                if (response.Error != null)
                                {
                                    return new ReturnMessage(new Exception(response.Error.Type + response.Error.Message), input);
                                }
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
                        );
                        return new ReturnMessage(null, null, 0, null, input);
                    }

                    try
                    {
                        _router._operationPending.Reset();

                        byte[] result = null;

                        result = _router._client.Execute(rData.ToArray());

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
                throw new InvalidOperationException(string.Format("Cannot find operation {0} on service {1}",input.MethodName,_service));
            
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
                    fromType == typeof (IChannel) || fromType == typeof (ICommunicationObject))
                {
                    //throw new NotImplementedException("Need to implement WCF channell contracts");
                    return false;//TODO: support these
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
                throw new NotImplementedException();
            }

            public void Close()
            {
                throw new NotImplementedException();
            }

            public void Close(TimeSpan timeout)
            {
                throw new NotImplementedException();
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

            public CommunicationState State { get; private set; }
            public event EventHandler Closed;
            public event EventHandler Closing;
            public event EventHandler Faulted;
            public event EventHandler Opened;
            public event EventHandler Opening;
            public T GetProperty<T>() where T : class
            {
                throw new NotImplementedException();
            }
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
