using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Reflection;
using System.ServiceModel;
using System.Threading;
using NDceRpc.ExplicitBytes;
using NDceRpc.ServiceModel.Channels;
using NDceRpc.ServiceModel.Custom;

namespace NDceRpc.ServiceModel
{


    public class RpcEndpointDispatcher : EndpointDispatcher
    {

        //private readonly EndpointBindingInfo _address;
        private readonly bool _duplex;
        private readonly SynchronizationContext _syncContext;

        private Dictionary<string, RpcCallbackChannelFactory> _clients = new Dictionary<string, RpcCallbackChannelFactory>();
        private OperationContext _noOp = new OperationContext();

        //private ManualResetEvent _opened = new ManualResetEvent(false);
        protected IExplicitBytesServer _host;
        private ManualResetEvent _operationPending = new ManualResetEvent(true);
        private ConcurrencyMode _concurrency;


        public RpcEndpointDispatcher(object singletonService, ServiceEndpoint endpoint, bool duplex = false, SynchronizationContext syncContext = null)
            : base(singletonService, endpoint)
        {
            _duplex = duplex;
            _syncContext = syncContext;
        }

        private Message InvokeContract(IRpcCallInfo call, MessageRequest request, Type contractType)
        {
            SetupOperationConext(call, request, contractType);

            OperationDispatchBase operation = _operations[request.Operation];

            var args = deserializeMessageArguments(request, operation);
            if (operation is AsyncOperationDispatch)
            {
                args.Add(null);//AsyncCallback
                args.Add(null);//object asyncState
            }
            var response = new Message();
            try
            {
                var result = InvokeServerMethod(operation, args);
                EnrichResponceWithReturn(operation, result, response);
            }
            catch (Exception ex)
            {
                response.Fault = new FaultData() {Code = ex.GetType().GUID.ToString(),Reason = ex.Message,Detail = ex.ToString(),Name = ex.GetType().FullName,Node = _endpoint._address};
            }
            finally
            {
                OperationContext.Current = _noOp;
            }

            return response;
        }

        internal void Open(ConcurrencyMode concurrency)
        {
            _concurrency = concurrency;
            //Action open = delegate
            {
                try
                {
                    if (_host == null)
                    {
                        RpcExecuteHandler onExecute =
                            delegate(IRpcCallInfo client, byte[] arg)
                            {
                                if (_concurrency == ConcurrencyMode.Single)
                                {
                                    lock (this)
                                    {
                                        _operationPending.Reset();
                                        try
                                        {
                                            return Invoke(client, _endpoint._contractType, arg);
                                        }
                                        finally
                                        {
                                            _operationPending.Set();
                                        }
                                    }
                                }
                                if (_concurrency == ConcurrencyMode.Multiple)
                                {
                                    //BUG: need have collection of operations because second operation rewrites state of first
                                    _operationPending.Reset();
                                    try
                                    {
                                        return Invoke(client, _endpoint._contractType, arg);
                                    }
                                    finally
                                    {
                                        _operationPending.Set();
                                    }
                                }

                                throw new NotImplementedException(
                                    string.Format("ConcurrencyMode {0} is note implemented", _concurrency));
                            };
                        _host = TransportFactory.CreateHost(_endpoint._binding, _endpoint._address, _endpoint._uuid);


                        _host.OnExecute += onExecute;
                        _host.StartListening();
                    }
                    //_opened.Set();
                }
                catch (Exception ex)
                {
                    bool handled = ExceptionHandler.AlwaysHandle.HandleException(ex);
                    if (!handled) throw;
                }
            };
            //Tasks.Factory.StartNew(open);
            //_opened.WaitOne();
        }

        public byte[] Invoke(IRpcCallInfo call, Type contractType, byte[] arg)
        {
            var messageRequest = (MessageRequest)ProtobufMessageEncodingBindingElement.ReadObject(new MemoryStream(arg), typeof(MessageRequest));
            Message response = InvokeContract(call, messageRequest, contractType);
            var stream = new MemoryStream();
            ProtobufMessageEncodingBindingElement.WriteObject(stream, response);
            return stream.ToArray();
        }

        private void EnrichResponceWithReturn(OperationDispatchBase operation, object result, Message response)
        {
            if (operation.MethodInfo.ReturnType != typeof(void) && operation.GetType() != typeof(AsyncOperationDispatch))
            {
                var stream = new MemoryStream();
                _endpoint._binding.Serializer.WriteObject(stream, result);
                response.Data = stream.ToArray();
            }
        }

        private object InvokeServerMethod(OperationDispatchBase operation, List<object> args)
        {
            object result = null;
            if (_syncContext != null)
            {
                _syncContext.Send(
                    _ =>
                    result = operation.MethodInfo.Invoke(_singletonService, BindingFlags.Public, null, args.ToArray(), null),
                    null);
            }
            else
            {
                result = operation.MethodInfo.Invoke(_singletonService, BindingFlags.Public, null, args.ToArray(), null);
            }
            return result;
        }

        private List<object> deserializeMessageArguments(MessageRequest request, OperationDispatchBase operation)
        {
            var args = new List<object>(operation.Params.Count);
            for (int i = 0; i < operation.Params.Count; i++)
            {
                RpcParamData pData = request.Data[i];
                var map = operation.Params[pData.Identifier];
                var type = map.Info.ParameterType;
                var obj = _endpoint._binding.Serializer.ReadObject(new MemoryStream(pData.Data), type);
                args.Add(obj);
            }
            return args;
        }

        private void SetupOperationConext(IRpcCallInfo call, MessageRequest request, Type contractType)
        {
            OperationContext.Current = new OperationContext { SessionId = request.Session };

            if (request.Session != null)
            {
                if (_duplex)
                {
                    lock (_clients)
                    {
                        RpcCallbackChannelFactory channelFactory = null;
                        bool contains = _clients.TryGetValue(request.Session, out channelFactory);
                        if (!contains)
                        {
                            var contract =
                                contractType.GetCustomAttributes(typeof(ServiceContractAttribute), false).Single() as
                                ServiceContractAttribute;
                            channelFactory = new RpcCallbackChannelFactory(_endpoint._binding,
                                                                           contract.CallbackContract, new Guid(request.Session),
                                                                           true);
                            _clients[request.Session] = channelFactory;
                        }
                        var callbackBindingInfo = EndpointMapper.WcfToRpc(_endpoint._address);
                        if (!call.IsClientLocal)
                        {
                            //BUG: callbacks accross network does not work
                            //callbackAddress.NetworkAddr =  call.ClientAddress    
                        }

                        callbackBindingInfo.EndPoint += request.Session.Replace("-", "");
                        OperationContext.Current.SetGetter(_clients[request.Session], EndpointMapper.RpcToWcf(callbackBindingInfo));
                    }
                }
            }
        }

        internal void Dispose(TimeSpan CloseTimeout)
        {
            if (_host != null)
            {
                _operationPending.WaitOne(CloseTimeout);
                _host.Dispose();
                _host = null;
            }
        }
    }


}
