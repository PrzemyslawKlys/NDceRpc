using System.Collections.Generic;

namespace NDceRpc.ServiceModel
{
    public abstract class RpcStub
    {
        protected object _singletonService;
        protected Dictionary<int, OperationDispatchBase> _operations = new Dictionary<int, OperationDispatchBase>();
        protected BinaryObjectSerializer _serializer;
        protected Binding _binding;

        public Dictionary<int, OperationDispatchBase> Operations
        {
            get { return _operations; }
        }

        protected RpcStub(object singletonService, Binding binding)
        {
            _serializer = binding.Serializer;
            _binding = binding;
            _singletonService = singletonService;
            //TODO: use only contract added and its interfaces, not all of type
            var ops = TypeExtensions.GetAllServiceImplmentations(singletonService.GetType());
            _operations = _operations = DispatchFactory.CreateOperations(ops);
        }
    }
}