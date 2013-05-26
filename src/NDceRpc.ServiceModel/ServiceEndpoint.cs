using System;

namespace NDceRpc.ServiceModel
{
    public class ServiceEndpoint
    {
        internal readonly Binding _binding;
        internal readonly Type _contractType;
        internal readonly string _address;
        internal readonly Guid _uuid;
        internal BinaryObjectSerializer _serializer;

        public ServiceEndpoint(Binding binding, Type contractType, string address, Guid uuid)
        {
            _binding = binding;
            _serializer = _binding.Serializer;
            _contractType = contractType;
            _address = address;
            _uuid = uuid;
        }
    }
}