using System;
using System.Collections.Generic;
using System.Linq;
using System.Reflection;
using System.Runtime.InteropServices;
using System.Text;
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

        struct TRANSFERRED_MESSAGE
        {
            PORT_MESSAGE Header;

            uint Command;
            char[] MessageText; //48
        }
    }
}
