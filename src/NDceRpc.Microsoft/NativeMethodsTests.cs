using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using NDceRpc.Microsoft.Interop;
using NUnit.Framework;

namespace NDceRpc.Test
{
    [TestFixture]
    public class NativeMethodsTests
    {
        [Test]
        public void Authenticaion()
        {
            SEC_WINNT_AUTH_IDENTITY identity = new SEC_WINNT_AUTH_IDENTITY();
var status =             NativeMethods.RpcBindingSetAuthInfo(IntPtr.Zero, "", RPC_C_AUTHN_LEVEL.RPC_C_AUTHN_LEVEL_NONE,
                                                RPC_C_AUTHN.RPC_C_AUTHN_NONE,ref identity,0);
        }
    }
}
