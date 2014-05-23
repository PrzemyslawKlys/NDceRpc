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
            return (ServiceContractAttribute)contractType.GetCustomAttributes(typeof(ServiceContractAttribute), false).SingleOrDefault();
        }


        public static OperationContractAttribute GetOperationContract(MethodInfo info)
        {
            return (OperationContractAttribute)info.GetCustomAttributes(typeof(OperationContractAttribute), false).SingleOrDefault();
        }

        public static ServiceBehaviorAttribute GetServiceBehavior(object service)
        {
            return (ServiceBehaviorAttribute)service.GetType().GetCustomAttributes(typeof(ServiceBehaviorAttribute), false).SingleOrDefault();
        }


        public static CallbackBehaviorAttribute GetCallbackBehavior(Type type)
        {
            return (CallbackBehaviorAttribute)type.GetCustomAttributes(typeof(CallbackBehaviorAttribute), false).SingleOrDefault();
        }

        public static T GetCustomAttribute<T>(MethodBase methodInfo)
        {
            return (T)(methodInfo.GetCustomAttributes(typeof(T), false).SingleOrDefault());
        }

        public static bool IsOperationContract(MethodInfo x)
        {
            return x.GetCustomAttributes(typeof(OperationContractAttribute), true).Length != 0;
        }
    }
}
