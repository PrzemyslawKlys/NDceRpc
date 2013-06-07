using System.Reflection;
using System.Runtime.InteropServices;
using System.ServiceModel;

namespace NDceRpc.ServiceModel
{
    /// <summary>
    /// Maps method binary identifiers into invocable entities.
    /// </summary>
    internal class OperationDispatch : OperationDispatchBase
    {

        public OperationDispatch(OperationContractAttribute operation,MethodInfo methodInfo):base(methodInfo)
        {
            Operation = operation;
            var allParameters = methodInfo.GetParameters();
           
            SetIdentifier(methodInfo);

            foreach (var p in allParameters)
            {
                var map = new ParameterDispatch(p);
                _params.Add(map.Identifier, map);
            }

        }

 
    }
}