using System;
using System.Collections.Generic;
using System.Linq;
using System.Reflection;
using System.Text;

namespace NDceRpc.ServiceModel
{
    internal class AttributesReader
    {
        public static ServiceContractAttribute GetServiceContract(Type contractType)
        {
            return contractType.GetCustomAttributes(typeof(ServiceContractAttribute), false).SingleOrDefault() as ServiceContractAttribute;
        }


        public static OperationContractAttribute GetOperationContract(MethodInfo info)
        {
            return (OperationContractAttribute)info.GetCustomAttributes(typeof(OperationContractAttribute), false).Single();
        }

        public static ServiceBehaviorAttribute GetServiceBehavior(object service)
        {
            return service.GetType().GetCustomAttributes(typeof(ServiceBehaviorAttribute), false).SingleOrDefault() as ServiceBehaviorAttribute;
        }


        public static CallbackBehaviorAttribute GetCallbackBehavior(Type type)
        {
            return type.GetCustomAttributes(typeof(CallbackBehaviorAttribute), false).SingleOrDefault() as CallbackBehaviorAttribute;
        }

        public static T GetCustomAttribute<T>(MethodBase methodInfo)
        {
            return (T)(methodInfo.GetCustomAttributes(typeof(T), false).SingleOrDefault());
        }

        public static bool IsOperation(MethodInfo x)
        {
            return x.GetCustomAttributes(typeof(OperationContractAttribute), true).Length != 0;
        }
    }
}
