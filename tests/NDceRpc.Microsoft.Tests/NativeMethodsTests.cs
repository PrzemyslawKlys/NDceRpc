using System;
using NDceRpc.Microsoft.Interop;
using NUnit.Framework;

namespace NDceRpc.Test
{
    [TestFixture]
    public class NativeMethodsTests
    {
        [Test]
        public void RpcBindingSetAuthInfo_null_notOk()
        {
            var identity = new SEC_WINNT_AUTH_IDENTITY();
            var status = NativeMethods.RpcBindingSetAuthInfo(IntPtr.Zero, "", RPC_C_AUTHN_LEVEL.RPC_C_AUTHN_LEVEL_NONE,
                                                            RPC_C_AUTHN.RPC_C_AUTHN_NONE, ref identity, 0);
            Assert.AreNotEqual(RPC_STATUS.RPC_S_OK, status);
        }

        [Test]
        public void RpcStringBindingCompose_ValidParams_ReturnsOk()
        {
            IntPtr stringBinding;
            var status = NativeMethods.RpcStringBindingCompose(
                null,
                RpcProtseq.ncacn_ip_tcp.ToString(),
                "localhost",
                "12345",
                null,
                out stringBinding);

            Assert.AreEqual(RPC_STATUS.RPC_S_OK, status);
            Assert.AreNotEqual(IntPtr.Zero, stringBinding);

            // Clean up
            if (stringBinding != IntPtr.Zero)
            {
                NativeMethods.RpcStringFree(ref stringBinding);
            }
        }
    }
}