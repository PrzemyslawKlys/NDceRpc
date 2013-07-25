using System;
using System.Collections.Generic;
using System.Linq;
using System.Reflection;
using System.ServiceModel;
using System.ServiceModel.Channels;
using System.ServiceModel.Description;
using System.ServiceModel.Dispatcher;
using System.Text;
using NUnit.Framework;

namespace WCF.Tests
{
    [TestFixture]
    public class EndpointBehavioursTests
    {
        [Test]
        public void ServerAncClientEndpointBehavior()
        {
            var hook = new InvokesCounterBehaviour();
            var address = @"net.pipe://127.0.0.1/test" + this.GetType().Name+"_"+ MethodBase.GetCurrentMethod().Name;
            var serv = new Service(null);
            var host = new ServiceHost(serv, new Uri[] { new Uri(address), });
            var b = new NetNamedPipeBinding();
            var serverEndpoint = host.AddServiceEndpoint(typeof(IService), b, address);
            serverEndpoint.Behaviors.Add(hook);
            Assert.AreEqual(0, hook.Counter);
            host.Open();
            Assert.AreEqual(3,hook.Counter);
            var f = new ChannelFactory<IService>(b);
            f.Endpoint.Behaviors.Add(hook);
            Assert.AreEqual(3, hook.Counter);
            var c = f.CreateChannel(new EndpointAddress(address));
            Assert.AreEqual(6, hook.Counter);
            var result = c.DoWithParamsAndResult("", Guid.NewGuid());

            host.Abort();
        }
    }

    public class InvokesCounterBehaviour : IEndpointBehavior
    {
        public int Counter { get; set; }

        public void Validate(ServiceEndpoint endpoint)
        {
            Counter++;
        }

        public void AddBindingParameters(ServiceEndpoint endpoint, BindingParameterCollection bindingParameters)
        {
            Counter++;
        }

        public void ApplyDispatchBehavior(ServiceEndpoint endpoint, EndpointDispatcher endpointDispatcher)
        {
            Counter++;
        }

        public void ApplyClientBehavior(ServiceEndpoint endpoint, ClientRuntime clientRuntime)
        {
            Counter++;
        }
    }
}
