using System.Runtime.ConstrainedExecution;
using Microsoft.Win32.SafeHandles;

namespace NAlpc
{
    public class AplcPortHandle : SafeHandleZeroOrMinusOneIsInvalid
    {
        public AplcPortHandle() : base(true)
        {
        }

        [ReliabilityContract(Consistency.WillNotCorruptState, Cer.MayFail)]
        protected override bool ReleaseHandle()
        {
            return NativeMethods.NtClose(this.handle) == 0;
        }

   
    }
}