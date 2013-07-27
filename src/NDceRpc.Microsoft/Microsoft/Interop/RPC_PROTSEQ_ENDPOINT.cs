using System;
using System.Runtime.InteropServices;

namespace NDceRpc.Interop
{
    [StructLayout(LayoutKind.Sequential)]
    internal struct RPC_PROTSEQ_ENDPOINT
    {
        public String RpcProtocolSequence;
        public String Endpoint;
    }
}