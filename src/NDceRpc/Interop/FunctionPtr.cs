using System;
using System.Runtime.InteropServices;
using System.Runtime.Serialization;

namespace NDceRpc.Interop
{
    public class FunctionPtr<T> : IDisposable
        //where T : Delegate
        where T : class, ICloneable, ISerializable
    {
        private T _delegate;
        public IntPtr Handle;

        public FunctionPtr(T data)
        {
            _delegate = data;
            Handle = Marshal.GetFunctionPointerForDelegate((Delegate) (object) data);
        }

        void IDisposable.Dispose()
        {
            _delegate = null;
            Handle = IntPtr.Zero;
        }
    }
}