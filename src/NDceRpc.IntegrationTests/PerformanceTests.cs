

using System;
using System.Diagnostics;
using System.Reflection;
using System.Threading.Tasks;
using NDceRpc.ExplicitBytes;
using NDceRpc.Interop;
using NDceRpc.ServiceModel;
using NUnit.Framework;

namespace NDceRpc.Test
{
    [TestFixture]
    public class TestPerformance
    {

        [Test]
        public void TestConcurentCreationOfServersAndClients2()
        {
            var callbackWasCalled = false;
            var serverId = Guid.NewGuid();
            var serverPipe = "\\pipe\\testserver" + MethodBase.GetCurrentMethod().Name;
            var callbackPipe = "\\pipe\\testcallback" + MethodBase.GetCurrentMethod().Name;
            var callbackId = Guid.NewGuid();
            var taskServer = new Task(() =>
                {
                    var server = new ExplicitBytesServer(serverId);
                    server.AddProtocol(RpcProtseq.ncacn_np, serverPipe, byte.MaxValue);
                    server.AddAuthentication(RpcAuthentication.RPC_C_AUTHN_NONE);
                    server.StartListening();

                    server.OnExecute += (x, y) =>
                    {
                        var taskCallback = new Task(() =>
                        {
                            ExplicitBytesClient callbackClient = new ExplicitBytesClient(callbackId, new EndpointBindingInfo(RpcProtseq.ncacn_np, null,
                                                                           callbackPipe));
                            callbackClient.AuthenticateAs(null, ExplicitBytesClient.Self,
                                                          RpcProtectionLevel.RPC_C_PROTECT_LEVEL_NONE,
                                                          RpcAuthentication.RPC_C_AUTHN_NONE);
                            callbackClient.Execute(new byte[0]);
                        });
                        taskCallback.Start();
                        taskCallback.Wait();
                        return y;
                    };
                });
            taskServer.Start();
            taskServer.Wait();

            var taskClient = new Task(() =>
                {
                    var client = new ExplicitBytesClient(serverId, new EndpointBindingInfo(RpcProtseq.ncacn_np, null, serverPipe));
                    client.AuthenticateAs(null, ExplicitBytesClient.Self, RpcProtectionLevel.RPC_C_PROTECT_LEVEL_NONE,
                                          RpcAuthentication.RPC_C_AUTHN_NONE);
                    var callbackServer = new ExplicitBytesServer(callbackId);
                    callbackServer.AddProtocol(RpcProtseq.ncacn_np, callbackPipe, byte.MaxValue);
                    callbackServer.AddAuthentication(RpcAuthentication.RPC_C_AUTHN_NONE);
                    callbackServer.OnExecute += (x, y) =>
                        {
                            callbackWasCalled = true;
                            return y;
                        };
                    client.Execute(new byte[0]);
                });
            taskClient.Start();
            taskClient.Wait();
            Assert.IsTrue(callbackWasCalled);


        }

        [Test]
        public void TestPerformanceWithLargePayloads()
        {
            Guid iid = Guid.NewGuid();
            using (ExplicitBytesServer server = new ExplicitBytesServer(iid))
            {
                server.AddProtocol(RpcProtseq.ncalrpc, "lrpctest", 5);
                server.AddAuthentication(RpcAuthentication.RPC_C_AUTHN_WINNT);
                server.StartListening();
                server.OnExecute +=
                    delegate(IRpcCallInfo client, byte[] arg)
                    { return arg; };

                using (ExplicitBytesClient client = new ExplicitBytesClient(iid, new EndpointBindingInfo(RpcProtseq.ncalrpc, null, "lrpctest")))
                {
                    client.AuthenticateAs(null, ExplicitBytesClient.Self, RpcProtectionLevel.RPC_C_PROTECT_LEVEL_PKT_PRIVACY, RpcAuthentication.RPC_C_AUTHN_WINNT);
                    client.Execute(new byte[0]);

                    byte[] bytes = new byte[1 * 1024 * 1024]; //1mb in/out
                    new Random().NextBytes(bytes);

                    Stopwatch timer = new Stopwatch();
                    timer.Start();

                    for (int i = 0; i < 50; i++)
                        client.Execute(bytes);

                    timer.Stop();
                    Trace.WriteLine(timer.ElapsedMilliseconds.ToString(), "TestPerformanceWithLargePayloads");
                }
            }
        }

