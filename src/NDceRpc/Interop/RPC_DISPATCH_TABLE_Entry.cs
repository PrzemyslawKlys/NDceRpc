using System;
using System.Runtime.InteropServices;

namespace NDceRpc.Interop
{
    [StructLayout(LayoutKind.Sequential)]
    internal struct RPC_DISPATCH_TABLE_Entry
    {
        public IntPtr DispatchMethod;
        public IntPtr Zero;
    }
}