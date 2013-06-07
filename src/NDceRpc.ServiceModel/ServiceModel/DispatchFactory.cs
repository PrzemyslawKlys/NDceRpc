using System;
using System.Collections.Generic;
using System.Linq;
using System.Reflection;
using System.ServiceModel;

namespace NDceRpc.ServiceModel
{

    public class DispatchTable : Dictionary<int, OperationDispatchBase>{}

    public static class DispatchFactory
    {
        private static Dictionary<Type, DispatchTable> _cache = new Dictionary<Type, DispatchTable>();
        public static Dictionary<Type, DispatchTable> Cache
        {
            get { return _cache; }
            set { _cache = value; }
        }

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

        public static DispatchTable CreateOperations(MethodInfo[] ops)
        {
           var operations = new DispatchTable();
            foreach (var methodInfo in ops)
            {
                OperationDispatchBase operation = DispatchFactory.Create(methodInfo);
                operations[operation.Identifier] = operation;
            }
            return operations;
        }

        /// <summary>
        /// Gets table of operations for specific type.
        /// 
        /// </summary>
        /// <param name="type"></param>
        /// <returns></returns>
        /// <remarks>
        /// Considers reflection costly operation and caching is optimization.  Considers types unchangable during runtime.
        /// </remarks>
        public static DispatchTable GetOperations(Type type)
        {
            DispatchTable table;
 
            if (!Cache.TryGetValue(type, out table))
            {
                var ops = TypeExtensions.GetAllServiceImplementations(type);
                var newTable = CreateOperations(ops);

                lock(Cache)
                {
                    if (!Cache.TryGetValue(type, out table))
                    {
                        Cache[type] = newTable;
                        table = newTable;
                    }
                }
            }

            return table;
        }
    }
}