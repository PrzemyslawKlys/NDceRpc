using System;

namespace NDceRpc.Microsoft.Interop
{

    internal static class RpcRuntime
    {
        internal static readonly bool Is64BitProcess;
        internal static readonly byte[] TYPE_FORMAT;
        internal static readonly byte[] FUNC_FORMAT;
        internal static readonly Ptr<Byte[]> FUNC_FORMAT_PTR;

        static RpcRuntime()
        {
            Is64BitProcess = (IntPtr.Size == 8);
            RpcTrace.Verbose("Is64BitProcess = {0}", Is64BitProcess);

            if (Is64BitProcess)
            {
                //Same as 32-bit except: [8] = 8; [32] = 24;
                TYPE_FORMAT = new byte[39]
                                  {
                                      0, 0, 27, 0, 1, 0, 40, 0, 8, 0, 1, 0, 2, 91, 17, 12, 8, 92, 17, 20, 2, 0, 18, 0, 2
                                      , 0, 27, 0,
                                      1, 0,
                                      40, 84, 24, 0, 1, 0, 2, 91, 0
                                  };
                //Very different from 32-bit:
                FUNC_FORMAT = new byte[61]
                                  {
                                      0, 72, 0, 0, 0, 0, 0, 0, 48, 0, 50, 0, 0, 0, 8, 0, 36, 0, 71, 5, 10, 7, 1, 0, 1, 0
                                      , 0, 0, 0, 0
                                      , 72,
                                      0, 8, 0, 8, 0, 11, 0, 16, 0, 2, 0, 80, 33, 24, 0, 8, 0, 19, 32, 32, 0, 18, 0, 112,
                                      0, 40, 0,
                                      16, 0,
                                      0
                                  };
            }
            else
            {
                TYPE_FORMAT = new byte[39]
                                  {
                                      0, 0, 27, 0, 1, 0, 40, 0, 4, 0, 1, 0, 2, 91, 17, 12, 8, 92, 17, 20, 2, 0, 18, 0, 2
                                      , 0, 27, 0,
                                      1, 0,
                                      40, 84, 12, 0, 1, 0, 2, 91, 0
                                  };
                FUNC_FORMAT = new byte[59]
                                  {
                                      0, /*104*/72, 0, 0, 0, 0, 0, 0, 24, 0, 50, 0, 0, 0, 8, 0, 36, 0, 71, 5, 8, 7, 1, 0
                                      , 1, 0, 0, 0
                                      , 72, 0, 4,
                                      0, 8, 0, 11, 0, 8, 0, 2, 0, 80, 33, 12, 0, 8, 0, 19, 32, 16, 0, 18, 0, 112, 0, 20,
                                      0, 16, 0, 0
                                  };
            }
            FUNC_FORMAT_PTR = new Ptr<byte[]>(FUNC_FORMAT);
        }

        internal static void Free(IntPtr ptr)
        {
            if (ptr != IntPtr.Zero)
            {
                RpcTrace.Verbose("LocalFree({0})", ptr);
                NativeMethods.LocalFree(ptr);
            }
        }

        private const UInt32 LPTR = 0x0040;

        internal static IntPtr Alloc(uint size)
        {
            IntPtr ptr = NativeMethods.LocalAlloc(LPTR, size);
            RpcTrace.Verbose("{0} = LocalAlloc({1})", ptr, size);
            return ptr;
        }

        internal delegate void ServerEntryPoint(IntPtr ptr);

        internal static FunctionPtr<ServerEntryPoint> ServerEntry = new FunctionPtr<ServerEntryPoint>(NativeMethods.NdrServerCall2);
        internal static FunctionPtr<LocalAlloc> AllocPtr = new FunctionPtr<LocalAlloc>(Alloc);
        internal static FunctionPtr<LocalFree> FreePtr = new FunctionPtr<LocalFree>(Free);


    }
}