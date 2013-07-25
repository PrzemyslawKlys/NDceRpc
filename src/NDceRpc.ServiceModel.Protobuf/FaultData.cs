using System;

namespace NDceRpc.ServiceModel.Channels
{
    //BUG: this is naive implementaion, should be done more SOAP like and C++ compatible
    public partial class FaultData
    {
        //public static FaultData FromException(Exception ex,IExceptionTO)
        //{
        //    return new FaultData{Code = ex.GetType().GUID.ToString(),Reason = ex.Message,Detail = ex.StackTrace};
        //}
    }
}