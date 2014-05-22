using System;
using System.Runtime.InteropServices;
using System.ServiceModel;
using NDceRpc.ServiceModel.Test;
using NUnit.Framework;

namespace NDceRpc.ServiceModel.Tests
{
    [TestFixture]
    public class DispatchTableFactoryTests
    {


 

        [Test]
        public void GetOperations_iterfacesWithOperationsWithoudId()
        {
            var abc = DispatchTableFactory.GetOperations(typeof (IABC));
        }

        [Test]
        public void GetOperations_iterfacesWithOperationsWithId()
        {
            var abcd = DispatchTableFactory.GetOperations(typeof(IABCD));
        }

    }
}