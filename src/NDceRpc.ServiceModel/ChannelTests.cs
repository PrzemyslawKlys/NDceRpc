using System;
using System.Reflection;
using System.Runtime.InteropServices;
using System.Runtime.Serialization;
using System.ServiceModel;
using System.Threading;
using NUnit.Framework;

namespace NDceRpc.ServiceModel.Test
{
    [TestFixture]
    public class ChannelTests
    {

   


        //[Test]
        //public void SingleTransportRuoting()
        //{

        //    TransportFactory.Transport = Transports.SingleRpcRouting;
        //    TransportFactory.SingleTransportServerUuid = new Guid("CD38A084-35B1-4701-90AA-72C908030A24");
        //    TransportFactory.SingleTransportClientUuid = new Guid("CD38A084-35B1-4701-90AA-72C908030A24");
        //    var address = "\\pipe\\testtest" + MethodBase.GetCurrentMethod().Name;
        //    var serv = new Service(null);
        //    var host = new ServiceHost(serv, address);
        //    var b = new NetNamedPipeBinding();
        //    host.AddServiceEndpoint(typeof(IService), b, address);
        //    host.Open();
        //    var f = new ChannelFactory<IService>(b);
        //    var c = f.CreateChannel(new EndpointAddress(address));
        //    var result = c.DoWithParamsAndResult(":)", Guid.NewGuid());
        //    Assert.AreEqual(2, result.d1);
        //    host.Dispose();

        //    TransportFactory.Transport = Transports.Rpc;

        //}

        [Test]
        public void Local()
        {
            var address = @"ipc:///test"+MethodBase.GetCurrentMethod().Name;
            var serv = new Service(null);
            var host = new ServiceHost(serv, address);
            var b = new LocalBinding();
            host.AddServiceEndpoint(typeof(IService), b, address);
            host.Open();
            var f = new ChannelFactory<IService>(b);
            var c = f.CreateChannel(new EndpointAddress(address));
            var result = c.DoWithParamsAndResult(":)", Guid.NewGuid());
            Assert.AreEqual(2, result.d1);
            host.Dispose();
        }

        [Test]
        public void InterfaceInheritance()
        {
            var address = @"ipc:///test" + MethodBase.GetCurrentMethod().Name;
            var serv = new InheritanceService();
            var host = new ServiceHost(serv, address);
            var b = new LocalBinding();
            host.AddServiceEndpoint(typeof(IInheritanceService), b, address);
            host.Open();
            var f = new ChannelFactory<IInheritanceService>(b);
            var c = f.CreateChannel(new EndpointAddress(address));
            c.Do();
            c.DoBase();

            host.Dispose();
        }

        [Test]
        public void LocalChannelICommuicationObject()
        {
            var address = @"ipc:///test" + MethodBase.GetCurrentMethod().Name;
            var serv = new Service(null);
            var host = new ServiceHost(serv, address);
            var b = new LocalBinding();
            host.AddServiceEndpoint(typeof(IService), b, address);
            host.Open();
            var f = new ChannelFactory<IService>(b);
            var c = f.CreateChannel(new EndpointAddress(address));
            var obj = c as ICommunicationObject;
            var state = obj.State; 
            Assert.AreEqual(CommunicationState.Opened,state);
            host.Dispose();
        }

        [Test]
        public void LongLocalName()
        {
            var address = @"ipc:///1/test.test/testtestLongNameLongNameLongNameLongNameLongNameLongNameLongNameLongNameLongNamefd0286a60b9b4db18659-b715e5db5b3bd0286a6-0b9b-4db1-8659-b715e5db5b3b";
            var serv = new Service(null);
            var host = new ServiceHost(serv, address);
            var b = new LocalBinding();
            host.AddServiceEndpoint(typeof(IService), b, address);
            host.Open();
            var f = new ChannelFactory<IService>(b);
            var c = f.CreateChannel(new EndpointAddress(address));
            var result = c.DoWithParamsAndResult(":)", Guid.NewGuid());
            Assert.AreEqual(2, result.d1);
            host.Dispose();
        }

