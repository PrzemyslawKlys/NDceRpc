using System;
using System.Reflection;
using System.ServiceModel;
using System.Threading;
using System.Threading.Tasks;
using NDceRpc.ServiceModel.Test;
using NUnit.Framework;

namespace NDceRpc.ServiceModel.Tests
{
    [TestFixture]
    public class AsyncTests
    {


        [Test]
        public void CallbackAsyncCallback_wait_done()
        {

            var address = @"net.pipe://127.0.0.1/" + this.GetType().Name + "_" + MethodBase.GetCurrentMethod().Name;
            var binding = new NetNamedPipeBinding();


            var srv = new AsyncService(null);
            var callback = new AsyncServiceCallback();

            using (var host = new ServiceHost(srv, new Uri(address)))
            {
                host.AddServiceEndpoint(typeof(IAsyncService), binding, address);
                host.Open();


                using (var factory = new DuplexChannelFactory<IAsyncService>(new InstanceContext(callback), binding))
                {
                    var client = factory.CreateChannel(new EndpointAddress(address));
                //    client.DoSyncCall();

                }
            }
        }

        [Test]
        public void CallAsyncCallback_wait_done()
        {
            var address = @"net.pipe://127.0.0.1/" + this.GetType().Name + "_" + MethodBase.GetCurrentMethod().Name;
            var binding = new NetNamedPipeBinding();

            var done = new ManualResetEvent(false);
            var srv = new AsyncService(done);
            var callback = new AsyncServiceCallback();

            using (var host = new ServiceHost(srv, new Uri(address)))
            {
                host.AddServiceEndpoint(typeof(IAsyncService), binding, address);
                host.Open();

                ThreadPool.QueueUserWorkItem(_ =>
                {
                    using (var factory = new DuplexChannelFactory<IAsyncService>(new InstanceContext(callback), binding))
                    {
                        var client = factory.CreateChannel(new EndpointAddress(address));
                        AsyncCallback act = (x) =>
                        {
                            Assert.AreEqual(x.AsyncState, 1);
                        };
                        var result = client.BeginServiceAsyncMethod(act, 1);
                        result.AsyncWaitHandle.WaitOne();
                        Assert.AreEqual(result.AsyncState, 1);
                      //  client.EndServiceAsyncMethod(result);

                    }
                });

              //  done.WaitOne();
            }
        }

        [Test]
        public void CallAsync_noServer_done()
        {

            var address = @"net.pipe://127.0.0.1/1/test.test/test" + MethodBase.GetCurrentMethod().Name;
            var binding = new NetNamedPipeBinding();

            var callback = new AsyncServiceCallback();


            using (var factory = new DuplexChannelFactory<IAsyncService>(new InstanceContext(callback), binding))
            {
                var client = factory.CreateChannel(new EndpointAddress(address));
                AsyncCallback act = x => Assert.AreEqual(x.AsyncState, 1);
                IAsyncResult result = client.BeginServiceAsyncMethod(act, 1);
                result.AsyncWaitHandle.WaitOne();
                Assert.AreEqual(result.AsyncState, 1);
            }
        }

    }
}
