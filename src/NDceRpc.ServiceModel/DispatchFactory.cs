using System.Collections.Generic;
using System.Linq;
using System.Reflection;
using System.ServiceModel;

namespace NDceRpc.ServiceModel
{
    public static class DispatchFactory
    {
        public static OperationDispatchBase Create(MethodInfo info)
        {
            //TODO: fix not null async params
            //TODO: add all async patterns
            //TODO: fix WCF ref params
            var operation = (OperationContractAttribute)info.GetCustomAttributes(typeof(OperationContractAttribute), false).Single();
            if (!operation.AsyncPattern)
            {
                return new OperationDispatch(operation, info);
            }
            else
            {
                return new AsyncOperationDispatch(operation,info);
            }
        }

        public static Dictionary<int, OperationDispatchBase> CreateOperations(MethodInfo[] ops)
        {
            Dictionary<int, OperationDispatchBase> operations= new Dictionary<int, OperationDispatchBase>();
            foreach (var methodInfo in ops)
            {
                OperationDispatchBase operation = DispatchFactory.Create(methodInfo);
                operations[operation.Identifier] = operation;
            }
            return operations;
        }
    }
}