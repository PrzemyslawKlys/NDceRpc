using System;
using System.Collections.Generic;
using System.Linq;
using System.ServiceModel;
using System.Text;
using System.Threading;

namespace NDceRpc.ServiceModel.Test
{
    // Simple async result implementation.
    class CompletedAsyncResult<T> : IAsyncResult
    {
        T data;

        public CompletedAsyncResult(T data)
        { this.data = data; }

        public T Data
        { get { return data; } }

        #region IAsyncResult Members
        public object AsyncState
        { get { return (object)data; } }

        public WaitHandle AsyncWaitHandle
        { get { throw new Exception("The method or operation is not implemented."); } }

        public bool CompletedSynchronously
        { get { return true; } }

        public bool IsCompleted
        { get { return true; } }
        #endregion
    }

    [ServiceContract(SessionMode = SessionMode.Required, CallbackContract = typeof(IAsyncServiceCallback))]
    public interface IAsyncService
    {
        [OperationContract(IsOneWay = false)]
        void DoSyncCall();

        [OperationContractAttribute(AsyncPattern = true)]
        IAsyncResult BeginServiceAsyncMethod(AsyncCallback callback, object asyncState);

        // Note: There is no OperationContractAttribute for the end method.
        string EndServiceAsyncMethod(IAsyncResult result);
    }

    [ServiceBehavior(InstanceContextMode = InstanceContextMode.Single)]
     class AsyncService : IAsyncService
    {
        private object _data;
        private ManualResetEvent _done;

        public AsyncService(ManualResetEvent done)
        {
            _done = done;
        }

        public void DoSyncCall()
        {
            var callback = OperationContext.Current.GetCallbackChannel<IAsyncServiceCallback>();
            var wait = callback.BeginCallback(null, null);
            wait.AsyncWaitHandle.WaitOne();
            callback.EndCallback(wait);
     
        }

        public IAsyncResult BeginServiceAsyncMethod(AsyncCallback callback, object data)
        {
            return new CompletedAsyncResult<string>("Hello");
        }

        public string EndServiceAsyncMethod(IAsyncResult asyncResult)
        {
            _done.Set();
            return ":)";
        }
    }


    public interface IAsyncServiceCallback
    {
        [OperationContract(IsOneWay = true, AsyncPattern = true)]
        IAsyncResult BeginCallback(AsyncCallback callback, object data);


        void EndCallback(IAsyncResult asyncResult);
    }

     class AsyncServiceCallback : IAsyncServiceCallback
    {
        public IAsyncResult BeginCallback(AsyncCallback callback, object data)
        {
            return new CompletedAsyncResult<string>("Hello");
        }

        public void EndCallback(IAsyncResult asyncResult)
        {

        }
    }
}
