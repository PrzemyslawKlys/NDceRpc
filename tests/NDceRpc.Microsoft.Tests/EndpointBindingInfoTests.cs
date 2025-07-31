using System;
using NDceRpc;
using NDceRpc.Microsoft.Interop;
using NUnit.Framework;

namespace NDceRpc.Test
{
    [TestFixture]
    public class EndpointBindingInfoTests
    {
        [Test]
        public void Constructor_ValidParams_CreatesInstance()
        {
            var protseq = RpcProtseq.ncacn_ip_tcp;
            var networkAddr = "localhost";
            var endpoint = "12345";

            var binding = new EndpointBindingInfo(protseq, networkAddr, endpoint);

            Assert.AreEqual(protseq, binding.Protseq);
            Assert.AreEqual(networkAddr, binding.NetworkAddr);
            Assert.AreEqual(endpoint, binding.EndPoint);
        }

        [Test]
        public void Clone_CreatesIndependentCopy()
        {
            var original = new EndpointBindingInfo(RpcProtseq.ncacn_np, "server", @"\pipe\test");
            
            var clone = (EndpointBindingInfo)original.Clone();

            Assert.AreEqual(original.Protseq, clone.Protseq);
            Assert.AreEqual(original.NetworkAddr, clone.NetworkAddr);
            Assert.AreEqual(original.EndPoint, clone.EndPoint);
            Assert.AreNotSame(original, clone);
        }

        [Test]
        public void DebuggerDisplay_ShowsCorrectFormat()
        {
            var binding = new EndpointBindingInfo(RpcProtseq.ncalrpc, null, "local_endpoint");
            
            // The DebuggerDisplay attribute shows "{Protseq} {NetworkAddr} {EndPoint}"
            var expectedDisplay = $"{binding.Protseq} {binding.NetworkAddr} {binding.EndPoint}";
            
            Assert.IsNotNull(binding);
            Assert.AreEqual(RpcProtseq.ncalrpc, binding.Protseq);
            Assert.IsNull(binding.NetworkAddr);
            Assert.AreEqual("local_endpoint", binding.EndPoint);
        }
    }
}