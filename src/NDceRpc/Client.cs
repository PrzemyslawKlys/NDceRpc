using System;
using System.Net;
using System.Runtime.InteropServices;
using NDceRpc.Interop;

namespace NDceRpc
{
    /// <summary>
    /// Provides a connection-based wrapper around the RPC client
    /// </summary>
    [System.Diagnostics.DebuggerDisplay("{_handle} @{_binding}")]
    public class Client:IDisposable
    {

        protected bool _authenticated;
        private readonly RpcProtseq _protocol;
        private readonly string _binding;
        protected readonly RpcHandle _handle;

        public Client(EndpointBindingInfo endpointBindingInfo) 
        {
            _handle = new RpcClientHandle();
            _protocol = endpointBindingInfo.Protseq;
            _binding = stringBindingCompose(endpointBindingInfo, null);
            RpcTrace.Verbose("Client('{0}:{1}')", endpointBindingInfo.NetworkAddr, endpointBindingInfo.EndPoint);

            connect();
        }

        // Creates a string binding handle.
        // This function is nothing more than a printf.
        // Connection is not done here.
        private static String stringBindingCompose(EndpointBindingInfo endpointBindingInfo,
                                           String Options)
        {
            IntPtr lpBindingString;
            RPC_STATUS result = NativeMethods.RpcStringBindingCompose(null, endpointBindingInfo.Protseq.ToString(), endpointBindingInfo.NetworkAddr, endpointBindingInfo.EndPoint, Options,
                                                      out lpBindingString);
            Guard.Assert(result);

            try
            {
                return Marshal.PtrToStringUni(lpBindingString);
            }
            finally
            {
                Guard.Assert(NativeMethods.RpcStringFree(ref lpBindingString));
            }
        }


        protected class RpcClientHandle : RpcHandle
        {
            protected override void DisposeHandle(ref IntPtr handle)
            {
                if (handle != IntPtr.Zero)
                {
                    Guard.Assert(NativeMethods.RpcBindingFree(ref Handle));
                    handle = IntPtr.Zero;
                }
            }
        }

        /// <summary>
        /// Disconnects the client and frees any resources.
        /// </summary>
        public void Dispose()
        {
            RpcTrace.Verbose("RpcClient('{0}').Dispose()", _binding);
            _handle.Dispose();
        }
        /// <summary>
        /// The protocol that was provided to the constructor
        /// </summary>
        public RpcProtseq Protocol
        {
            get { return _protocol; }
        }

        /// <summary>
        /// Connects the client; however, this is a soft-connection and validation of 
        /// the connection will not take place until the first call is attempted.
        /// </summary>
        private void connect()
        {
            bindingFromStringBinding(_handle, _binding);
            RpcTrace.Verbose("RpcClient.connect({0} = {1})", _handle.Handle, _binding);
        }

        private static void bindingFromStringBinding(RpcHandle handle, String bindingString)
        {
            RPC_STATUS result = NativeMethods.RpcBindingFromStringBinding(bindingString, out handle.Handle);
            Guard.Assert(result);
        }

        /// <summary>
        /// Returns a constant NetworkCredential that represents the Anonymous user
        /// </summary>
        public static NetworkCredential Anonymous
        {
            get { return new NetworkCredential("ANONYMOUS LOGON", "", "NT_AUTHORITY"); }
        }
        /// <summary>
        /// Returns a constant NetworkCredential that represents the current Windows user
        /// </summary>
        public static NetworkCredential Self
        {
            get { return null; }
        }



        /// <summary>
        /// Adds authentication information to the client, use the static Self to
        /// authenticate as the currently logged on Windows user.
        /// </summary>
        public void AuthenticateAs(NetworkCredential credentials)
        {
            AuthenticateAs(null, credentials);
        }
        /// <summary>
        /// Adds authentication information to the client, use the static Self to
        /// authenticate as the currently logged on Windows user.
        /// </summary>
        public void AuthenticateAs(string serverPrincipalName, NetworkCredential credentials)
        {
            RpcAuthentication[] types = new RpcAuthentication[] { RpcAuthentication.RPC_C_AUTHN_GSS_NEGOTIATE, RpcAuthentication.RPC_C_AUTHN_WINNT };
            RpcProtectionLevel protect = RpcProtectionLevel.RPC_C_PROTECT_LEVEL_PKT_PRIVACY;

            bool isAnon = (credentials != null && credentials.UserName == Anonymous.UserName && credentials.Domain == Anonymous.Domain);
            if (isAnon)
            {
                protect = RpcProtectionLevel.RPC_C_PROTECT_LEVEL_DEFAULT;
                types = new RpcAuthentication[] { RpcAuthentication.RPC_C_AUTHN_NONE };
            }

            AuthenticateAs(serverPrincipalName, credentials, protect, types);
        }

        /// <summary>
        /// Adds authentication information to the client, use the static Self to
        /// authenticate as the currently logged on Windows user.  This overload allows
        /// you to specify the privacy level and authentication types to try. Normally
        /// these default to RPC_C_PROTECT_LEVEL_PKT_PRIVACY, and both RPC_C_AUTHN_GSS_NEGOTIATE
        /// or RPC_C_AUTHN_WINNT if that fails.  If credentials is null, or is the Anonymous
        /// user, RPC_C_PROTECT_LEVEL_DEFAULT and RPC_C_AUTHN_NONE are used instead.
        /// </summary>
        public void AuthenticateAs(string serverPrincipalName, NetworkCredential credentials, RpcProtectionLevel level, params RpcAuthentication[] authTypes)
        {
            if (!_authenticated)
            {
                bindingSetAuthInfo(level, authTypes, _handle, serverPrincipalName, credentials);
                _authenticated = true;
            }
        }






        private static void bindingSetAuthInfo(RpcProtectionLevel level, RpcAuthentication[] authTypes,
                                               RpcHandle handle, string serverPrincipalName, NetworkCredential credentails)
        {
            if (credentails == null)
            {
                foreach (RpcAuthentication atype in authTypes)
                {
                    RPC_STATUS result = NativeMethods.RpcBindingSetAuthInfo2(handle.Handle, serverPrincipalName, level, atype, IntPtr.Zero, 0);
                    if (result != RPC_STATUS.RPC_S_OK)
                        RpcTrace.Warning("Unable to register {0}, result = {1}", atype, new RpcException(result).Message);
                }
            }
            else
            {
                SEC_WINNT_AUTH_IDENTITY pSecInfo = new SEC_WINNT_AUTH_IDENTITY(credentails);
                foreach (RpcAuthentication atype in authTypes)
                {
                    RPC_STATUS result = NativeMethods.RpcBindingSetAuthInfo(handle.Handle, serverPrincipalName, level, atype, ref pSecInfo, 0);
                    if (result != RPC_STATUS.RPC_S_OK)
                        RpcTrace.Warning("Unable to register {0}, result = {1}", atype, new RpcException(result).Message);
                }
            }
        }


    }
}