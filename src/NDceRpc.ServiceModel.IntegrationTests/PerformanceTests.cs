using System;
using System.Diagnostics;
using System.Reflection;
using System.Runtime.InteropServices;
using System.ServiceModel;
using System.Threading;
using NUnit.Framework;

namespace NDceRpc.ServiceModel.Test
{
    [TestFixture]
    public class PerformanceTests
    {

        [ServiceContract]
        [Guid("11B688EC-5F06-4AE4-AA0A-2895BB125FE7")]
        public interface IService 
        {
            [OperationContract(IsOneWay = false)]
            byte[] Execute(byte[] arg);
        }

        [ServiceBehavior(InstanceContextMode = InstanceContextMode.Single)]
        private class Service : IService
        {
            public byte[] Execute(byte[] arg)
            {
                return arg;
            }
        }


        [Test]
        public void TestPerformanceOnNamedPipe()
        {
            var uri = @"net.pipe://127.0.0.1/testpipename" + MethodBase.GetCurrentMethod().Name;
            using (var server = new NDceRpc.ServiceModel.ServiceHost(new Service(), uri))
            {
                server.AddServiceEndpoint(typeof(IService), new NDceRpc.ServiceModel.NetNamedPipeBinding { MaxConnections = 5 },
                                          uri);
                server.Open();
                using (
                    var channelFactory = new ChannelFactory<IService>(new NetNamedPipeBinding {MaxConnections = 5})
                    )
                {

                    var client = channelFactory.CreateChannel(new EndpointAddress(uri));
                    client.Execute(new byte[0]);

                    byte[] bytes = new byte[512];
                    new Random().NextBytes(bytes);

                    Stopwatch timer = new Stopwatch();
                    timer.Start();

                    for (int i = 0; i < 5000; i++)
                        client.Execute(bytes);

                    timer.Stop();
                    Trace.WriteLine(timer.ElapsedMilliseconds.ToString(), "ncacn_np-timming");
                }
            }
        }


        
    }
}
