

using System;
using NDceRpc.ExplicitBytes;
using NDceRpc.Microsoft.Interop;
using NUnit.Framework;

namespace NDceRpc.Test
{
    [TestFixture]
    public class ExplicitBytesClientTests
    {

        [Test]
        public void TestPropertyProtocol()
        {
            var endpoingBinding = new EndpointBindingInfo(RpcProtseq.ncacn_ip_tcp, null, "123");
            using (ExplicitBytesClient client = new ExplicitBytesClient(Guid.NewGuid(), endpoingBinding))
                Assert.AreEqual(RpcProtseq.ncacn_ip_tcp, client.Protocol);
        }

        [Test]
        public void TestClientAbandon()
        {
            Guid iid = Guid.NewGuid();
            using (ExplicitBytesServer server = new ExplicitBytesServer(iid))
            {
                server.AddProtocol(RpcProtseq.ncalrpc, "lrpctest", 5);
                server.AddAuthentication(RPC_C_AUTHN.RPC_C_AUTHN_WINNT);
                server.StartListening();
                server.OnExecute +=
                    delegate(IRpcCallInfo client, byte[] arg)
                    { return arg; };

                {
                    var endpoingBinding = new EndpointBindingInfo(RpcProtseq.ncalrpc, null, "lrpctest");
                    ExplicitBytesClient client = new ExplicitBytesClient(iid, endpoingBinding);
                    client.AuthenticateAs(null, ExplicitBytesClient.Self, RPC_C_AUTHN_LEVEL.RPC_C_AUTHN_LEVEL_PKT_PRIVACY, RPC_C_AUTHN.RPC_C_AUTHN_WINNT);
                    client.Execute(new byte[0]);
                    client = null;
                }

                GC.Collect(0, GCCollectionMode.Forced);
                GC.WaitForPendingFinalizers();

                server.StopListening();
            }
        }

        [Test]
        public void TestClientCannotConnect()
        {
            Assert.Throws<RpcException>(() =>
            {
                var endpoingBinding = new EndpointBindingInfo(RpcProtseq.ncalrpc, null, "lrpc-endpoint-doesnt-exist");
                using (ExplicitBytesClient client = new ExplicitBytesClient(Guid.NewGuid(), endpoingBinding))
                    client.Execute(new byte[0]);
            });
        }

        [Test]
        public void TestExceptionExplicitMessage()
        {
            var ex = Assert.Throws<RpcException>(() => { throw new RpcException("TEST_MESSAGE"); });
            Assert.That(ex.Message, Is.EqualTo("TEST_MESSAGE"));
        }

        [Test]
        public void TestExceptionDefaultMessage()
        {
            var ex = Assert.Throws<RpcException>(() => { throw new RpcException(); });
            Assert.That(ex.Message, Is.EqualTo("Unspecified rpc error"));
        }

    }
}
