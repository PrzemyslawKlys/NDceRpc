using System;
using System.Collections;
using System.IO;
using System.ServiceModel;
using System.ServiceModel.Dispatcher;
using System.Threading;
using NDceRpc.ExplicitBytes;

namespace NDceRpc.ServiceModel
{
    /// <summary>
    /// 
    /// </summary>
    public class ServiceHostBase : IDisposable
    {
        private TimeSpan closeTimeout = RpcServiceDefaults.ServiceHostCloseTimeout;

        /// <summary>
        /// Gets or sets the interval of time allowed for the service host to close.
        /// </summary>
        /// 
        /// <returns>
        /// The <see cref="T:System.Timespan"/> that specifies the interval of time allowed for the service host to close.
        /// </returns>
        /// <exception cref="T:System.ArgumentOutOfRangeException">The value, in milliseconds, is less than zero or is larger than <see cref="F:System.Int32.MaxValue"/> (2,147,483,647 or, in hexadecimal notation, 0X7FFFFFFF).</exception><exception cref="T:System.InvalidOperationException">The host is in an <see cref="F:System.ServiceModel.CommunicationState.Opening"/> or <see cref="F:System.ServiceModel.CommunicationState.Closing"/> state and cannot be modified.</exception><exception cref="T:System.ObjectDisposedException">The host is already in a <see cref="F:System.ServiceModel.CommunicationState.Closed"/> state and cannot be modified.</exception><exception cref="T:System.ServiceModel.CommunicationObjectFaultedException">The host is in a <see cref="F:System.ServiceModel.CommunicationState.Faulted"/> state and cannot be modified.</exception>
        public TimeSpan CloseTimeout
        {
            get
            {
                return this.closeTimeout;
            }
            set
            {
                lock (this.ThisLock)
                {
                    this.closeTimeout = value;
                }
            }
        }

        protected object ThisLock
        {
            get { return _thisLock; }

        }

        protected Uri _baseAddress;
        private ManualResetEvent _opened = new ManualResetEvent(false);
        protected object _service;
        protected IExplicitBytesServer _host;
        protected bool _disposed;
        protected RpcServerStub _serverStub;
        protected ConcurrencyMode _concurrency = ConcurrencyMode.Single;
        private object _thisLock = new object();
        private ManualResetEvent _operationPending = new ManualResetEvent(true);
        private ServiceEndpoint _endpoint;
        private static System.Collections.ArrayList _gcRoot = new ArrayList();

        protected ServiceEndpoint AddEndpoint(Type contractType, Binding binding, string address, Guid uuid)
        {
            var uri = new Uri(address, UriKind.RelativeOrAbsolute);
            if (!uri.IsAbsoluteUri)
            {
                address = _baseAddress + address;
            }
            _endpoint = new ServiceEndpoint(binding, contractType, address, uuid);
            return _endpoint;
        }



        private byte[] DoRequest(IRpcCallInfo call, Type contractType, byte[] arg)
        {
            var messageRequest = (MessageRequest)_endpoint._serializer.ReadObject(new MemoryStream(arg), typeof(MessageRequest));
            var response = _serverStub.Invoke(call, messageRequest, contractType);
            var stream = new MemoryStream();
            _endpoint._serializer.WriteObject(stream, response);
            return stream.ToArray();
        }

        public void Open()
        {


            ThreadPool.QueueUserWorkItem(x =>
                {
                    try
                    {
                        if (_host == null)
                        {
                            RpcExecuteHandler onExecute =
    delegate(IRpcCallInfo client, byte[] arg)
    {

        if (_concurrency == ConcurrencyMode.Single)
        {
            lock (_serverStub)
            {
                _operationPending.Reset();
                try
                {
                    return DoRequest(client, _endpoint._contractType, arg);
                }
                finally
                {
                    _operationPending.Set();
                }
            }
        }
        _operationPending.Reset();
        try
        {
            return DoRequest(client, _endpoint._contractType, arg);
        }
        finally
        {
            _operationPending.Set();
        }
    };
                            _host = TransportFactory.CreateHost(_endpoint._binding, _endpoint._address, _endpoint._uuid);
                            //TODO: make GC root disposable
                            lock (_gcRoot.SyncRoot)
                            {
                                _gcRoot.Add(this);
                                _gcRoot.Add(_host);
                            }

                            _host.OnExecute += onExecute;
                            _host.StartListening();
                        }
                        _opened.Set();
                    }
                    catch (Exception ex)
                    {
                        bool handled = ExceptionHandler.AlwaysHandle.HandleException(ex);
                        if (!handled) throw;
                    }

                });
            _opened.WaitOne();

        }

        public void Dispose()
        {
            if (_disposed)
            {
                return;
            }

            if (_host != null)
            {
                _operationPending.WaitOne(CloseTimeout);
                _host.Dispose();
                _host = null;
            }
            _disposed = true;
        }

        public void Stop()
        {
            Dispose();
        }

        public void Close()
        {
            this.Dispose();
        }
    }
}