using System;

namespace NDceRpc.ServiceModel
{
    //TODO: replace it with WCF strategy - how to tackle errors in threads
    public class ObsoleteErrorHandler 
    {
        private static ObsoleteErrorHandler _handler = new ObsoleteErrorHandler();
        public static ObsoleteErrorHandler Handler
        {
            get { return _handler; }
            set { _handler = value; }
        }

        public static bool Handle(Exception exception)
        {

            return false;

        }
        
    }
}
