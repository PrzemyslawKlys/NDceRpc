using System.Runtime.Serialization;

namespace NDceRpc.ServiceModel
{
    [DataContract]
    public class MessageRequest
    {
        private RpcParamData[] _data = new RpcParamData[0];

        [DataMember(Order = 1)]
        public int Operation { get; set; }

        [DataMember(Order = 2)]
        public RpcParamData[] Data
        {
            get { return _data; }
            set { _data = value; }
        }

        [DataMember(Order = 3)]
        public string Session { get; set; }

  
    }
}