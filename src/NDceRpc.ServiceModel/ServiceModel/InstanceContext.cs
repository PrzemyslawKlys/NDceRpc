using System;
using System.Diagnostics;
using System.Linq;
using System.ServiceModel;
using System.Threading;

namespace NDceRpc.ServiceModel
{
    //RpcTwoChannelInstanseContext
    public class InstanceContext
    {
        internal readonly object _contextObject;
        private CallbackServiceHost _callbackServer;
        private Type _typeOfService;
        private string _serverAddress;

        private string _session;
        private bool _started;
        private Binding _binding;


        public InstanceContext(object contextObject)
        {
            _contextObject = contextObject;
        }

        public void Close()
        {

        }

   
        //TODO: implement polling duplex
        //TODO: implement single channell duplex
        //context.CreateClientStub(_typeOfService);
        //ThreadPool.QueueUserWorkItem((x) =>
        //    {
        //        while (!_disposed)
        //        {
        //            var request = new MessageRequest {Callback = true};
        //            var stream = new MemoryStream();
        //            _serializer.WriteObject(stream, request);
        //            var result = _client.Execute(stream.ToArray());
        //            var response = (MessageResponse)_serializer.ReadObject(new MemoryStream(result),
        //                                                                 typeof (MessageResponse));
        //            context.Invoke(response);
        //        }
        //    });
        internal void Initialize(Type typeOfService, string serverAddress, Binding binding, string session)
        {
            if (!_started)
            {
                var opened = new ManualResetEvent(false);
                ThreadPool.QueueUserWorkItem(x =>
                    {
                        _typeOfService = typeOfService;
                        _serverAddress = serverAddress;
                        _binding = binding;
                        _session = session;
                        var contract = _typeOfService.GetCustomAttributes(typeof(ServiceContractAttribute), false).Single() as ServiceContractAttribute;
                        Debug.Assert(contract.CallbackContract != null);
                        //BUG: should be used only for non polling HTTM, all others should go via provided single channel
                        var address = _serverAddress + _session.Replace("-","");
                        _callbackServer = new CallbackServiceHost(_contextObject, address);
                        _callbackServer.AddServiceEndpoint(contract.CallbackContract, new Guid(_session), _binding, address);
                        _callbackServer.Open();
                        
                        opened.Set();
                    });
                opened.WaitOne();

            }
            _started = true;
        }
    }
}