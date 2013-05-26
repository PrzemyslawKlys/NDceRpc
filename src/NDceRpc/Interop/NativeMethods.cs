using System;
using System.Runtime.InteropServices;

namespace NDceRpc.Interop
{
    /// <summary>
    /// 
    /// </summary>
    public static class NativeMethods
    {
        ///<summary>
        /// Validates the format of the string binding handle and converts
        /// it to a binding handle.
        /// Connection is not done here either.
        /// </summary>
        [DllImport("Rpcrt4.dll", EntryPoint = "RpcBindingFromStringBindingW",
    CallingConvention = CallingConvention.StdCall,
    CharSet = CharSet.Unicode, SetLastError = true)]
        public static extern RPC_STATUS RpcBindingFromStringBinding(String bindingString, out IntPtr lpBinding);

        [DllImport("Rpcrt4.dll", EntryPoint = "RpcBindingSetAuthInfoW", CallingConvention = CallingConvention.StdCall,
    CharSet = CharSet.Unicode, SetLastError = true)]
        public static extern RPC_STATUS RpcBindingSetAuthInfo(IntPtr Binding, String ServerPrincName,
                                                             RpcProtectionLevel AuthnLevel, RpcAuthentication AuthnSvc,
                                                             [In] ref SEC_WINNT_AUTH_IDENTITY AuthIdentity,
                                                             uint AuthzSvc);

        [DllImport("Rpcrt4.dll", EntryPoint = "RpcBindingSetAuthInfoW", CallingConvention = CallingConvention.StdCall,
            CharSet = CharSet.Unicode, SetLastError = true)]
        public static extern RPC_STATUS RpcBindingSetAuthInfo2(IntPtr Binding, String ServerPrincName,
                                                              RpcProtectionLevel AuthnLevel, RpcAuthentication AuthnSvc,
                                                              IntPtr p, uint AuthzSvc);

        [DllImport("Rpcrt4.dll", EntryPoint = "NdrClientCall2", CallingConvention = CallingConvention.Cdecl,
    CharSet = CharSet.Unicode, SetLastError = true)]
        public static extern IntPtr NdrClientCall2x86(IntPtr pMIDL_STUB_DESC, IntPtr formatString, IntPtr args);

        [DllImport("Rpcrt4.dll", EntryPoint = "NdrClientCall2", CallingConvention = CallingConvention.Cdecl,
            CharSet = CharSet.Unicode, SetLastError = true)]
        public static extern IntPtr NdrClientCall2x64(IntPtr pMIDL_STUB_DESC, IntPtr formatString, IntPtr Handle,
                                                       int DataSize, IntPtr Data, [Out] out int ResponseSize,
                                                       [Out] out IntPtr Response);

        [DllImport("Rpcrt4.dll", EntryPoint = "RpcStringFreeW", CallingConvention = CallingConvention.StdCall,
    CharSet = CharSet.Unicode, SetLastError = true)]
        public static extern RPC_STATUS RpcStringFree(ref IntPtr lpString);

        [DllImport("Rpcrt4.dll", EntryPoint = "RpcBindingFree", CallingConvention = CallingConvention.StdCall,
            CharSet = CharSet.Unicode, SetLastError = true)]
        public static extern RPC_STATUS RpcBindingFree(ref IntPtr lpString);



        [DllImport("Rpcrt4.dll", EntryPoint = "RpcStringBindingComposeW", CallingConvention = CallingConvention.StdCall,
            CharSet = CharSet.Unicode, SetLastError = true)]
        public static extern RPC_STATUS RpcStringBindingCompose(
            String ObjUuid, String ProtSeq, String NetworkAddr, String Endpoint, String Options,
            out IntPtr lpBindingString
            );

        [DllImport("Kernel32.dll", EntryPoint = "LocalFree", SetLastError = true,
    CharSet = CharSet.Unicode, ExactSpelling = true, CallingConvention = CallingConvention.Winapi)]
        public static extern IntPtr LocalFree(IntPtr memHandle);

        /// Return Type: RPC_STATUS->int  
        ///IfSpec: RPC_IF_HANDLE->void*  
        ///MgrTypeUuid: UUID*  
        ///MgrEpv: void*  
        ///Flags: unsigned int  
        ///MaxCalls: unsigned int  
        ///IfCallback: RPC_IF_CALLBACK_FN*  
        [DllImport("Rpcrt4.dll", EntryPoint = "RpcServerRegisterIfEx", CallingConvention = CallingConvention.StdCall,
             CharSet = CharSet.Unicode, SetLastError = true)]
        public static extern int RpcServerRegisterIfEx(IntPtr IfSpec, IntPtr MgrTypeUuid, IntPtr MgrEpv, InterfacRegistrationFlags Flags, uint MaxCalls, ref RPC_IF_CALLBACK_FN IfCallback);

        /// <summary>
        /// 
        /// </summary>
        /// <param name="IfSpec"></param>
        /// <param name="MgrTypeUuid"></param>
        /// <param name="MgrEpv"></param>
        /// <returns></returns>
        [DllImport("Rpcrt4.dll", EntryPoint = "RpcServerRegisterIf", CallingConvention = CallingConvention.StdCall,
    CharSet = CharSet.Unicode, SetLastError = true)]
        public static extern RPC_STATUS RpcServerRegisterIf(IntPtr IfSpec, IntPtr MgrTypeUuid, IntPtr MgrEpv);

