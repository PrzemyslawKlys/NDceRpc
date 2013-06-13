using System;
using System.Runtime.InteropServices.ComTypes;
using System.ServiceModel;
using System.Threading;

namespace NDceRpc.ServiceModel
{
    /// <summary>
    /// Similar to WCF <seealso cref="System.ServiceModel.DuplexChannelFactory{TChannel}"/> and 
    /// COM <seealso cref="IConnectionPoint"/>
    /// </summary>
    /// <typeparam name="T"></typeparam>
    public class DuplexChannelFactory<T>:IDisposable
    {
        private readonly InstanceContext _context;
        private Binding _binding;
        private Type _type;
        private RpcProxyRouter _client;

        public DuplexChannelFactory(InstanceContext context, Binding binding)
        {
            _type = typeof (T);
            _context = context;
            _binding = binding;
        }

        public T CreateChannel(EndpointAddress createEndpoint)
        {
            if (_client == null)
                _client = new RpcProxyRouter(createEndpoint.Uri, _type, _binding, false, _context);
            return (T)_client.Channell;
        }

        public void Dispose()
        {
            _client.Dispose();
        }
    }
}