using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace NAlpc
{
    public static class NativeMethods
    {
//        /*++

//    NtCreatePort
//    ============

//    Creates a LPC port object. The creator of the LPC port becomes a server
//    of LPC communication

//    PortHandle - Points to a variable that will receive the
//        port object handle if the call is successful.

//    ObjectAttributes - Points to a structure that specifies the object’s
//        attributes. OBJ_KERNEL_HANDLE, OBJ_OPENLINK, OBJ_OPENIF, OBJ_EXCLUSIVE,
//        OBJ_PERMANENT, and OBJ_INHERIT are not valid attributes for a port object.

//    MaxConnectionInfoLength - The maximum size, in bytes, of data that can
//        be sent through the port.

//    MaxMessageLength - The maximum size, in bytes, of a message
//        that can be sent through the port.

//    MaxPoolUsage - Specifies the maximum amount of NonPaged pool that can be used for
//        message storage. Zero means default value.

//    ZwCreatePort verifies that (MaxDataSize <= 0x104) and (MaxMessageSize <= 0x148).
 
//--*/

//NTSYSAPI
//NTSTATUS
//NTAPI
//NtCreatePort(
//    OUT PHANDLE PortHandle,                     
//    IN  POBJECT_ATTRIBUTES ObjectAttributes,
//    IN  ULONG MaxConnectionInfoLength,
//    IN  ULONG MaxMessageLength,
//    IN  ULONG MaxPoolUsage
//    );

//NTSYSAPI
//NTSTATUS
//NTAPI
//ZwCreatePort(
//    OUT PHANDLE PortHandle,                     
//    IN  POBJECT_ATTRIBUTES ObjectAttributes,
//    IN  ULONG MaxConnectionInfoLength,
//    IN  ULONG MaxMessageLength,
//    IN  ULONG MaxPoolUsage
//    );


///*++

//    NtConnectPort
//    =============

//    Creates a port connected to a named port (cliend side).

//    PortHandle - A pointer to a variable that will receive the client
//        communication port object handle value.

//    PortName - Points to a structure that specifies the name
//        of the port to connect to.

//    SecurityQos - Points to a structure that specifies the level
//        of impersonation available to the port listener.

//    ClientView - Optionally points to a structure describing
//        the shared memory region used to send large amounts of data
//        to the listener; if the call is successful, this will be updated.

//    ServerView - Optionally points to a caller-allocated buffer
//        or variable that receives information on the shared memory region
//        used by the listener to send large amounts of data to the
//        caller.

//    MaxMessageLength - Optionally points to a variable that receives the size,
//        in bytes, of the largest message that can be sent through the port.

//    ConnectionInformation - Optionally points to a caller-allocated
//        buffer or variable that specifies connect data to send to the listener,
//        and receives connect data sent by the listener.

//    ConnectionInformationLength - Optionally points to a variable that
//        specifies the size, in bytes, of the connect data to send
//        to the listener, and receives the size of the connect data
//        sent by the listener.

//--*/
 
//NTSYSAPI
//NTSTATUS
//NTAPI
//NtConnectPort(
//    OUT PHANDLE PortHandle,
//    IN  PUNICODE_STRING PortName,
//    IN  PSECURITY_QUALITY_OF_SERVICE SecurityQos,
//    IN  OUT PPORT_VIEW ClientView OPTIONAL,
//    OUT PREMOTE_PORT_VIEW ServerView OPTIONAL,
//    OUT PULONG MaxMessageLength OPTIONAL,
//    IN  OUT PVOID ConnectionInformation OPTIONAL,
//    IN  OUT PULONG ConnectionInformationLength OPTIONAL
//    );


//NTSYSAPI
//NTSTATUS
//NTAPI
//ZwConnectPort(
//    OUT PHANDLE PortHandle,
//    IN  PUNICODE_STRING PortName,
//    IN  PSECURITY_QUALITY_OF_SERVICE SecurityQos,
//    IN  OUT PPORT_VIEW ClientView OPTIONAL,
//    OUT PREMOTE_PORT_VIEW ServerView OPTIONAL,
//    OUT PULONG MaxMessageLength OPTIONAL,
//    IN  OUT PVOID ConnectionInformation OPTIONAL,
//    IN  OUT PULONG ConnectionInformationLength OPTIONAL
//    );


///*++

//    NtListenPort
//    ============

//    Listens on a port for a connection request message on the server side.

//    PortHandle - A handle to a port object. The handle doesn't need 
//        to grant any specific access.

//    ConnectionRequest - Points to a caller-allocated buffer
//        or variable that receives the connect message sent to
//        the port.

//--*/


//NTSYSAPI
//NTSTATUS
//NTAPI
//NtListenPort(
//    IN  HANDLE PortHandle,
//    OUT PPORT_MESSAGE RequestMessage
//    );
 
//NTSYSAPI
//NTSTATUS
//NTAPI
//ZwListenPort(
//    IN  HANDLE PortHandle,
//    OUT PPORT_MESSAGE RequestMessage
//    );

///*++

//    NtAcceptConnectPort
//    ===================

//    Accepts or rejects a connection request on the server side.

//    PortHandle - Points to a variable that will receive the port object
//        handle if the call is successful.

//    PortContext - A numeric identifier to be associated with the port.

//    ConnectionRequest - Points to a caller-allocated buffer or variable
//        that identifies the connection request and contains any connect
//        data that should be returned to requestor of the connection

//    AcceptConnection - Specifies whether the connection should
//        be accepted or not

//    ServerView - Optionally points to a structure describing
//        the shared memory region used to send large amounts of data to the
//        requestor; if the call is successful, this will be updated

//    ClientView - Optionally points to a caller-allocated buffer
//        or variable that receives information on the shared memory
//        region used by the requestor to send large amounts of data to the
//        caller

//--*/


//NTSYSAPI
//NTSTATUS
//NTAPI
//NtAcceptConnectPort(
//    OUT PHANDLE PortHandle,
//    IN  PVOID PortContext OPTIONAL,
//    IN  PPORT_MESSAGE ConnectionRequest,
//    IN  BOOLEAN AcceptConnection,
//    IN  OUT PPORT_VIEW ServerView OPTIONAL,
//    OUT PREMOTE_PORT_VIEW ClientView OPTIONAL
//    );

//NTSYSAPI
//NTSTATUS
//NTAPI
//ZwAcceptConnectPort(
//    OUT PHANDLE PortHandle,
//    IN  PVOID PortContext OPTIONAL,
//    IN  PPORT_MESSAGE ConnectionRequest,
//    IN  BOOLEAN AcceptConnection,
//    IN  OUT PPORT_VIEW ServerView OPTIONAL,
//    OUT PREMOTE_PORT_VIEW ClientView OPTIONAL
//    );


///*++

//    NtCompleteConnectPort
//    =====================

//    Completes the port connection process on the server side.

//    PortHandle - A handle to a port object. The handle doesn't need 
//        to grant any specific access.

//--*/


//NTSYSAPI
//NTSTATUS
//NTAPI
//NtCompleteConnectPort(
//    IN  HANDLE PortHandle
//    );


//NTSYSAPI
//NTSTATUS
//NTAPI
//ZwCompleteConnectPort(
//    IN  HANDLE PortHandle
//    );


///*++

//    NtRequestPort
//    =============

//    Sends a request message to a port (client side)

//    PortHandle - A handle to a port object. The handle doesn't need 
//        to grant any specific access.

//    RequestMessage - Points to a caller-allocated buffer or variable
//        that specifies the request message to send to the port.

//--*/

//NTSYSAPI
//NTSTATUS
//NTAPI
//NtRequestPort (
//    IN  HANDLE PortHandle,
//    IN  PPORT_MESSAGE RequestMessage
//    );

//NTSYSAPI
//NTSTATUS
//NTAPI
//ZwRequestPort (
//    IN  HANDLE PortHandle,
//    IN  PPORT_MESSAGE RequestMessage
//    );

///*++

//    NtRequestWaitReplyPort
//    ======================

//    Sends a request message to a port and waits for a reply (client side)

//    PortHandle - A handle to a port object. The handle doesn't need 
//        to grant any specific access.

//    RequestMessage - Points to a caller-allocated buffer or variable
//        that specifies the request message to send to the port.

//    ReplyMessage - Points to a caller-allocated buffer or variable
//        that receives the reply message sent to the port.

//--*/

//NTSYSAPI
//NTSTATUS
//NTAPI
//NtRequestWaitReplyPort(
//    IN  HANDLE PortHandle,
//    IN  PPORT_MESSAGE RequestMessage,
//    OUT PPORT_MESSAGE ReplyMessage
//    );


//NTSYSAPI
//NTSTATUS
//NTAPI
//ZwRequestWaitReplyPort(
//    IN  HANDLE PortHandle,
//    IN  PPORT_MESSAGE RequestMessage,
//    OUT PPORT_MESSAGE ReplyMessage
//    );


///*++

//    NtReplyPort
//    ===========

//    Sends a reply message to a port (Server side)

//    PortHandle - A handle to a port object. The handle doesn't need 
//        to grant any specific access.

//    ReplyMessage - Points to a caller-allocated buffer or variable
//        that specifies the reply message to send to the port.

//--*/


//NTSYSAPI
//NTSTATUS
//NTAPI
//NtReplyPort(
//    IN  HANDLE PortHandle,
//    IN  PPORT_MESSAGE ReplyMessage
//    );

//NTSYSAPI
//NTSTATUS
//NTAPI
//ZwReplyPort(
//    IN  HANDLE PortHandle,
//    IN  PPORT_MESSAGE ReplyMessage
//    );

///*++

//    NtReplyWaitReplyPort
//    ====================

//    Sends a reply message to a port and waits for a reply message

//    PortHandle - A handle to a port object. The handle doesn't need 
//        to grant any specific access.

//    ReplyMessage - Points to a caller-allocated buffer or variable
//        that specifies the reply message to send to the port.

//--*/

//NTSYSAPI
//NTSTATUS
//NTAPI
//NtReplyWaitReplyPort(
//    IN  HANDLE PortHandle,
//    IN  OUT PPORT_MESSAGE ReplyMessage
//    );

//NTSYSAPI
//NTSTATUS
//NTAPI
//ZwReplyWaitReplyPort(
//    IN  HANDLE PortHandle,
//    IN  OUT PPORT_MESSAGE ReplyMessage
//    );

///*++

//    NtReplyWaitReceivePort
//    ======================

//    Optionally sends a reply message to a port and waits for a
//    message

//    PortHandle - A handle to a port object. The handle doesn't need 
//        to grant any specific access.

//    PortContext - Optionally points to a variable that receives
//        a numeric identifier associated with the port.

//    ReplyMessage - Optionally points to a caller-allocated buffer
//        or variable that specifies the reply message to send to the port.

//    ReceiveMessage - Points to a caller-allocated buffer or variable
//        that receives the message sent to the port.

//--*/

//NTSYSAPI
//NTSTATUS
//NTAPI
//NtReplyWaitReceivePort(
//    IN  HANDLE PortHandle,
//    OUT PVOID *PortContext OPTIONAL,
//    IN  PPORT_MESSAGE ReplyMessage OPTIONAL,
//    OUT PPORT_MESSAGE ReceiveMessage
//    );

//NTSYSAPI
//NTSTATUS
//NTAPI
//ZwReplyWaitReceivePort(
//    IN  HANDLE PortHandle,
//    OUT PVOID *PortContext OPTIONAL,
//    IN  PPORT_MESSAGE ReplyMessage OPTIONAL,
//    OUT PPORT_MESSAGE ReceiveMessage
//    );
    }
}