      ///IfSpec: RPC_IF_HANDLE->void*     
	        ///MgrTypeUuid: UUID*     
	        ///MgrEpv: void*     
	        ///Flags: unsigned int     
	        ///MaxCalls: unsigned int     
	        ///MaxRpcSize: unsigned int     
	        ///IfCallbackFn: RPC_IF_CALLBACK_FN*     
	        [DllImport("rpcrt4.dll", EntryPoint = "RpcServerRegisterIf2", CallingConvention = CallingConvention.StdCall)]
        public static extern RPC_STATUS RpcServerRegisterIf2(IntPtr IfSpec, ref Guid MgrTypeUuid, IntPtr MgrEpv, uint Flags, uint MaxCalls, uint MaxRpcSize, ref RPC_IF_CALLBACK_FN IfCallbackFn);  


        ///<summary>
        ///http://msdn.microsoft.com/en-us/library/aa373623.aspx
        ///unsigned long StatusToConvert,
        ///unsigned char *ErrorText
        /// </summary>
                [DllImport("Rpcrt4.dll", EntryPoint = "DceErrorInqText", CallingConvention = CallingConvention.StdCall,
    CharSet = CharSet.Unicode, SetLastError = true)]
        public static extern RPC_STATUS DceErrorInqText(uint StatusToConvert, out string ErrorText);

        [DllImport("Rpcrt4.dll", EntryPoint = "RpcServerUnregisterIf", CallingConvention = CallingConvention.StdCall,
            CharSet = CharSet.Unicode, SetLastError = true)]
        public static extern RPC_STATUS RpcServerUnregisterIf(IntPtr IfSpec, IntPtr MgrTypeUuid,
                                                            uint WaitForCallsToComplete);

        [DllImport("Rpcrt4.dll", EntryPoint = "RpcServerUseProtseqEpW", CallingConvention = CallingConvention.StdCall,
            CharSet = CharSet.Unicode, SetLastError = true)]
        public static extern RPC_STATUS RpcServerUseProtseqEp(String Protseq, uint MaxCalls, String Endpoint,
                                                            IntPtr SecurityDescriptor);

        [DllImport("Rpcrt4.dll", EntryPoint = "NdrServerCall2", CallingConvention = CallingConvention.StdCall,
            CharSet = CharSet.Unicode, SetLastError = true)]
        public static extern void NdrServerCall2(IntPtr ptr);


        [DllImport("Kernel32.dll", EntryPoint = "LocalAlloc", SetLastError = true,
            CharSet = CharSet.Unicode, ExactSpelling = true, CallingConvention = CallingConvention.Winapi)]
        public static extern IntPtr LocalAlloc(UInt32 flags, UInt32 nBytes);

        [DllImport("Rpcrt4.dll", EntryPoint = "RpcServerInqCallAttributesW",
    CallingConvention = CallingConvention.StdCall,
    CharSet = CharSet.Unicode, SetLastError = true)]
        public static extern RPC_STATUS RpcServerInqCallAttributes(IntPtr binding,
                                                                  [In, Out] ref RPC_CALL_ATTRIBUTES_V2 attributes);

        [DllImport("Rpcrt4.dll", EntryPoint = "RpcImpersonateClient", CallingConvention = CallingConvention.StdCall,
            CharSet = CharSet.Unicode, SetLastError = true)]
        public static extern RPC_STATUS RpcImpersonateClient(IntPtr binding);

        [DllImport("Rpcrt4.dll", EntryPoint = "RpcRevertToSelfEx", CallingConvention = CallingConvention.StdCall,
            CharSet = CharSet.Unicode, SetLastError = true)]
        public static extern RPC_STATUS RpcRevertToSelfEx(IntPtr binding);

        [DllImport("Rpcrt4.dll", EntryPoint = "RpcServerListen", CallingConvention = CallingConvention.StdCall,
    CharSet = CharSet.Unicode, SetLastError = true)]
        public static extern RPC_STATUS RpcServerListen(uint MinimumCallThreads, uint MaxCalls, uint DontWait);

        [DllImport("Rpcrt4.dll", EntryPoint = "RpcMgmtStopServerListening",
     CallingConvention = CallingConvention.StdCall,
     CharSet = CharSet.Unicode, SetLastError = true)]
        public static extern RPC_STATUS RpcMgmtStopServerListening(IntPtr ignore);

        [DllImport("Rpcrt4.dll", EntryPoint = "RpcMgmtWaitServerListen", CallingConvention = CallingConvention.StdCall,
            CharSet = CharSet.Unicode, SetLastError = true)]
        public static extern RPC_STATUS RpcMgmtWaitServerListen();

        [DllImport("Rpcrt4.dll", EntryPoint = "RpcServerRegisterAuthInfoW",
    CallingConvention = CallingConvention.StdCall,
    CharSet = CharSet.Unicode, SetLastError = true)]
        public static extern RPC_STATUS RpcServerRegisterAuthInfo(String ServerPrincName, uint AuthnSvc, IntPtr GetKeyFn,
                                                                 IntPtr Arg);

  
    }
}
