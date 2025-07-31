using System;
using NDceRpc;
using NDceRpc.Microsoft.Interop;
using NUnit.Framework;

namespace NDceRpc.Test
{
    [TestFixture]
    public class RpcExceptionTests
    {
        [Test]
        public void Constructor_Default_CreatesExceptionWithDefaultMessage()
        {
            var ex = new RpcException();
            Assert.AreEqual("Unspecified RPC error", ex.Message);
        }

        [Test]
        public void Constructor_WithInnerException_PreservesInnerException()
        {
            var inner = new InvalidOperationException("Inner error");
            var ex = new RpcException(inner);
            
            Assert.AreEqual("Unspecified RPC error", ex.Message);
            Assert.AreEqual(inner, ex.InnerException);
        }

        [Test]
        public void Constructor_WithMessage_PreservesMessage()
        {
            const string message = "Custom error message";
            var ex = new RpcException(message);
            
            Assert.AreEqual(message, ex.Message);
        }

        [Test]
        public void Constructor_WithMessageAndInnerException_PreservesBoth()
        {
            const string message = "Custom error";
            var inner = new InvalidOperationException("Inner error");
            var ex = new RpcException(message, inner);
            
            Assert.AreEqual(message, ex.Message);
            Assert.AreEqual(inner, ex.InnerException);
        }

        [Test]
        public void Constructor_WithRpcStatus_PreservesStatus()
        {
            var ex = new RpcException(RPC_STATUS.RPC_S_INVALID_BINDING);
            
            Assert.AreEqual(RPC_STATUS.RPC_S_INVALID_BINDING, ex.RpcStatus);
            Assert.AreEqual((int)RPC_STATUS.RPC_S_INVALID_BINDING, ex.NativeErrorCode);
        }

        [Test]
        public void Assert_FalseCondition_ThrowsRpcException()
        {
            Assert.Throws<RpcException>(() => RpcException.Assert(false));
        }

        [Test]
        public void Assert_TrueCondition_DoesNotThrow()
        {
            Assert.DoesNotThrow(() => RpcException.Assert(true));
        }

        [Test]
        public void RpcStatus_ReturnsCorrectStatus()
        {
            var ex = new RpcException(RPC_STATUS.RPC_S_ACCESS_DENIED);
            Assert.AreEqual(RPC_STATUS.RPC_S_ACCESS_DENIED, ex.RpcStatus);
        }
    }
}