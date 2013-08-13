using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Linq;
using System.Reflection;
using System.Runtime.InteropServices;
using System.Text;
using System.Threading.Tasks;
using NUnit.Framework;

namespace NAlpc.Tests
{
    [TestFixture]
    public class NativeMethodsTests
    {
        [Test]
        public void NtCreatePort()
        {
            var name = "\\" + this.GetType().Name + MethodBase.GetCurrentMethod().Name;
            NAlpc.AplcPortHandle handle = null;
            var attributes = new OBJECT_ATTRIBUTES(name, 0);
            int status = NativeMethods.NtCreatePort(out handle, ref attributes, 100, 100, 50);
            Assert.AreEqual(Constants.S_OK, status);
            IntPtr realPointer = handle.DangerousGetHandle();
            Assert.AreNotEqual(IntPtr.Zero, realPointer);
            Assert.IsFalse(handle.IsInvalid);
            Assert.IsFalse(handle.IsClosed);
            handle.Dispose();
            Assert.IsTrue(handle.IsClosed);
        }

        [Test]
        public void NtCreatePort_NtListenPort()
        {
            var name = "\\" + this.GetType().Name + MethodBase.GetCurrentMethod().Name;
            NAlpc.AplcPortHandle handle = null;
            var attributes = new OBJECT_ATTRIBUTES(name, 0);
            NativeMethods.NtCreatePort(out handle, ref attributes, 100, 100, 50);
            var msg = new MY_TRANSFERRED_MESSAGE();
            var wait = Task.Factory.StartNew(() =>
                {
                    var status = NativeMethods.NtListenPort(handle, ref msg.Header);
                    Assert.AreNotEqual(0,status);
                });
            var listenBlocksThread = wait.Wait(100);
            Assert.IsFalse(listenBlocksThread);
            handle.Dispose();

        }

        [Test]
        public void NtListenPort()
        {
           var handle = new AplcPortHandle();
            var msg = new MY_TRANSFERRED_MESSAGE();
            var wait = Task.Factory.StartNew(() =>
            {
                var status = NativeMethods.NtListenPort(handle, ref msg.Header);
                Assert.AreNotEqual(0, status);
            });
            var listenBlocksThread = wait.Wait(100);
            Assert.IsTrue(listenBlocksThread);
  
        }

        [StructLayout(LayoutKind.Sequential)]
        struct MY_TRANSFERRED_MESSAGE
        {
            public PORT_MESSAGE Header;

            public uint Command;
            public char[] MessageText; //48
        }
    }
}
