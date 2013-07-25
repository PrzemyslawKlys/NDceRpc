using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.InteropServices;
using System.Text;

namespace NDceRpc.Interop.Async
{
    public static class NativeMethods
    {
        [DllImport("Rpcrt4.dll",
  CallingConvention = CallingConvention.StdCall,
  CharSet = CharSet.Unicode, SetLastError = true)]
        public extern static RPC_STATUS RpcAsyncInitializeHandle(
            /* PRPC_ASYNC_STATE pAsync*/
                     ref RPC_ASYNC_STATE pAsync,
            /* unsigned int Size*/
         ushort Size
       );
    }
}
