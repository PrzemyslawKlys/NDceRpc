using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.InteropServices;
using System.Text;

namespace NAlpc
{
    public class Constants
    {
        /// <summary>
        /// Maximum number of bytes that can be copied through LPC
        /// </summary>
        public const int MAX_LPC_DATA = 0x130;
    }

    /// <summary>
    ///  Valid values for PORT_MESSAGE::u2::s2::Type
    /// </summary>
    public enum PortMessageType : int
    {

        LPC_REQUEST = 1,
        LPC_REPLY = 2,
        LPC_DATAGRAM = 3,
        LPC_LOST_REPLY = 4,
        LPC_PORT_CLOSED = 5,
        LPC_CLIENT_DIED = 6,
        LPC_EXCEPTION = 7,
        LPC_DEBUG_EVENT = 8,
        LPC_ERROR_EVENT = 9,
        LPC_CONNECTION_REQUEST = 10,

        ALPC_REQUEST = 0x2000 | LPC_REQUEST,
        ALPC_CONNECTION_REQUEST = 0x2000 | LPC_CONNECTION_REQUEST
    }

    //
    // Define structure for initializing shared memory on the caller's side of the port
    [StructLayout(LayoutKind.Sequential)]
    public struct PORT_VIEW
    {

        ulong Length;                      // Size of this structure
        IntPtr SectionHandle;               // Handle to section object with
        // SECTION_MAP_WRITE and SECTION_MAP_READ
        ulong SectionOffset;               // The offset in the section to map a view for
        // the port data area. The offset must be aligned 
        // with the allocation granularity of the system.
        UIntPtr ViewSize;                    // The size of the view (in bytes)
        IntPtr ViewBase;                    // The base address of the view in the creator
        // 
        IntPtr ViewRemoteBase;              // The base address of the view in the process
        // connected to the port.
    }

    //
    // Define structure for shared memory coming from remote side of the port
    [StructLayout(LayoutKind.Sequential)]
    public struct REMOTE_PORT_VIEW
    {

        ulong Length;                      // Size of this structure
        UIntPtr ViewSize;                    // The size of the view (bytes)
        IntPtr ViewBase;                    // Base address of the view

    }

    [StructLayout(LayoutKind.Sequential)]
    public struct s1
    {
        public ushort DataLength;          // Length of data following the header (bytes)
        public ushort TotalLength;         // Length of data + sizeof(PORT_MESSAGE)
    }

    [StructLayout(LayoutKind.Explicit)]
    public struct u1
    {
        [FieldOffset(0)]
        public s1 s1;

        [FieldOffset(0)]
        public ulong Length;
    }

    [StructLayout(LayoutKind.Explicit)]
    public struct u2
    {
        [FieldOffset(0)]
        public s2 s2;

        [FieldOffset(0)]
        public ulong ZeroInit;
    }

    [StructLayout(LayoutKind.Sequential)]
    public struct s2
    {
        public ushort Type;
        public ushort DataInfoOffset;
    }

    [StructLayout(LayoutKind.Explicit)]
    public struct u3
    {
        [FieldOffset(0)]
        public CLIENT_ID ClientId;

        [FieldOffset(0)]
        public double DoNotUseThisField;// Force quadword alignment
    }

    [StructLayout(LayoutKind.Explicit)]
    public struct u4
    {
        [FieldOffset(0)]
        public IntPtr ClientViewSize;// Size of section created by the sender (in bytes)

        [FieldOffset(0)]
        public ulong CallbackId;
    }
    [StructLayout(LayoutKind.Sequential)]
    public struct CLIENT_ID
    {
        public IntPtr UniqueProcess;
        public IntPtr UniqueThread;
    }

    ///<summary>Define header for Port Message</summary>
    public struct PORT_MESSAGE
    {

        //
        // Macro for initializing the message header
        //


        public static void InitializeMessageHeader(PORT_MESSAGE ph, ushort l, ushort t)
        {
            (ph).u1.s1.TotalLength = (ushort)(l);
            unsafe
            {
                (ph).u1.s1.DataLength = (ushort)(l - sizeof(PORT_MESSAGE));
            }
            (ph).u2.s2.Type = (ushort)(t);
            (ph).u2.s2.DataInfoOffset = 0;


        }


        public u1 u1;

        public u2 u2;

        public u3 u3;

        public ulong MessageId;                   // Identifier of the particular message instance

        public u4 u4;

    }

}
