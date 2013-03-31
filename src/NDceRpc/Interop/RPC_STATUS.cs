namespace NDceRpc.Interop
{
    /// <summary> WIN32 RPC Error Codes </summary>
    /// <seealso href="http://msdn.microsoft.com/en-us/library/windows/desktop/ms681386.aspx"/>
    public enum RPC_STATUS : uint
    {
        RPC_S_OK = 0,
        RPC_S_INVALID_ARG = 87,
        RPC_S_OUT_OF_MEMORY = 14,
        RPC_S_OUT_OF_THREADS = 164,
        RPC_S_INVALID_LEVEL = 87,
        RPC_S_BUFFER_TOO_SMALL = 122,
        RPC_S_INVALID_SECURITY_DESC = 1338,
        RPC_S_ACCESS_DENIED = 5,
        RPC_S_SERVER_OUT_OF_MEMORY = 1130,
        RPC_S_ASYNC_CALL_PENDING = 997,
        RPC_S_UNKNOWN_PRINCIPAL = 1332,
        RPC_S_TIMEOUT = 1460,
        RPC_S_ALREADY_REGISTERED = 1711,
        RPC_S_TYPE_ALREADY_REGISTERED = 1712,
        RPC_S_ALREADY_LISTENING = 1713,
        RPC_S_NO_PROTSEQS_REGISTERED = 1714,
        RPC_S_NOT_LISTENING = 1715,
        RPC_S_DUPLICATE_ENDPOINT = 1740,
        RPC_S_BINDING_HAS_NO_AUTH = 1746,
        RPC_S_CANNOT_SUPPORT = 1764,
        /// <summary>
        /// The string is too long.
        /// </summary>
        RPC_S_STRING_TOO_LONG = 1743 ,
        RPC_E_FAIL = 0x80004005u
    }
}