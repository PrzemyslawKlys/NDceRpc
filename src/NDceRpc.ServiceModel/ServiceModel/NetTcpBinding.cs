using NDceRpc.Interop;
using NDceRpc.Serialization;

namespace NDceRpc.ServiceModel
{
    public class NetTcpBinding : Binding
    {
        private RpcAuthentication _authentication = RpcAuthentication.RPC_C_AUTHN_WINNT;
        private BinaryObjectSerializer _serializer = new ProtobufObjectSerializer();

        internal override RpcProtseq ProtocolTransport
        {
            get { return RpcProtseq.ncacn_ip_tcp; }
        }

        public override RpcAuthentication Authentication
        {
            get { return _authentication; }
            set { _authentication = value; }
        }

        public override BinaryObjectSerializer Serializer
        {
            get { return _serializer; }
            set { _serializer = value; }
        }
    }
}