using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace NDceRpc.Interop
{

    /// <summary>
    /// 
    /// </summary>
    /// <seealso cref="RpcDce.h"/>
    public static  class CONSTANTS
    {
        public const int RPC_C_BINDING_INFINITE_TIMEOUT = 10;
        public const int RPC_C_BINDING_MIN_TIMEOUT = 0;
        public const int RPC_C_BINDING_DEFAULT_TIMEOUT = 5;
        public const int RPC_C_BINDING_MAX_TIMEOUT = 9;

        public const int RPC_C_CANCEL_INFINITE_TIMEOUT = -1;

        public const int RPC_C_LISTEN_MAX_CALLS_DEFAULT = 1234;
        public const int RPC_C_PROTSEQ_MAX_REQS_DEFAULT = 10;
    }
}
