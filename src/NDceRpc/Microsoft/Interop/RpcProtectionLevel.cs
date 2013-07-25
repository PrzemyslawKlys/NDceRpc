namespace NDceRpc.Interop
{
    /// <summary>
    /// The protection level of the communications, RPC_C_PROTECT_LEVEL_PKT_PRIVACY is 
    /// the default for authenticated communications.
    /// </summary>
    public enum RpcProtectionLevel : uint
    {
        RPC_C_PROTECT_LEVEL_DEFAULT = 0,
        RPC_C_PROTECT_LEVEL_NONE = 1,
        RPC_C_PROTECT_LEVEL_CONNECT = 2,
        RPC_C_PROTECT_LEVEL_CALL = 3,
        RPC_C_PROTECT_LEVEL_PKT = 4,
        RPC_C_PROTECT_LEVEL_PKT_INTEGRITY = 5,
        RPC_C_PROTECT_LEVEL_PKT_PRIVACY = 6,
    }
}