using System;
using System.Reflection;
using System.Runtime.InteropServices;
using System.Runtime.Serialization;
using System.ServiceModel;
using System.Threading;
using NUnit.Framework;

namespace WCF.Tests
{
    [TestFixture]
    public class ChannelTests
    {

        [ServiceContract]
        [Guid("7916102D-903A-4E2E-B8ED-4C0DEFEEDF15")]
        public interface IOtherService
        {

            [OperationContract(IsOneWay = true)]
            void Do();
        }

        public class OtherService : IOtherService
        {
            private ManualResetEvent _wait;

            public OtherService(ManualResetEvent wait)
            {
                _wait = wait;
            }

            public void Do()
            {
                _wait.Set();
            }
        }

        public class Service : IService
        {
            private EventWaitHandle _wait;

            public Service(EventWaitHandle wait)
            {
                _wait = wait;
            }

            public ServiceResult DoWithParamsAndResult(string p1, Guid p2)
            {
                return new ServiceResult { d1 = 2 };
            }

            public void DoOneWay()
            {
                _wait.Set();
            }

            public void CallOtherService(string address)
            {
        
            }

            public void Dispose()
            {

            }
        }


        [ServiceContract]
        [Guid("C059B8B0-9318-4467-9BB7-4FBB9979C3C5")]
        public interface IService : IDisposable
        {
            [OperationContract(IsOneWay = false)]
            ServiceResult DoWithParamsAndResult(string p1, Guid p2);

            [OperationContract(IsOneWay = true)]
            void DoOneWay();

            [OperationContract(IsOneWay = true)]
            void CallOtherService(string address);

        }

        [DataContract]
        public class ServiceResult
        {
            [DataMember(Order = 1)]
            public int d1 { get; set; }
            [DataMember(Order = 2)]
            public double d2 { get; set; }

        }

        [Test]
        public void LongNamePipe()
        {
            var address = @"net.pipe://127.0.0.1/1/test.test/testtestLongNameLongNameLongNameLongNameLongNameLongNameLongNameLongNameLongNamefd0286a60b9b4db18659-b715e5db5b3bd0286a6-0b9b-4db1-8659-b715e5db5b3b";
            var serv = new Service(null);
            var host = new ServiceHost(serv, new Uri[] { new Uri(address), });
            var b = new NetNamedPipeBinding();
            host.AddServiceEndpoint(typeof(IService), b, address);
            host.Open();
            var f = new ChannelFactory<IService>(b);
            var c = f.CreateChannel(new EndpointAddress(address));
            host.Open();
            var result = c.DoWithParamsAndResult(":)", Guid.NewGuid());
            Assert.AreEqual(2, result.d1);
            host.Abort();
        }

       


    }


}
