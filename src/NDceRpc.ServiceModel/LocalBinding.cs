using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using NDceRpc.Interop;

namespace NDceRpc.ServiceModel
{
    /// <summary>
    /// Uses http://en.wikipedia.org/wiki/Local_Procedure_Call on Windows
    /// </summary>
    public class LocalBinding : Binding
    {
        private RpcAuthentication _authentication = RpcAuthentication.RPC_C_AUTHN_NONE;
        private BinaryObjectSerializer _serializer = new ProtobufObjectSerializer();

        internal override RpcProtseq ProtocolTransport
        {
            get { return RpcProtseq.ncalrpc; }
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
