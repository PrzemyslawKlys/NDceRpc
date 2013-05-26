using System;

namespace NDceRpc.ServiceModel
{
    public interface IErrorHander
    {
        bool Handle(Exception exception);
    }
}