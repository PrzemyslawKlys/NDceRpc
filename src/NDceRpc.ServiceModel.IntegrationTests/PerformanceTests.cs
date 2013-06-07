using System;
using System.Diagnostics;
using System.Reflection;
using System.Runtime.InteropServices;

using System.Threading;
using NUnit.Framework;

namespace NDceRpc.ServiceModel.Test
{
    [TestFixture]
    public class PerformanceTests
    {

        [ System.ServiceModel.ServiceContract]
        [Guid("11B688EC-5F06-4AE4-AA0A-2895BB125FE7")]
        public interface IService 
        {
            [System.ServiceModel.OperationContract(IsOneWay = false)]
            byte[] Execute(byte[] arg);
        }

        [System.ServiceModel.ServiceBehavior(InstanceContextMode = System.ServiceModel.InstanceContextMode.Single)]
        private class Service : IService
        {
            public byte[] Execute(byte[] arg)
            {
                return arg;
            }
        }


        [Test]
        public void NamedPipe_byteArray()
        {

            using (var server = new ServiceHost(new Service(), new Uri("net.pipe://127.0.0.1/testpipename")))
            {
                var binding = new NetNamedPipeBinding { MaxConnections = 5 };
                server.AddServiceEndpoint(typeof(IService), binding, "net.pipe://127.0.0.1/testpipename");
                server.Open();
                Thread.Sleep(100);
                using (var channelFactory = new ChannelFactory<IService>(binding))
                {

                    var client = channelFactory.CreateChannel(new EndpointAddress("net.pipe://127.0.0.1/testpipename"));
                    client.Execute(new byte[0]);

                    byte[] bytes = new byte[512];
                    new Random().NextBytes(bytes);

                    Stopwatch timer = new Stopwatch();
                    timer.Start();

                    for (int i = 0; i < 5000; i++)
                        client.Execute(bytes);

                    timer.Stop();
                    Trace.WriteLine(timer.ElapsedMilliseconds.ToString() + " ms", MethodBase.GetCurrentMethod().Name);
                }
            }
        }


        [Test]
        public void Ipc_byteArray()
        {
            var uri = "ipc:///" + MethodBase.GetCurrentMethod().Name;
            using (var server = new ServiceHost(new Service(), new Uri(uri)))
            {
                var binding = new LocalBinding { MaxConnections = 5 };
                server.AddServiceEndpoint(typeof(IService), binding, uri);
                server.Open();
                Thread.Sleep(100);
                using (var channelFactory = new ChannelFactory<IService>(binding))
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
                    Trace.WriteLine(timer.ElapsedMilliseconds.ToString() + " ms", MethodBase.GetCurrentMethod().Name);
                }
            }
        }
    }
}
