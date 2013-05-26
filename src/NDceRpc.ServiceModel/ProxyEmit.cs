using System;
using System.Linq;
using System.Reflection;
using System.Reflection.Emit;

namespace NDceRpc.ServiceModel
{
    public class ProxyEmit
    {
        private const string PREFIX = "RpcRuntime";

        //TODO: create fake and intercept using only IL
        public static Type CreateFake(Type service)
        {
            var assemblyName = new AssemblyName(PREFIX+"DynamicAssembly");
            AssemblyBuilder assembly = AppDomain.CurrentDomain.DefineDynamicAssembly(assemblyName, AssemblyBuilderAccess.Run);
            ModuleBuilder module = assembly.DefineDynamicModule(PREFIX+"EmittedModule");
            //infertype name and fix generics
            string typeName = service.Name.Replace("[", String.Empty)
                                     .Replace("]", string.Empty)
                                     .Replace("`", string.Empty);
                typeName+= DateTime.Now.Ticks;
            //TypeBuilder myHelloWorldType = module.DefineType(PREFIX + typeName, TypeAttributes.Public);

            TypeBuilder proxy =
               module.DefineType(PREFIX + typeName,
                           TypeAttributes.Public, typeof(Object),
                           new[] { service });

            // Implement 'IMyInterface' interface.
            proxy.AddInterfaceImplementation(service);

            var methods = TypeExtensions.GetAllImplementations(service);
            foreach (var methodInfo in methods)
            {
                // Define the HelloMethod of IMyInterface.
                MethodBuilder myHelloMethod = proxy.DefineMethod(methodInfo.Name,
                   MethodAttributes.Public | MethodAttributes.Virtual,
                   methodInfo.ReturnType, methodInfo.GetParameters().Select(x => x.ParameterType).ToArray());

                // Generate IL for the method.
                ILGenerator myMethodIL = myHelloMethod.GetILGenerator();//TODO: make use of RPC 
                myMethodIL.ThrowException(typeof(Exception));
                proxy.DefineMethodOverride(myHelloMethod, methodInfo);
            }


            Type proxyType = proxy.CreateType();


            return proxyType;
        }
    }
}