        [Test]
        public void LongNamePipe()
        {
            var address = @"net.pipe://127.0.0.1/1/test.test/testtestLongNameLongNameLongNameLongNameLongNameLongNameLongNameLongNameLongNamefd0286a60b9b4db18659-b715e5db5b3bd0286a6-0b9b-4db1-8659-b715e5db5b3b";
            var serv = new Service(null);
            var host = new ServiceHost(serv, address);
            var b = new NetNamedPipeBinding();
            host.AddServiceEndpoint(typeof(IService), b, address);
            host.Open();
            var f = new ChannelFactory<IService>(b);
            var c = f.CreateChannel(new EndpointAddress(address));
   
            var result = c.DoWithParamsAndResult(":)", Guid.NewGuid());
            Assert.AreEqual(2, result.d1);
            host.Dispose();
        }

        [Test]
        public void TpcIp()
        {
            var address = @"net.tcp://127.0.0.1:18080";
            var serv = new Service(null);
            var host = new ServiceHost(serv, address);
            var b = new NetTcpBinding();
            host.AddServiceEndpoint(typeof(IService), b, address);
            host.Open();
            var f = new ChannelFactory<IService>(b);
            var c = f.CreateChannel(new EndpointAddress(address));

            var result = c.DoWithParamsAndResult(":)", Guid.NewGuid());
            Assert.AreEqual(2, result.d1);
            host.Dispose();
        }

        [Test]
        public void InvokenBlockingWithParams_resultObtained()
        {
            var address = @"net.pipe://127.0.0.1/1/test.test/test";

            var serv = new Service(null);
            var host = new ServiceHost(serv, address);
            var b = new NetNamedPipeBinding();
            host.AddServiceEndpoint(typeof(IService), b, address);
            host.Open();
            var f = new ChannelFactory<IService>(b);
            var c = f.CreateChannel(new EndpointAddress(address));
            var result = c.DoWithParamsAndResult(":)", Guid.NewGuid());
            Assert.AreEqual(2, result.d1);
            host.Dispose();
        }

        [Test]
        public void InvokeOneWay_waitOnEvent_received()
        {
            var address = @"net.pipe://127.0.0.1/1/test.test/test";
            var wait = new ManualResetEvent(false);
            var serv = new Service(wait);
            var host = new ServiceHost(serv, address);
            var b = new NetNamedPipeBinding();
            host.AddServiceEndpoint(typeof(IService), b, address);
            var f = new ChannelFactory<IService>(b);
            var c = f.CreateChannel(new EndpointAddress(address));
            host.Open();
            c.DoOneWay();
            wait.WaitOne();
            host.Dispose();
        }


        [Test]
        public void InvokeOtherService()
        {
            var address = @"net.pipe://127.0.0.1/1/test.test/test" + MethodBase.GetCurrentMethod().Name;
            var otherAddress = @"net.pipe://127.0.0.1/1/test.test/other" + MethodBase.GetCurrentMethod().Name;
            var wait = new ManualResetEvent(false);
            var srv = new Service(null);
            var otherSrv = new OtherService(wait);
            var host = new ServiceHost(srv, address);
            var b = new NetNamedPipeBinding();
            host.AddServiceEndpoint(typeof(IService), b, address);
            var otherHost = new ServiceHost(otherSrv, address);

            otherHost.AddServiceEndpoint(typeof(IOtherService), b, otherAddress);
            var f = new ChannelFactory<IService>(b);
            var c = f.CreateChannel(new EndpointAddress(address));

            host.Open();
            otherHost.Open();
            c.CallOtherService(otherAddress);
            wait.WaitOne();
            host.Dispose();
            otherHost.Dispose();
        }


    }


}
