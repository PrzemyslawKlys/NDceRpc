using NDceRpc.ServiceModel;

namespace NDceRpc.Description
{
    public interface IEndpointBehavior
    {
        void ApplyDispatchBehavior(ServiceEndpoint endpoint, EndpointDispatcher endpointDispatcher);

        void ApplyClientBehavior(ServiceEndpoint endpoint, ClientRuntime clientRuntime);
    }
}