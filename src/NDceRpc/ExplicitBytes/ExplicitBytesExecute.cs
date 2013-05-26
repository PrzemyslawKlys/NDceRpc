using System;

namespace NDceRpc.ExplicitBytes
{
    internal delegate uint ExplicitBytesExecute(
        IntPtr clientHandle,
    uint szInput, IntPtr input,
    out uint szOutput, out IntPtr output);
}