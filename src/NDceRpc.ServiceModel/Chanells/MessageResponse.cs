using System.Runtime.Serialization;

namespace NDceRpc.ServiceModel
{
    [DataContract]
    public class MessageResponse
    {
        [DataMember(Order = 1)]
        public byte[] Data { get; set; }

        [DataMember(Order = 2)]
        public RpcErrorData Error { get; set; }

    }
}