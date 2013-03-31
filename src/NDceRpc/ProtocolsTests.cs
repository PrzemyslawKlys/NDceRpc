

using System;
using System.Text;
using NDceRpc.ExplicitBytes;
using NDceRpc.Interop;
using NUnit.Framework;

namespace NDceRpc.Test
{
    [TestFixture]
    public class ProtocolsTests
    {
        string[] LocalNames = new string[] { null, "localhost", "127.0.0.1", "::1", Environment.MachineName };

  

        [Test]
        public void TcpIpTest()
        {
            ReversePingTest(RpcProtseq.ncacn_ip_tcp, LocalNames, "18080", 
                RpcAuthentication.RPC_C_AUTHN_WINNT, RpcAuthentication.RPC_C_AUTHN_GSS_NEGOTIATE);
        }

        [Test]
        public void NamedPipeTest()
        {
            ReversePingTest(RpcProtseq.ncacn_np, LocalNames, @"\pipe\testpipename", 
                RpcAuthentication.RPC_C_AUTHN_NONE, RpcAuthentication.RPC_C_AUTHN_WINNT, RpcAuthentication.RPC_C_AUTHN_GSS_NEGOTIATE);
        }

        [Test]
        public void LocalRpcTest()
        {
            ReversePingTest(RpcProtseq.ncalrpc, new string[] { null }, @"testsomename", 
                RpcAuthentication.RPC_C_AUTHN_NONE, RpcAuthentication.RPC_C_AUTHN_WINNT, RpcAuthentication.RPC_C_AUTHN_GSS_NEGOTIATE);
        }

        /*
         *  Helper Methods
         */

        static void ReversePingTest(RpcProtseq protocol, string[] hostNames, string endpoint, params RpcAuthentication[] authTypes)
        {
            foreach (RpcAuthentication auth in authTypes)
                ReversePingTest(protocol, hostNames, endpoint, auth);
        }

        static void ReversePingTest(RpcProtseq protocol, string[] hostNames, string endpoint, RpcAuthentication auth)
        {
            Guid iid = Guid.NewGuid();
            using (ExplicitBytesServer server = new ExplicitBytesServer(iid))
            {
                server.OnExecute += 
                    delegate(IRpcCallInfo client, byte[] arg)
                    {
                        Array.Reverse(arg);
                        return arg;
                    };

                server.AddProtocol(protocol, endpoint, 5);
                server.AddAuthentication(auth);
                server.StartListening();

                byte[] input = Encoding.ASCII.GetBytes("abc");
                byte[] expect = Encoding.ASCII.GetBytes("cba");

                foreach (string hostName in hostNames)
                {
                    using (ExplicitBytesClient client = new ExplicitBytesClient(iid, new EndpointBindingInfo(protocol, hostName, endpoint)))
                    {
                        client.AuthenticateAs(null, auth == RpcAuthentication.RPC_C_AUTHN_NONE
                                                      ? ExplicitBytesClient.Anonymous
                                                      : ExplicitBytesClient.Self, 
                                                  auth == RpcAuthentication.RPC_C_AUTHN_NONE
                                                      ? RpcProtectionLevel.RPC_C_PROTECT_LEVEL_NONE
                                                      : RpcProtectionLevel.RPC_C_PROTECT_LEVEL_PKT_PRIVACY,
                                                  auth);

                        Assert.AreEqual(expect, client.Execute(input));
                    }
                }
            }
        }
    }
}
