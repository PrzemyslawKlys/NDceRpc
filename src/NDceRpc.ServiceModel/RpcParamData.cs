using System.Runtime.Serialization;

namespace NDceRpc.ServiceModel
{
    [DataContract]
    public class RpcParamData
    {
        private byte[] _data = new byte[0];

        [DataMember(Order = 1)]
        public int Identifier { get; set; }

        [DataMember(Order = 2)]
        public byte[] Data
        {
            get { return _data; }
            set { _data = value; }
        }
    }
}