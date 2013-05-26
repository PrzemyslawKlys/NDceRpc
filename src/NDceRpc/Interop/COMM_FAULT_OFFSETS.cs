using System.Runtime.InteropServices;

namespace NDceRpc.Interop
{
    [StructLayout(LayoutKind.Sequential)]
    internal struct COMM_FAULT_OFFSETS
    {
        public short CommOffset;
        public short FaultOffset;
    }
}