using System;
using System.Diagnostics;
using System.Linq;
using System.ServiceModel;

namespace NDceRpc.ServiceModel
{
    public sealed class ServiceHost :ServiceHostBase
    {


        private ServiceBehaviorAttribute _behaviour= new ServiceBehaviorAttribute();
   

        public ServiceHost(Type service,params Uri[] baseAddresses)
            : this(Activator.CreateInstance(service), baseAddresses)
        {
            //TODO: make it not singleton
        }

        [Obsolete("Wcf has not such constuctor")]
        public ServiceHost(object service, Uri baseAddress)
            : this(service, baseAddress.ToString())
        {
        }

        [Obsolete("Wcf has not such constuctor")]
        public ServiceHost(object service, string baseAddress)
        {
            _baseAddress = new Uri(baseAddress,UriKind.Absolute);
            Reflect(service);
        }

        public ServiceHost(object service,params Uri[] baseAddress)
        {
            if (baseAddress == null)
                throw new ArgumentNullException("baseAddress");
            if (baseAddress.Length > 1)
                throw new NotImplementedException("Can you only one base address for now");
            _baseAddress = new Uri(baseAddress.First().ToString(), UriKind.Absolute);
            Reflect(service);
        }

        private void Reflect(object service)
        {
            var serviceBehaviour =
                service.GetType().GetCustomAttributes(typeof (ServiceBehaviorAttribute), false).SingleOrDefault() as
                ServiceBehaviorAttribute;
            if (serviceBehaviour != null) _behaviour = serviceBehaviour;
            if (service == null) throw new ArgumentNullException("service");
            _service = service;
            _concurrency = _behaviour.ConcurrencyMode;
        }

        public ServiceEndpoint AddServiceEndpoint(Type contractType, Binding binding, string address)
        {
            
            var uri = new Uri(address, UriKind.RelativeOrAbsolute);
            if (!uri.IsAbsoluteUri)
            {
                address = _baseAddress + address;
            }
            var uuid = EndpointMapper.CreateUuid(address, contractType);
            bool expectDuplexInitialization = false;
            var service = contractType.GetCustomAttributes(typeof(ServiceContractAttribute), false).SingleOrDefault() as ServiceContractAttribute;
            if (service.CallbackContract != null)
            {
                expectDuplexInitialization = true;
            }
            RpcTrace.TraceEvent(TraceEventType.Start, "Start adding service endpoint for {0} at {1}",contractType,address);
            var endpoint = base.CreateEndpoint(contractType, binding, address, uuid);
            _serverStub.Add(new RpcEndpointDispatcher(_service, endpoint, expectDuplexInitialization));
            return endpoint;
        }
    }
}