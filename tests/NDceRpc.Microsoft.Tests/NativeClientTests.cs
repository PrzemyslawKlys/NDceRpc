using System;
using System.Net;
using NDceRpc;
using NDceRpc.Native;
using NDceRpc.Microsoft.Interop;
using NUnit.Framework;

namespace NDceRpc.Test
{
    [TestFixture]
    public class NativeClientTests
    {
        [Test]
        public void Constructor_ValidEndpointBinding_CreatesClient()
        {
            var binding = new EndpointBindingInfo(RpcProtseq.ncacn_ip_tcp, "localhost", "12345");
            
            using (var client = new NativeClient(binding))
            {
                Assert.IsNotNull(client);
                Assert.AreNotEqual(IntPtr.Zero, client.Binding);
                Assert.AreNotEqual(IntPtr.Zero, client.Handle);
            }
        }

        [Test]
        public void Constructor_DifferentProtocols_CreatesClient()
        {
            var protocols = new[]
            {
                RpcProtseq.ncacn_ip_tcp,
                RpcProtseq.ncacn_np,
                RpcProtseq.ncalrpc
            };

            foreach (var protocol in protocols)
            {
                var binding = new EndpointBindingInfo(protocol, null, "test_endpoint");
                
                using (var client = new NativeClient(binding))
                {
                    Assert.IsNotNull(client);
                    Assert.AreEqual(protocol, client.Protocol);
                }
            }
        }

        [Test]
        public void AuthenticateAs_AnonymousCredentials_DoesNotThrow()
        {
            var binding = new EndpointBindingInfo(RpcProtseq.ncacn_ip_tcp, "localhost", "12345");
            
            using (var client = new NativeClient(binding))
            {
                Assert.DoesNotThrow(() => client.AuthenticateAs(Client.Anonymous));
            }
        }

        [Test]
        public void AuthenticateAs_SelfCredentials_DoesNotThrow()
        {
            var binding = new EndpointBindingInfo(RpcProtseq.ncacn_ip_tcp, "localhost", "12345");
            
            using (var client = new NativeClient(binding))
            {
                Assert.DoesNotThrow(() => client.AuthenticateAs(Client.Self));
            }
        }

        [Test]
        public void AuthenticateAs_CustomCredentials_DoesNotThrow()
        {
            var binding = new EndpointBindingInfo(RpcProtseq.ncacn_ip_tcp, "localhost", "12345");
            var credentials = new NetworkCredential("testuser", "testpass", "testdomain");
            
            using (var client = new NativeClient(binding))
            {
                Assert.DoesNotThrow(() => client.AuthenticateAs(credentials));
            }
        }

        [Test]
        public void AuthenticateAsNone_DoesNotThrow()
        {
            var binding = new EndpointBindingInfo(RpcProtseq.ncacn_ip_tcp, "localhost", "12345");
            
            using (var client = new NativeClient(binding))
            {
                Assert.DoesNotThrow(() => client.AuthenticateAsNone());
            }
        }

        [Test]
        public void Dispose_CalledMultipleTimes_DoesNotThrow()
        {
            var binding = new EndpointBindingInfo(RpcProtseq.ncacn_ip_tcp, "localhost", "12345");
            var client = new NativeClient(binding);
            
            client.Dispose();
            Assert.DoesNotThrow(() => client.Dispose());
        }

        [Test]
        public void Equals_SameHandle_ReturnsTrue()
        {
            var binding = new EndpointBindingInfo(RpcProtseq.ncacn_ip_tcp, "localhost", "12345");
            
            using (var client1 = new NativeClient(binding))
            {
                Assert.IsTrue(client1.Equals(client1));
            }
        }

        [Test]
        public void Equals_DifferentClients_ReturnsFalse()
        {
            var binding = new EndpointBindingInfo(RpcProtseq.ncacn_ip_tcp, "localhost", "12345");
            
            using (var client1 = new NativeClient(binding))
            using (var client2 = new NativeClient(binding))
            {
                Assert.IsFalse(client1.Equals(client2));
            }
        }

        [Test]
        public void GetHashCode_ValidClient_ReturnsNonZero()
        {
            var binding = new EndpointBindingInfo(RpcProtseq.ncacn_ip_tcp, "localhost", "12345");
            
            using (var client = new NativeClient(binding))
            {
                var hashCode = client.GetHashCode();
                Assert.AreNotEqual(0, hashCode);
            }
        }
    }
}