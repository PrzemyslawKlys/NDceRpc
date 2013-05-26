using System;
using System.Collections.Generic;
using System.IO;
using System.Runtime.InteropServices;
using System.Runtime.Serialization;
using MeasureIt;
using NUnit.Framework;
using ProtoBuf.Meta;

namespace NDceRpc.ServiceModel.IntegrationTests
{
    [TestFixture]
    public class StartupTimeTests
    {

        [System.ServiceModel.ServiceContract]
        [Guid("130B1570-30F3-498D-9C10-19B037A746D0")]
        public interface IService2
        {
            [System.ServiceModel.OperationContract(IsOneWay = false)]
            byte[] Execute2(byte[] arg);
        }

        [System.ServiceModel.ServiceBehavior(InstanceContextMode = System.ServiceModel.InstanceContextMode.Single)]
        private class Service2 : IService2
        {
            public byte[] Execute2(byte[] arg)
            {
                return arg;
            }
        }


        [System.ServiceModel.ServiceContract]
        [Guid("FB055ED1-8090-4A66-9EFB-1469A5336420")]
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
        public void SerializerGeneration()
        {
            FirstCallTester tester = new FirstCallTester(Console.Out);
            tester.Start();
            CreateProto();
            tester.Stop();
            tester.Start();
            CreateProto();
            tester.Stop();
            tester.Report();

            Reportwatch reportwatch = new Reportwatch();
            reportwatch.Start("Protobuf");
            var proto = ProtoBuf.Meta.TypeModel.Create();
            proto.Add(typeof (UserInfo), true);
            proto.CompileInPlace();
            
            reportwatch.stop("Protobuf");

            reportwatch.Start("Protobuf serialize");
            proto.Serialize(new MemoryStream(), CreateObj());
            reportwatch.stop("Protobuf serialize");

            reportwatch.Start("Protobuf serialize 2");
            proto.Serialize(new MemoryStream(), CreateObj());
            reportwatch.stop("Protobuf serialize 2");

            reportwatch.Start("DataContractSerializer ctor");
            DataContractSerializer xml = new DataContractSerializer(typeof(UserInfo));
            reportwatch.stop("DataContractSerializer ctor");

            reportwatch.Start("DataContractSerializer serialize");
            xml.WriteObject(new MemoryStream(),CreateObj());
            reportwatch.stop("DataContractSerializer serialize");

            reportwatch.Start("DataContractSerializer serialize 2");
            xml.WriteObject(new MemoryStream(), CreateObj());
            reportwatch.stop("DataContractSerializer serialize 2");

            reportwatch.Report("Protobuf");
            reportwatch.Report("Protobuf serialize");
            reportwatch.Report("Protobuf serialize 2");
            reportwatch.Report("DataContractSerializer ctor");
            reportwatch.Report("DataContractSerializer serialize");
            reportwatch.Report("DataContractSerializer serialize 2");
        }

        private static UserInfo CreateObj()
        {
            return new UserInfo { Entitlements = new List<string> { "GOD" } ,ProxyDetails = new ProxyDetails{ProxyUserCredentials = new UserCredentials() },Token = "1231238221=="};
        }

        private static void CreateProto()
        {
            var proto = TypeModel.Create();
            proto.Add(typeof (MessageRequest), true);
            proto.Add(typeof (MessageResponse), true);
            proto.Add(typeof (RpcParamData), true);
            proto.CompileInPlace();

        }


        [Test]
        public void NamedPipe_byteArray()
        {
            var tester = new FirstCallTester(Console.Out);
            tester.Start("1 Service");
            DoWcfHost();
            tester.Stop();          
            tester.Start("2 Service2");
            DoWcfHost2();
            tester.Stop();
            tester.Start("3 Service2");
            DoWcfHost2();
            tester.Stop();
            tester.Start("4 Service2");
            DoWcfHost2();
            tester.Stop();
            tester.Report();
        }

        private static void DoWcfHost()
        {
            var reportWatch = new MeasureIt.Reportwatch();

            reportWatch.Start("ServiceHost ctor");
            using (var server = new ServiceHost(new Service(), new Uri("net.pipe://127.0.0.1/testpipename")))
            {
                reportWatch.stop("ServiceHost ctor");

                reportWatch.Start("AddServiceEndpoint");
                var binding = new NetNamedPipeBinding {MaxConnections = 5};
                server.AddServiceEndpoint(typeof (IService), binding, "net.pipe://127.0.0.1/testpipename");
                reportWatch.stop("AddServiceEndpoint");

                reportWatch.Start("Open");
                server.Open();
                reportWatch.stop("Open");

                reportWatch.Start("ChannelFactory ctor");
                using (var channelFactory = new ChannelFactory<IService>(binding))
                {
                    reportWatch.stop("ChannelFactory ctor");

                    reportWatch.Start("CreateChannel");
                    var client = channelFactory.CreateChannel(new EndpointAddress("net.pipe://127.0.0.1/testpipename"));
                    reportWatch.stop("CreateChannel");

                    reportWatch.Start("Execute");
                    client.Execute(new byte[0]);
                    reportWatch.stop("Execute");
                }
            }
            reportWatch.Report("ServiceHost ctor");
            reportWatch.Report("AddServiceEndpoint");
            reportWatch.Report("Open");
            reportWatch.Report("ChannelFactory ctor");
            reportWatch.Report("CreateChannel");
            reportWatch.Report("Execute");
        }

        private static void DoWcfHost2()
        {
            var reportWatch = new MeasureIt.Reportwatch();

            DoWcfHost2Internal(reportWatch);
            reportWatch.Report("ServiceHost ctor");
            reportWatch.Report("AddServiceEndpoint");
            reportWatch.Report("Open");
            reportWatch.Report("ChannelFactory ctor");
            reportWatch.Report("CreateChannel");
            reportWatch.Report("Execute");
        }

        private static void DoWcfHost2Internal(Reportwatch reportWatch)
        {
            reportWatch.Start("ServiceHost ctor");
            using (var server = new ServiceHost(new Service2(), new Uri("net.pipe://127.0.0.1/testpipename")))
            {
                reportWatch.stop("ServiceHost ctor");

                reportWatch.Start("AddServiceEndpoint");
                var binding = new NetNamedPipeBinding {MaxConnections = 5};
                server.AddServiceEndpoint(typeof (IService2), binding, "net.pipe://127.0.0.1/testpipename");
                reportWatch.stop("AddServiceEndpoint");

                reportWatch.Start("Open");
                server.Open();
                reportWatch.stop("Open");

                reportWatch.Start("ChannelFactory ctor");
                using (var channelFactory = new NDceRpc.ServiceModel.ChannelFactory<IService2>(binding))
                {
                    reportWatch.stop("ChannelFactory ctor");

                    reportWatch.Start("CreateChannel");
                    var client = channelFactory.CreateChannel(new EndpointAddress("net.pipe://127.0.0.1/testpipename"));
                    reportWatch.stop("CreateChannel");

                    reportWatch.Start("Execute");
                    client.Execute2(new byte[0]);
                    reportWatch.stop("Execute");
                }
            }
        }
    }
}
