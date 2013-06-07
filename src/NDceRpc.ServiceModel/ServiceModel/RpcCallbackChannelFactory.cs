using System;

namespace NDceRpc.ServiceModel
{
    public class RpcCallbackChannelFactory
    {
        private Binding _binding;
        private Type _type;
        private readonly Guid _session;
        private readonly bool _callback;
        private RpcProxy _client;

        public RpcCallbackChannelFactory(Binding binding, Type typeOfService,Guid session, bool callback = false)
        {
            _type = typeOfService;
            _session = session;
            _callback = callback;
            _binding = binding;
        }

        public TService CreateChannel<TService>(EndpointAddress createEndpoint)
        {
            if (_client == null)
                _client = new RpcProxy(createEndpoint.Uri, _type, _binding, false, null, _session);
            return (TService)_client.Channell;
        }

        public void Dispose()
        {
            _client.Dispose();
        }
    }
}