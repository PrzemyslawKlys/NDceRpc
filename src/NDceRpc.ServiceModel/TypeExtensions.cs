using System;
using System.Linq;
using System.Reflection;
using System.ServiceModel;

namespace NDceRpc.ServiceModel
{
    public static class TypeExtensions
    {
        public static MethodInfo[] GetAllImplementations<T>()
        {
            return GetAllImplementations(typeof (T));
        }

        public static MethodInfo[] GetAllImplementations(Type t)
        {
            var ofThis = t.GetMethods(BindingFlags.Public | BindingFlags.Instance);
            MethodInfo[] ops =
                ofThis.Union(
                    t.GetInterfaces().SelectMany(x => x.GetMethods(BindingFlags.Public | BindingFlags.Instance)))
                      .ToArray();
            return ops;
        }

        public static MethodInfo[] GetAllServiceImplementations<T>()
        {
            return GetAllServiceImplementations(typeof (T));
        }
        public static MethodInfo[] GetAllServiceImplementations(Type t)
        {
            return GetAllImplementations(t).Where(x => x.GetCustomAttributes(typeof (OperationContractAttribute),true).Length !=0 ).ToArray();
        }

        public static T GetCustomAttribute<T>(MethodInfo methodInfo)
        {
            return (T) (methodInfo.GetCustomAttributes(typeof (T), false).SingleOrDefault());
        }
    }
}