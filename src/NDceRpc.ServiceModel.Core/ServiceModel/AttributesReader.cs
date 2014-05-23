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
        private static string wcfServiceBehavior = wcfNs + "ServiceBehavior";
        private static string wcfCallbackBehavior = wcfNs + "CallbackBehavior";
        private static string wcfOperation = wcfNs + "OperationContract";

        

        public static ServiceContractAttribute GetServiceContract(Type contractType)
        {
            return getAttr<ServiceContractAttribute>(contractType, wcfService, wcfServiceToCustom);
        }

        [MethodImpl(MethodImplOptions.NoInlining)]
        private static ServiceContractAttribute wcfServiceToCustom(object attr)
        {
            var wcf = (System.ServiceModel.ServiceContractAttribute)attr;
            return new ServiceContractAttribute
            {
                CallbackContract = wcf.CallbackContract,
                ConfigurationName = wcf.ConfigurationName,
                Name = wcf.Name,
                Namespace = wcf.Namespace,
                ProtectionLevel = wcf.ProtectionLevel,
                SessionMode = (SessionMode)wcf.SessionMode

            };
        }


        public static OperationContractAttribute GetOperationContract(MethodInfo info)
        {
            return getAttr<OperationContractAttribute>(info, wcfOperation, wcfOperationToCustom);
        }

        [MethodImpl(MethodImplOptions.NoInlining)]
        private static OperationContractAttribute wcfOperationToCustom(object attr)
        {
            var wcf = (System.ServiceModel.OperationContractAttribute)attr;
            return new OperationContractAttribute
            {
                Action = wcf.Action,
                AsyncPattern = wcf.AsyncPattern,
                IsInitiating = wcf.IsInitiating,
                IsOneWay = wcf.IsOneWay,
                IsTerminating = wcf.IsTerminating,
                Name = wcf.Name,
                ProtectionLevel = wcf.ProtectionLevel,
                ReplyAction = wcf.ReplyAction

            };
        }



        public static ServiceBehaviorAttribute GetServiceBehavior(object service)
        {
            return getAttr<ServiceBehaviorAttribute>(service.GetType(), wcfServiceBehavior, wcfServiceBehaviorToCustom);
        }

        [MethodImpl(MethodImplOptions.NoInlining)]
        private static ServiceBehaviorAttribute wcfServiceBehaviorToCustom(object attr)
        {
            var wcf = (System.ServiceModel.ServiceBehaviorAttribute)attr;
            return new ServiceBehaviorAttribute
            {
                AddressFilterMode = (AddressFilterMode)wcf.AddressFilterMode,
                ConcurrencyMode = (ConcurrencyMode)wcf.ConcurrencyMode,
                AutomaticSessionShutdown = wcf.AutomaticSessionShutdown,
                ConfigurationName = wcf.ConfigurationName,
                IgnoreExtensionDataObject = wcf.IgnoreExtensionDataObject,
                IncludeExceptionDetailInFaults = wcf.IncludeExceptionDetailInFaults,
                InstanceContextMode = (InstanceContextMode)wcf.InstanceContextMode,
                MaxItemsInObjectGraph = wcf.MaxItemsInObjectGraph,
                Name = wcf.Name,
                Namespace = wcf.Name,
                ReleaseServiceInstanceOnTransactionComplete = wcf.ReleaseServiceInstanceOnTransactionComplete,
                TransactionAutoCompleteOnSessionClose = wcf.TransactionAutoCompleteOnSessionClose,
                TransactionTimeout = wcf.TransactionTimeout,
                TransactionIsolationLevel = wcf.TransactionIsolationLevel,
                UseSynchronizationContext = wcf.UseSynchronizationContext,
                ValidateMustUnderstand = wcf.ValidateMustUnderstand
            };
        }

        public static CallbackBehaviorAttribute GetCallbackBehavior(Type type)
        {
            return getAttr<CallbackBehaviorAttribute>(type, wcfCallbackBehavior, wcfCallbackBehaviorToCustom);
        }

        [MethodImpl(MethodImplOptions.NoInlining)]
        private static CallbackBehaviorAttribute wcfCallbackBehaviorToCustom(object attr)
        {
            var wcf = (System.ServiceModel.CallbackBehaviorAttribute)attr;
            return new CallbackBehaviorAttribute
            {
                ConcurrencyMode = (ConcurrencyMode)wcf.ConcurrencyMode,
                AutomaticSessionShutdown = wcf.AutomaticSessionShutdown,
                IgnoreExtensionDataObject = wcf.IgnoreExtensionDataObject,
                IncludeExceptionDetailInFaults = wcf.IncludeExceptionDetailInFaults,
                MaxItemsInObjectGraph = wcf.MaxItemsInObjectGraph,
                TransactionTimeout = wcf.TransactionTimeout,
                TransactionIsolationLevel = wcf.TransactionIsolationLevel,
                UseSynchronizationContext = wcf.UseSynchronizationContext,
                ValidateMustUnderstand = wcf.ValidateMustUnderstand
            };
        }

        public static T GetCustomAttribute<T>(MethodBase methodInfo)
        {
            return (T)(methodInfo.GetCustomAttributes(typeof(T), false).SingleOrDefault());
        }

        public static bool IsOperationContract(MethodInfo x)
        {
            return GetOperationContract(x) != null;
        }

        private static T getAttr<T>(ICustomAttributeProvider provider, string wcfFallback, Func<object, T> map)
           where T : class
        {
            var attrs = provider.GetCustomAttributes(false);
            var attr = attrs.Where(x => x.GetType() == typeof(T)).SingleOrDefault();

            if (attr == null)
            {
                attr = attrs.Where(x => x.GetType().FullName.StartsWith(wcfFallback)).SingleOrDefault();
                if (attr != null)
                    attr = map(attr);
            }
            return attr as T;
        }
    }
}
