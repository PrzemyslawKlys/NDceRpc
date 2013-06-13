using System;
using System.Linq;
using System.ServiceModel;

namespace NDceRpc.ServiceModel
{

    internal sealed class CallbackServiceHost : ServiceHostBase
    {
        private readonly InstanceContext _serviceCtx;


        private CallbackBehaviorAttribute _behaviour = new CallbackBehaviorAttribute();


        //public CallbackServiceHost(Type service, Uri baseAddress,)
        //    : this(Activator.CreateInstance(service), baseAddress.ToString())
        //{
        //    //TODO: make it not singleton
        //}

        public CallbackServiceHost(InstanceContext serviceCtx, Uri baseAddress)
            : this(serviceCtx, baseAddress.ToString())
        {
        }

        public CallbackServiceHost(InstanceContext serviceCtx, string baseAddress)
        {
            _serviceCtx = serviceCtx;
            if (serviceCtx == null) throw new ArgumentNullException("serviceCtx");
            _baseAddress = new Uri(baseAddress,UriKind.Absolute);
            var serviceBehaviour = serviceCtx._contextObject.GetType().GetCustomAttributes(typeof(CallbackBehaviorAttribute), false).SingleOrDefault() as CallbackBehaviorAttribute;
            if (serviceBehaviour != null) _behaviour = serviceBehaviour;
   
            base._concurrency = _behaviour.ConcurrencyMode;
            _service = serviceCtx._contextObject;
        }
  
        public ServiceEndpoint AddServiceEndpoint(Type contractType, Guid uuid, Binding binding, string address)
        {
            var uri = new Uri(address, UriKind.RelativeOrAbsolute);
            if (!uri.IsAbsoluteUri)
            {
                address = _baseAddress + address;
            }
            EndpointBindingInfo bindingInfo = EndpointMapper.WcfToRpc(address);
            _serverStub = new RpcServerStub(_service, bindingInfo, binding,false, _serviceCtx.SynchronizationContext);

            return AddEndpoint(contractType, binding, address, uuid);
        }

        public void Close()
        {
            this.Dispose();
        }
    }
}