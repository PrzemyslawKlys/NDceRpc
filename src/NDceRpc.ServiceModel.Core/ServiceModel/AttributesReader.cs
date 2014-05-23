using System;
using System.Linq;
using System.Reflection;
using System.Runtime.CompilerServices;


namespace NDceRpc.ServiceModel
{
    internal class AttributesReader
    {

        private static string wcfNs = "System.ServiceModel.";
        private static string wcfService = wcfNs + "ServiceContract";

        public static ServiceContractAttribute GetServiceContract(Type contractType)
        {
            var attrs = contractType.GetCustomAttributes(false);
            var attr = attrs.Where(x => x.GetType() == typeof (ServiceContractAttribute)).SingleOrDefault();

            if (attr == null)
            {
                attr = attrs.Where(x => x.GetType().FullName.StartsWith(wcfService)).SingleOrDefault();
                attr = wcfServiceToCustom(attr);
            }
            return attr as ServiceContractAttribute;
        }

        [MethodImpl(MethodImplOptions.NoInlining)]
        private static object wcfServiceToCustom(object attr)
         {
             var wcf = (System.ServiceModel.ServiceContractAttribute) attr;
             return new ServiceContractAttribute
             {
                 CallbackContract = wcf.CallbackContract,
                 ConfigurationName = wcf.ConfigurationName,
                 Name = wcf.Name,
                 Namespace = wcf.Namespace,
                 ProtectionLevel = wcf.ProtectionLevel,
                 SessionMode = (SessionMode) (int) wcf.SessionMode
             };
         }

       
      


        public static OperationContractAttribute GetOperationContract(MethodInfo info)
        {
            return GetCustomAttribute<OperationContractAttribute>(info); ;
        }

        public static ServiceBehaviorAttribute GetServiceBehavior(object service)
        {
            return GetCustomAttribute<ServiceBehaviorAttribute>(service.GetType());
        }


        public static CallbackBehaviorAttribute GetCallbackBehavior(Type type)
        {
            return GetCustomAttribute<CallbackBehaviorAttribute>(type);
        }

        public static T GetCustomAttribute<T>(Type type)
        {
            return (T)(type.GetCustomAttributes(typeof(T), false).SingleOrDefault());
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
