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


        // Valid values for PORT_MESSAGE::u2::s2::Type
        const ushort LPC_REQUEST = 1;
        const ushort LPC_REPLY = 2;
        const ushort LPC_DATAGRAM = 3;
        const ushort LPC_LOST_REPLY = 4;
        const ushort LPC_PORT_CLOSED = 5;
        const ushort LPC_CLIENT_DIED = 6;
        const ushort LPC_EXCEPTION = 7;
        const ushort LPC_DEBUG_EVENT = 8;
        const ushort LPC_ERROR_EVENT = 9;
        const ushort LPC_CONNECTION_REQUEST = 10;
        const ushort ALPC_REQUEST = 0x2000 | LPC_REQUEST;
        const ushort ALPC_CONNECTION_REQUEST = 0x2000 | LPC_CONNECTION_REQUEST;
    }


}
