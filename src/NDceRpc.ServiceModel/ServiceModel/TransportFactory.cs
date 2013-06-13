using System;
using System.Collections.Generic;
using System.IO;
using System.Runtime.Serialization;
using NDceRpc.ExplicitBytes;
using NDceRpc.Interop;
using ProtoBuf.Meta;

namespace NDceRpc.ServiceModel
{
    //NOTE: this is only workaround using one pipe until resolved bu with SEH exceptioon on many client call
    public static class TransportFactory
    {
        //TODO: remove this field
        //public static Transports _transport = Transports.Rpc;

        //public static Transports Transport { get; set; }

        //TODO: move to SingleTransportBinding
        /// <summary>
        /// Where server should host await requests
        /// </summary>
        // public static Guid SingleTransportServerUuid = Guid.Empty;
        /// <summary>
        /// Where client should go for data
        /// </summary>
        // public static Guid SingleTransportClientUuid = Guid.Empty;
        // private static ExplicitBytesClient _client;
        // private static ExplicitBytesServer _server;
        // private static object _lock = new object();
        //private static Dictionary<string, ExplicitBytesRoutingClient> _routingClients = new Dictionary<string, ExplicitBytesRoutingClient>();
        // private static Dictionary<string, ExplicitBytesRoutingServer> _routingServers = new Dictionary<string, ExplicitBytesRoutingServer>();

        //private static RuntimeTypeModel _serializer = TypeModel.Create();

        static TransportFactory()
        {
            //_serializer.Add(typeof(RpcRoutedMessage), true);
           // _serializer.CompileInPlace();
        }

        public static IExplicitBytesClient CreateClient(Binding binding, Guid uuid, string address)
        {

            var bindingInfo = EndpointMapper.WcfToRpc(address);
            bindingInfo.EndPoint = CanonizeEndpoint(bindingInfo);
            var client = new ExplicitBytesClient(uuid, bindingInfo);
            
            //NOTE: applying any authentication on local IPC greatly slows down start up of many simulatanious service
            bool skipAuthentication = binding.Authentication == RpcAuthentication.RPC_C_AUTHN_NONE && bindingInfo.Protseq == RpcProtseq.ncalrpc;
            if (skipAuthentication)
            {
                client.AuthenticateAsNone();
            }
            else
            {
                client.AuthenticateAs(null, binding.Authentication == RpcAuthentication.RPC_C_AUTHN_NONE
                                                                  ? ExplicitBytesClient.Anonymous
                                                                  : ExplicitBytesClient.Self,
                                                              binding.Authentication == RpcAuthentication.RPC_C_AUTHN_NONE
                                                                  ? RpcProtectionLevel.RPC_C_PROTECT_LEVEL_NONE
                                                                  : RpcProtectionLevel.RPC_C_PROTECT_LEVEL_PKT_PRIVACY,
                                                              binding.Authentication);
            }
         
            return client;

            //NOTE: does we need such routing?
            //if (_client == null)
            //{
            //    lock (_lock)
            //    {
            //        if (_client == null)
            //        {
            //            _client = new ExplicitBytesClient(SingleTransportClientUuid, binding.ProtocolTransport, null,
            //                                       "\\pipe\\NDceRpc.ServiceModel" + SingleTransportClientUuid);
            //            _client.AuthenticateAs(null, ExplicitBytesClient.Anonymous, RpcProtectionLevel.RPC_C_PROTECT_LEVEL_NONE,
            //                                   RpcAuthentication.RPC_C_AUTHN_NONE);
            //        }
            //    }
            //}
            //lock (_routingClients)
            //{
            //    ExplicitBytesRoutingClient client;
            //    if (!_routingClients.TryGetValue(address, out client))
            //    {
            //        client = new ExplicitBytesRoutingClient(_client, address);
            //        _routingClients.Add(address, client);
            //    }
            //    return client;
            //}
        }


