using System;
using System.Reflection;
using System.ServiceModel;
using System.Threading.Tasks;
using NUnit.Framework;

namespace NDceRpc.ServiceModel.Test
{
    [TestFixture]
    public class TestAsyncCallbacks
    {
        [ServiceContract(SessionMode = SessionMode.Required, CallbackContract = typeof(IAsyncServiceCallback))]
        public interface IAsyncService
        {
            [OperationContract(IsOneWay = false)]
            void Call();
        }

        private class AsyncService : IAsyncService
        {
            public void Call()
            {
                var callback = RpcOperationContext.Current.GetCallbackChannel<IAsyncServiceCallback>();
                var wait = callback.BeginCallback(null, null);
                wait.AsyncWaitHandle.WaitOne();
            }
        }


        public interface IAsyncServiceCallback
        {
            [OperationContract(IsOneWay = true, IsInitiating = false, IsTerminating = false,AsyncPattern = true)]
            IAsyncResult BeginCallback(AsyncCallback callback, object data);
            void EndCallback(IAsyncResult asyncResult);
        }

        private class AsyncServiceCallback : IAsyncServiceCallback
        {
            public IAsyncResult BeginCallback(AsyncCallback callback, object data)
            {
                var t = new Task(() => { });
                t.Start();
                return t;
            }

            public void EndCallback(IAsyncResult asyncResult)
            {
               
            }
        }

        [Test]
        public void CallServiceReturningSession2Times_sessionAreEqual()
        {

            var address = @"net.pipe://127.0.0.1/1/test.test/test" + MethodBase.GetCurrentMethod().Name;
            var binding = new NetNamedPipeBinding();

  
            var srv = new AsyncService();
            var callback = new AsyncServiceCallback();

            using (var host = new ServiceHost(srv, address))
            {
                host.AddServiceEndpoint(typeof(IAsyncService), binding, address);
                host.Open();


                using (var factory = new DuplexChannelFactory<IAsyncService>(new InstanceContext(callback), binding))
                {
                    var client = factory.CreateChannel(new EndpointAddress(address));
                    client.Call();
                }

       

            }
        }

    }
}
