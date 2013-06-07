using System;

namespace NDceRpc.ServiceModel
{
    public class ErrorHandler : IErrorHander
    {
        private static IErrorHander _handler = new ErrorHandler();
        public static IErrorHander Handler
        {
            get { return _handler; }
            set { _handler = value; }
        }

        public static bool Handle(Exception exception)
        {

            return Handler.Handle(exception);

        }

        bool IErrorHander.Handle(Exception exception)
        {
            return false;
        }
    }
}
