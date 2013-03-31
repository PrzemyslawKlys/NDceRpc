using NDceRpc.Interop;

namespace NDceRpc.ServiceModel
{
    /// <summary>
    /// 
    /// </summary>
    public class NetNamedPipeBinding : Binding
    {
        private RpcAuthentication _authentication = RpcAuthentication.RPC_C_AUTHN_NONE;
        private BinaryObjectSerializer _serializer = new ProtobufObjectSerializer();

        internal override RpcProtseq ProtocolTransport
        {
            get { return RpcProtseq.ncacn_np; }
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