using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using NUnit.Framework;

namespace NDceRpc.ServiceModel.Tests
{
    [TestFixture]
    public class NDceRpcServiceModelTests
    {
        [Test]
        public void ServiceContract_create_defaultsAreEqual()
        {
            NDceRpc.ServiceModel.ServiceContractAttribute my = new NDceRpc.ServiceModel.ServiceContractAttribute();
            System.ServiceModel.ServiceContractAttribute wcf = new System.ServiceModel.ServiceContractAttribute();

            Assert.AreEqual(my.CallbackContract,wcf.CallbackContract);
            Assert.AreEqual(my.ConfigurationName, wcf.ConfigurationName);
            Assert.AreEqual(my.HasProtectionLevel, wcf.HasProtectionLevel);
            Assert.AreEqual(my.Name, wcf.Name);
            Assert.AreEqual(my.Namespace, wcf.Namespace);
            Assert.AreEqual(my.ProtectionLevel, wcf.ProtectionLevel);
            Assert.AreEqual((int)my.SessionMode, (int)wcf.SessionMode);
            Assert.AreEqual(my.IsDefaultAttribute(), wcf.IsDefaultAttribute());
        }

        [Test]
        public void OperationContract_create_defaultsAreEqual()
        {
            NDceRpc.ServiceModel.OperationContractAttribute my = new NDceRpc.ServiceModel.OperationContractAttribute();
            System.ServiceModel.OperationContractAttribute wcf = new System.ServiceModel.OperationContractAttribute();

            Assert.AreEqual(my.Action, wcf.Action);
            Assert.AreEqual(my.AsyncPattern, wcf.AsyncPattern);
            Assert.AreEqual(my.HasProtectionLevel, wcf.HasProtectionLevel);
            Assert.AreEqual(my.IsInitiating, wcf.IsInitiating);
            Assert.AreEqual(my.IsOneWay, wcf.IsOneWay);
            Assert.AreEqual(my.IsTerminating, wcf.IsTerminating);
            Assert.AreEqual(my.Name, wcf.Name);
            Assert.AreEqual(my.ProtectionLevel, wcf.ProtectionLevel);
            Assert.AreEqual(my.ReplyAction, wcf.ReplyAction);
        }
    }
}
