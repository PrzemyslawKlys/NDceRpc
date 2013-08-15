using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.InteropServices;
using System.Text;

namespace NAlpc
{
    [StructLayout(LayoutKind.Sequential)]
    public struct LPCP_PORT_OBJECT
    {
        public IntPtr ConnectionPort;
        public IntPtr ConnectedPort;
        public LPCP_PORT_QUEUE MsgQueue;
        public CLIENT_ID Creator;
        public IntPtr ClientSectionBase;
        public IntPtr ServerSectionBase;
        public IntPtr PortContext;
        public IntPtr ClientThread;
        public SECURITY_QUALITY_OF_SERVICE SecurityQos;
        public SECURITY_CLIENT_CONTEXT StaticSecurity;
        public LIST_ENTRY LpcReplyChainHead;
        public LIST_ENTRY LpcDataInfoChainHead;

        //      union
        //{
        //     PEPROCESS ServerProcess;
        //     PEPROCESS MappingProcess;
        //};
        public IntPtr MappingProcess;

        public ushort MaxMessageLength;
        public ushort MaxConnectionInfoLength;
        public uint Flags;
        public KEVENT WaitEvent;

        //TEMP: buffer
        public decimal b1;
        public decimal b2;
        public decimal b3;
        public decimal b5;
        public decimal b6;
        public decimal b7;
        public decimal b8;
        public decimal b9;
        public decimal b10;
        public decimal b11;
        public decimal b12;
        public decimal b13;
        public decimal b14;
        public decimal b15;
        public decimal b16;
        public decimal b17;
        public decimal b18;

    }
}
