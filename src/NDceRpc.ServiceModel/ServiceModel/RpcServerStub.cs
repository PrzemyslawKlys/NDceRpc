using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Reflection;
using System.ServiceModel;
using NDceRpc.ServiceModel.Channels;

namespace NDceRpc.ServiceModel
{
    public class RpcServerStub : RpcStub
    {
        private readonly Type _contractType;
        private readonly EndpointBindingInfo _address;
        private readonly bool _duplex;
 
        private Dictionary<string, RpcCallbackChannelFactory> _clients = new Dictionary<string, RpcCallbackChannelFactory>();
        private OperationContext _noOp = new OperationContext();


        public RpcServerStub(object singletonService, EndpointBindingInfo address, Binding binding, bool duplex = false)
            : base(singletonService, binding)
        {
            _address = address;
            _duplex = duplex;
        }

        public MessageResponse Invoke(IRpcCallInfo call,MessageRequest request, Type contractType)
        {
            SetupOperationConext(call,request, contractType);

            OperationDispatchBase operation = _operations[request.Operation];
            var args = new List<object>(operation.Params.Count);
            for (int i = 0; i < operation.Params.Count; i++)
            {
                RpcParamData pData = request.Data[i];
                var map = operation.Params[pData.Identifier];
                var type = map.Info.ParameterType;
                var obj = _serializer.ReadObject(new MemoryStream(pData.Data), type);
                args.Add(obj);
            }
            if (operation is AsyncOperationDispatch)
            {
                args.Add(null);//AsyncCallback
                args.Add(null);//object asyncState
            }
            var response = new MessageResponse();
            try
            {
                object result = operation.MethodInfo.Invoke(_singletonService, BindingFlags.Public, null, args.ToArray(),null);
                if (operation.MethodInfo.ReturnType != typeof (void) && operation.GetType() != typeof(AsyncOperationDispatch))
                {
                    var stream = new MemoryStream();
                    _serializer.WriteObject(stream, result);
                    response.Data = stream.ToArray();
                }
            }
            catch (Exception ex)
            {
                response.Error = new RpcErrorData {Type = ex.GetType().FullName, Message = ex.ToString()};
            }
            finally
            {
                OperationContext.Current = _noOp;
            }
          
            return response;
        }

        private void SetupOperationConext(IRpcCallInfo call,MessageRequest request, Type contractType)
        {
            OperationContext.Current = new OperationContext();

            OperationContext.Current.SessionId = request.Session;
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
                                contractType.GetCustomAttributes(typeof (ServiceContractAttribute), false).Single() as
                                ServiceContractAttribute;
                            channelFactory = new RpcCallbackChannelFactory(_binding,
                                                                           contract.CallbackContract, new Guid(request.Session),
                                                                           true);
                            _clients[request.Session] = channelFactory;
                        }
                        var callbackBindingInfo= (EndpointBindingInfo)_address.Clone() ;
                        if (!call.IsClientLocal)
                        {
                            //BUG: callbacks accross network does not work
                            //callbackAddress.NetworkAddr =  call.ClientAddress    
                        }

                        callbackBindingInfo.EndPoint += request.Session.Replace("-","");
                        OperationContext.Current.SetGetter(_clients[request.Session], EndpointMapper.RpcToWcf(callbackBindingInfo));
                    }
                }
            }
        }
    }


}
