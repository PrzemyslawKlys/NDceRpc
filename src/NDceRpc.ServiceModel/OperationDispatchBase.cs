using System;
using System.Collections.Generic;
using System.Globalization;
using System.Reflection;
using System.Runtime.InteropServices;
using System.ServiceModel;

namespace NDceRpc.ServiceModel
{


    public class OperationDispatchBase
    {
        protected int _identifier;
        protected Dictionary<int, ParameterDispatch> _params = new Dictionary<int, ParameterDispatch>();

        protected OperationDispatchBase(MethodInfo methodInfo)
        {
            
            MethodInfo = methodInfo;
        }

        protected void SetIdentifier(MethodInfo methodInfo)
        {
            var dispatchId = TypeExtensions.GetCustomAttribute<DispIdAttribute>(methodInfo);
            if (dispatchId == null)
            {
                _identifier = methodInfo.MetadataToken;
            }
            else
            {
                _identifier = dispatchId.Value;
            }
        }

        public MethodInfo MethodInfo { get; set; }
        public OperationContractAttribute Operation { get; set; }
        
        
        public Dictionary<int, ParameterDispatch> Params
        {
            get { return _params; }
        }

        public int Identifier
        {
            get { return _identifier; }

        }

        private bool Equals(OperationDispatchBase other)
        {
            return _identifier == other._identifier;
        }

        public override bool Equals(object obj)
        {
            if (ReferenceEquals(null, obj)) return false;
            if (ReferenceEquals(this, obj)) return true;
            if (obj.GetType() != this.GetType()) return false;
            return Equals((OperationDispatchBase)obj);
        }

        public override int GetHashCode()
        {
            return _identifier;
        }
    }
}