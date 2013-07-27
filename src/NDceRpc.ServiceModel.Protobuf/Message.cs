namespace NDceRpc.ServiceModel.Channels
{

    public partial class Message
    {
        public bool IsFault
        {
            get { return Fault != null; }
        }

    }
}