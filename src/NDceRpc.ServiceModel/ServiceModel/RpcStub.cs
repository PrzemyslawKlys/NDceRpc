using System;
using System.Collections.Generic;

namespace NDceRpc.ServiceModel
{
    public abstract class RpcStub
    {
        protected object _singletonService;
        protected DispatchTable _operations;
        protected ServiceEndpoint _endpoint;
        public DispatchTable Operations
        {
            get { return _operations; }
        }

        protected RpcStub(object singletonService, ServiceEndpoint endpoint)
        {

            _endpoint = endpoint;
            _singletonService = singletonService;
            _operations = DispatchFactory.GetOperations(endpoint._contractType);

        }
    }
}