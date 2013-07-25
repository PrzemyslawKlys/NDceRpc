using System;
using System.Reflection;
using System.ServiceModel.Channels;
using System.ServiceModel.Description;
using NDceRpc.ServiceModel.Test;
using NUnit.Framework;

namespace NDceRpc.ServiceModel.Tests
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
            var host = new NDceRpc.ServiceModel.ServiceHost(serv, new Uri[] { new Uri(address), });
            var b = new NDceRpc.ServiceModel.NetNamedPipeBinding();
            var serverEndpoint = host.AddServiceEndpoint(typeof(IService), b, address);
            serverEndpoint.Behaviors.Add(hook);
            Assert.AreEqual(0, hook.Counter);
            host.Open();
            Assert.AreEqual(1,hook.Counter);
            var f = new NDceRpc.ServiceModel.ChannelFactory<IService>(b);
            f.Endpoint.Behaviors.Add(hook);
            Assert.AreEqual(1, hook.Counter);
            var c = f.CreateChannel(new NDceRpc.ServiceModel.EndpointAddress(address));
            Assert.AreEqual(2, hook.Counter);
            var result = c.DoWithParamsAndResult("", Guid.NewGuid());

          
            host.Abort();
        }
    }

    public class InvokesCounterBehaviour : NDceRpc.ServiceModel.Description.IEndpointBehavior
    {
        public int Counter { get; set; }


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
