using System.IO.Pipes;
using System.Threading;
using NDceRpc.ExplicitBytes;

namespace NDceRpc.ServiceModel.Custom
{
    public class PipeServer : IExplicitBytesServer
    {
        private readonly string _address;
        private NamedPipeServerStream _server;

        public PipeServer(string address)
        {
            _address = address;
        }

        public void StartListening()
        {
            _server = new NamedPipeServerStream(_address, PipeDirection.InOut, 1, PipeTransmissionMode.Message,
                                                PipeOptions.Asynchronous);
            ThreadPool.QueueUserWorkItem(x => _server.WaitForConnection());


        }

        public void Dispose()
        {
          
        }

        public event RpcExecuteHandler OnExecute;
    }
}
