using System;
using System.Collections.Generic;
using System.Linq;
using System.ServiceModel;
using System.Text;
using System.Threading;
using NUnit.Framework;

namespace WCF.IntegrationTests
{
    [TestFixture]
    public class TimeoutTests
    {
        [ServiceContract]
        [System.Runtime.InteropServices.Guid("11B688EC-5F06-4AE4-AA0A-2895BB125FE7")]
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
        public void TestTimeoutSet()
        {

            using (var server = new System.ServiceModel.ServiceHost(new Service(), new Uri("net.tcp://127.0.0.1:6782")))
            {
                var binding = new System.ServiceModel.NetTcpBinding() { MaxConnections = 5 };
                server.AddServiceEndpoint(typeof(IService), binding, "net.tcp://127.0.0.1:6782");
                server.Open();
                Thread.Sleep(100);
                using (var channelFactory = new System.ServiceModel.ChannelFactory<IService>(binding))
                {

                    var client = channelFactory.CreateChannel(new EndpointAddress("net.tcp://127.0.0.1:6782"));
                    var result = client.Execute(new byte[] { 1 });
                    Assert.AreEqual(1, result[0]);

                }
            }
        }
    }
}
