using System;
using System.Linq;
using System.ServiceModel;

namespace NDceRpc.ServiceModel
{

    internal sealed class CallbackServiceHost : ServiceHostBase
    {


        private CallbackBehaviorAttribute _behaviour = new CallbackBehaviorAttribute();


        public CallbackServiceHost(Type service, Uri baseAddress)
            : this(Activator.CreateInstance(service), baseAddress.ToString())
        {
            //TODO: make it not singleton
        }

        public CallbackServiceHost(object service, Uri baseAddress)
            : this(service, baseAddress.ToString())
        {
        }

        public CallbackServiceHost(object service, string baseAddress)
        {
            _baseAddress = new Uri(baseAddress,UriKind.Absolute);
            var serviceBehaviour = service.GetType().GetCustomAttributes(typeof(CallbackBehaviorAttribute), false).SingleOrDefault() as CallbackBehaviorAttribute;
            if (serviceBehaviour != null) _behaviour = serviceBehaviour;
            if (service == null) throw new ArgumentNullException("service");
            base._concurrency = _behaviour.ConcurrencyMode;
            _service = service;
        }
  
        public ServiceEndpoint AddServiceEndpoint(Type contractType, Guid uuid, Binding binding, string address)
        {
            var uri = new Uri(address, UriKind.RelativeOrAbsolute);
            if (!uri.IsAbsoluteUri)
            {
                address = _baseAddress + address;
            }
            EndpointBindingInfo bindingInfo = EndpointMapper.WcfToRpc(address);
            _serverStub = new RpcServerStub(_service, bindingInfo, binding);

            return AddEndpoint(contractType, binding, address, uuid);
        }

        public void Close()
        {
            this.Dispose();
        }
    }
}