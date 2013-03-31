

using System;
using System.Runtime.InteropServices;

namespace NDceRpc.Interop
{
    [StructLayout(LayoutKind.Sequential)]
    public struct RPC_VERSION
    {
        public ushort MajorVersion;
        public ushort MinorVersion;
    }
}