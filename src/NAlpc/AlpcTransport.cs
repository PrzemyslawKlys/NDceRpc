using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading;
using System.Threading.Tasks;

namespace NAlpc
{
    public class AlpcTransport:System.ServiceModel.Channels.CommunicationObject
    {
        private string _portName;

        public AlpcTransport(string portName)
        {
            _portName = portName;
        }

        struct ChunkCarrierMessage
        {
            PORT_MESSAGE Header;

            uint Command;
            byte[] Chunk;
        }

        protected override TimeSpan DefaultCloseTimeout
        {
            get 
            { 
                // local user will not consider this time as hang or freeze
                return TimeSpan.FromSeconds(3);
            }
        }

        protected override TimeSpan DefaultOpenTimeout
        {
            get 
            {
                // local user will not consider this time as hang or freeze
                return TimeSpan.FromSeconds(3);
            }
        }

        protected override void OnAbort()
        {
            
        }

        protected override IAsyncResult OnBeginOpen(TimeSpan timeout, AsyncCallback callback, object state)
        {
            var open = Task.Factory.StartNew(() =>
            {
                var attributes = new OBJECT_ATTRIBUTES(_portName, 0);
                int status = NativeMethods.NtCreatePort(out handle, ref attributes, 100, 100, 50);
                callback(state);
            });
            open.Wait(timeout);
            return open;
            
        }

        protected override IAsyncResult OnBeginClose(TimeSpan timeout, AsyncCallback callback, object state)
        {
            return null;
        }

        protected override void OnClose(TimeSpan timeout)
        {
        }

        protected override void OnOpen(TimeSpan timeout)
        {

        }

        protected override void OnEndClose(IAsyncResult result)
        {
        }

        protected override void OnEndOpen(IAsyncResult result)
        {
        }
    }
}