        [Test]
        public void TestPerformanceOnLocalRpc()
        {
            Guid iid = Guid.NewGuid();
            using (ExplicitBytesServer server = new ExplicitBytesServer(iid))
            {
                server.AddProtocol(RpcProtseq.ncalrpc, "lrpctest", 5);
                server.AddAuthentication(RpcAuthentication.RPC_C_AUTHN_WINNT);
                server.StartListening();
                server.OnExecute +=
                    delegate(IRpcCallInfo client, byte[] arg)
                    { return arg; };

                using (ExplicitBytesClient client = new ExplicitBytesClient(iid, new EndpointBindingInfo(RpcProtseq.ncalrpc, null, "lrpctest")))
                {
                    client.AuthenticateAs(null, ExplicitBytesClient.Self, RpcProtectionLevel.RPC_C_PROTECT_LEVEL_PKT_PRIVACY, RpcAuthentication.RPC_C_AUTHN_WINNT);
                    client.Execute(new byte[0]);

                    byte[] bytes = new byte[512];
                    new Random().NextBytes(bytes);

                    Stopwatch timer = new Stopwatch();
                    timer.Start();

                    for (int i = 0; i < 10000; i++)
                        client.Execute(bytes);

                    timer.Stop();
                    Trace.WriteLine(timer.ElapsedMilliseconds.ToString(), "TestPerformanceOnLocalRpc");
                }
            }
        }

        [Test]
        public void TestPerformanceOnNamedPipe()
        {
            Guid iid = Guid.NewGuid();
            using (ExplicitBytesServer server = new ExplicitBytesServer(iid))
            {
                server.AddProtocol(RpcProtseq.ncacn_np, @"\pipe\testpipename", 5);
                server.AddAuthentication(RpcAuthentication.RPC_C_AUTHN_WINNT);
                server.StartListening();
                server.OnExecute +=
                    delegate(IRpcCallInfo client, byte[] arg)
                    { return arg; };

                using (ExplicitBytesClient client = new ExplicitBytesClient(iid, new EndpointBindingInfo(RpcProtseq.ncacn_np, null, @"\pipe\testpipename")))
                {
                    client.AuthenticateAs(null, ExplicitBytesClient.Self, RpcProtectionLevel.RPC_C_PROTECT_LEVEL_PKT_PRIVACY, RpcAuthentication.RPC_C_AUTHN_WINNT);
                    client.Execute(new byte[0]);

                    byte[] bytes = new byte[512];
                    new Random().NextBytes(bytes);

                    Stopwatch timer = new Stopwatch();
                    timer.Start();

                    for (int i = 0; i < 5000; i++)
                        client.Execute(bytes);

                    timer.Stop();
                    Trace.WriteLine(timer.ElapsedMilliseconds.ToString(), "TestPerformanceOnNamedPipe");
                }
            }
        }

        [Test]
        public void TestPerformanceOnTcpip()
        {
            Guid iid = Guid.NewGuid();
            using (ExplicitBytesServer server = new ExplicitBytesServer(iid))
            {
                server.AddProtocol(RpcProtseq.ncacn_ip_tcp, @"18081", 5);
                server.AddAuthentication(RpcAuthentication.RPC_C_AUTHN_WINNT);
                server.StartListening();
                server.OnExecute +=
                    delegate(IRpcCallInfo client, byte[] arg)
                    { return arg; };

                using (ExplicitBytesClient client = new ExplicitBytesClient(iid, new EndpointBindingInfo(RpcProtseq.ncacn_ip_tcp, null, @"18081")))
                {
                    client.AuthenticateAs(null, ExplicitBytesClient.Self, RpcProtectionLevel.RPC_C_PROTECT_LEVEL_PKT_PRIVACY, RpcAuthentication.RPC_C_AUTHN_WINNT);
                    client.Execute(new byte[0]);

                    byte[] bytes = new byte[512];
                    new Random().NextBytes(bytes);

                    Stopwatch timer = new Stopwatch();
                    timer.Start();

                    for (int i = 0; i < 4000; i++)
                        client.Execute(bytes);

                    timer.Stop();
                    Trace.WriteLine(timer.ElapsedMilliseconds.ToString(), "TestPerformanceOnTcpip");
                }
            }
        }

        [Test]
        public void GuidGeneration()
        {
            var name = "1234567890-=qwertyuiop[]\asdfghjkl;'zxcvbnm,./1234567890-=";
            Stopwatch timer = new Stopwatch();
            timer.Start();

            for (int i = 0; i < 500; i++)
                GuidUtility.Create(GuidUtility.DnsNamespace, name, 5);

            timer.Stop();
            Trace.WriteLine(timer.ElapsedMilliseconds.ToString(), "GuidGeneration 5");

             timer = new Stopwatch();
            timer.Start();

            for (int i = 0; i < 500; i++)
                GuidUtility.Create(GuidUtility.DnsNamespace, name, 3);

            timer.Stop();
            Trace.WriteLine(timer.ElapsedMilliseconds.ToString(), "GuidGeneration 3");

        }
    }
}