        public static IExplicitBytesServer CreateHost(Binding binding, string address, Guid uuid)
        {

            var host = new ExplicitBytesServer(uuid);
            var endpointBinding = EndpointMapper.WcfToRpc(address);
            string endPoint = CanonizeEndpoint(endpointBinding);
            host.AddProtocol(binding.ProtocolTransport, endPoint, (uint)binding.MaxConnections);
            host.AddAuthentication(binding.Authentication);
            return host;

            //NOTE: does we need such routing?
            //if (_server == null)
            //{
            //    lock (_lock)
            //    {
            //        if (_server == null)
            //        {
            //            _server = new ExplicitBytesServer(SingleTransportServerUuid);
            //            _server.OnExecute += (client, data) =>
            //                {

            //                    var msg = (RpcRoutedMessage)_serializer.Deserialize(new MemoryStream(data), null, typeof(RpcRoutedMessage));
            //                    ExplicitBytesRoutingServer server = null;
            //                    lock (_routingServers)
            //                    {
            //                        server = _routingServers[msg.Receiver];
            //                    }
            //                   var result  = server.ExecuteHandler(client, msg.Data);
            //                    var stream = new MemoryStream();
            //                    _serializer.Serialize(stream,new RpcRoutedMessage{Data = result,Receiver = msg.Receiver});
            //                    return stream.ToArray();
            //                };
            //            _server.AddProtocol(binding.ProtocolTransport, "\\pipe\\NDceRpc.ServiceModel" + SingleTransportServerUuid, (uint)binding.MaxConnections);
            //            _server.AddAuthentication(binding.Authentication);
            //            _server.StartListening();
            //        }
            //    }
            //}
            //lock (_routingServers)
            //{
            //    ExplicitBytesRoutingServer server;
            //    if (!_routingServers.TryGetValue(address, out server))
            //    {
            //        server = new ExplicitBytesRoutingServer();
            //        _routingServers.Add(address, server);
            //    }
            //    return server;
            //}
        }

        
        private static string CanonizeEndpoint(EndpointBindingInfo endpointBinding)
        {
           var endpoint = endpointBinding.EndPoint;
            // Windows XP has restiction on names of local transpor
           //  RPC_S_STRING_TOO_LONG is thrown on XP, but not 7
           //so generating GUID for each 
            if (endpointBinding.Protseq == RpcProtseq.ncalrpc 
                && Environment.OSVersion.Platform == PlatformID.Win32NT
                && Environment.OSVersion.Version.Major < 6
                )
            {
                //hope will not clash on Windows, can leave part of old one, but need to find out limits
                endpoint = "NET"+GuidUtility.Create(EndpointMapper.RpcNamespace, endpoint).ToString("N");
            }
            return endpoint;
        }

        internal class ExplicitBytesRoutingClient : IExplicitBytesClient
        {
            private readonly ExplicitBytesClient _client;
            private readonly string _address;


            public ExplicitBytesRoutingClient(ExplicitBytesClient client, string address)
            {

                _client = client;
                _address = address;
            }

            public byte[] Execute(byte[] arg)
            {
               // var toRoute = new RpcRoutedMessage() { Data = arg, Receiver = _address };
              //  var stream = new MemoryStream();
                //_serializer.Serialize(stream, toRoute);
                //var ret = _client.Execute(stream.ToArray());
                //var msg = (RpcRoutedMessage)_serializer.Deserialize(new MemoryStream(ret), null, typeof(RpcRoutedMessage));
               // return msg.Data;
                return null;
            }

            public void Dispose()
            {

            }


        }

        [DataContract]
        public class RpcRoutedMessage
        {
            [DataMember(Order = 1)]
            public string Receiver { get; set; }
            [DataMember(Order = 2)]
            public string Sender { get; set; }

            [DataMember(Order = 3)]
            public byte[] Data { get; set; }
        }
        public class ExplicitBytesRoutingServer : IExplicitBytesServer
        {
            public RpcExecuteHandler ExecuteHandler = null;

            public void StartListening()
            {

            }

            public void Dispose()
            {

            }

            public event RpcExecuteHandler OnExecute
            {
                add { ExecuteHandler += value; }
                remove { ExecuteHandler -= value; }
            }

        }
    }






}
