using System;

namespace NDceRpc.Native
{
    /// <summary>
    /// Hosts native server interfaces provided by unmanaged code as pointer.
    /// </summary>
    public class NativeServer:Server
    {
        public NativeServer(IntPtr sIfHandle)
        {
            base.ServerRegisterInterface(sIfHandle, _handle);
        }
    }
}