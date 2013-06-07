using System;
using System.Reflection;
using System.ServiceModel;
using NUnit.Framework;

namespace WCF.Tests
{
    [TestFixture]
    public class HostTests
    {
        [Test]
        [ExpectedException(typeof(InvalidOperationException))]
        public void Open_Open_error()
        {
            var address = @"net.pipe://127.0.0.1/" + this.GetType().Name + MethodBase.GetCurrentMethod().Name;
            var serv = new Service(null);
            using (var host = new ServiceHost(serv, new Uri[] { new Uri(address) }))
            {
                var b = new NetNamedPipeBinding();
                host.AddServiceEndpoint(typeof(IService), b, address);
                host.Open();
                host.Open();
            }
        }
    }
}
