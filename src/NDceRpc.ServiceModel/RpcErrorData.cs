using System.Runtime.Serialization;

namespace NDceRpc.ServiceModel
{
    [DataContract]
    public class RpcErrorData
    {
        [DataMember(Order = 1)]
        public string Type { get; set; }
        [DataMember(Order = 2)]
        public string Message { get; set; }
    }
}