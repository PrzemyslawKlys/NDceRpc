using System.Runtime.InteropServices;

namespace NAlpc
{
    [StructLayout(LayoutKind.Sequential)]
    public struct SECURITY_QUALITY_OF_SERVICE
    {
        public uint Length;
        public short ImpersonationLevel;
        public bool ContextTrackingMode;
        public bool EffectiveOnly;

        public static SECURITY_QUALITY_OF_SERVICE Create(short SecurityImpersonation, bool EffectiveOnly, bool DynamicTracking)
        {
            SECURITY_QUALITY_OF_SERVICE SecurityQos = new SECURITY_QUALITY_OF_SERVICE();
            unsafe
            {
                SecurityQos.Length = (uint)sizeof(SECURITY_QUALITY_OF_SERVICE);    
            }
            
            SecurityQos.ImpersonationLevel = SecurityImpersonation;
            SecurityQos.EffectiveOnly = EffectiveOnly;
            SecurityQos.ContextTrackingMode = DynamicTracking;
            return SecurityQos;
        }
    };
}