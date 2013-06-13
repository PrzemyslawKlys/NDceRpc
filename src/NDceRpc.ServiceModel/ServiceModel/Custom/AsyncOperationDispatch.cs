using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Reflection;
using System.ServiceModel;

namespace NDceRpc.ServiceModel
{
    public class AsyncOperationDispatch : OperationDispatchBase
    {
    
 
        private MethodInfo _endAsync;

        public MethodInfo EndAsync
        {
            get { return _endAsync; }
        }


        public AsyncOperationDispatch(OperationContractAttribute operation, MethodInfo methodInfo):base(methodInfo)
        {

            Operation = operation;
            ParameterInfo[] allParameters = methodInfo.GetParameters();
            var serializedParameters = allParameters;
            if (Operation.AsyncPattern)
            {
                var isAsyncResultPattern =
                    methodInfo.Name.StartsWith("Begin", StringComparison.InvariantCultureIgnoreCase)
                    && allParameters.Last().ParameterType == typeof(object)
                    && allParameters.Skip(allParameters.Length - 1).Last().ParameterType == typeof(AsyncCallback)
                    && methodInfo.ReturnType == typeof(IAsyncResult);
                if (isAsyncResultPattern)
                {
                    var endName = methodInfo.Name.Replace("Begin", "End");
                    _endAsync = methodInfo.DeclaringType.GetMethod(endName);
                    Debug.Assert(_endAsync.ReturnType == typeof(void));
                }
                serializedParameters = allParameters.Take(allParameters.Length - 2).ToArray();
            }

            SetIdentifier(methodInfo);
           
            foreach (var p in serializedParameters)
            {
                var map = new ParameterDispatch(p);
                _params.Add(map.Identifier, map);
            }

        }

  
    }
}