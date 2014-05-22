using System;
using System.Collections.Generic;
using System.Diagnostics.Contracts;
using System.Linq;
using System.Reflection;
using System.Runtime.InteropServices;
using NDceRpc.ServiceModel.Custom;

namespace NDceRpc.ServiceModel
{
    internal static class DispatchTableFactory
    {
        /// like http://msdn.microsoft.com/en-us/library/windows/desktop/aa367040.aspx
        internal const int DEFAULT_ID_SHIFT = 0x60020000;

        private static Dictionary<Type, DispatchTable> _cache = new Dictionary<Type, DispatchTable>();
        private static Dictionary<Type, DispatchTable> Cache
        {
            get { return _cache; }
            set { _cache = value; }
        }

        private static OperationDispatchBase create(MethodInfo info, int identifier)
        {
            //BUG: fix not null async params
            //BUG: add all async patterns
            //BUG: fix WCF ref params
            var operation = AttributesReader.GetOperationContract(info);
            if (!operation.AsyncPattern)
            {
                return new OperationDispatch(operation, info, identifier);
            }
            else
            {
                return new AsyncOperationDispatch(operation, info, identifier);
            }
        }

  

        private static int createIdentifier(MethodInfo info, int orderNumber)
        {
            int identifier = DEFAULT_ID_SHIFT + orderNumber;
            var dispatchId = AttributesReader.GetCustomAttribute<DispIdAttribute>(info);
            if (dispatchId != null)
            {
                identifier = dispatchId.Value;
            }
            return identifier;
        }

        private static DispatchTable createOperations(MethodInfo[] ops)
        {
           Contract.Ensures(Contract.Result<DispatchTable>().IdToOperation.Count == Contract.Result<DispatchTable>().TokenToOperation.Count);
           var operations = new DispatchTable();
           for (int orderNumber = 0; orderNumber < ops.Length; orderNumber++)
            {
                var methodInfo = ops[orderNumber];
                var identifier = createIdentifier(methodInfo, orderNumber);
                OperationDispatchBase operation = create(methodInfo, identifier);
                operations.IdToOperation[identifier] = operation;
                operations.TokenToOperation[methodInfo.MetadataToken] = operation;
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
        /// Considers reflection costly operation and caching is optimization.  Considers types unchangeable during runtime.
        /// </remarks>
        public static DispatchTable GetOperations(Type type)
        {
            DispatchTable table;
 
            if (!Cache.TryGetValue(type, out table))
            {
                var ops = DipatchTableHelper.GetAllServiceImplementations(type);
                var newTable = createOperations(ops);

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