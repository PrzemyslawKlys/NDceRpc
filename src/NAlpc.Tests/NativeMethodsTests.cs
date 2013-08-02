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
            IntPtr handle = IntPtr.Zero;
            var attributes = new OBJECT_ATTRIBUTES(name, 0);
            int status = NativeMethods.NtCreatePort(out handle, ref attributes, 100, 100, 50);
            Assert.AreEqual(Constants.S_OK, status);
            Assert.AreNotEqual(IntPtr.Zero, handle);
            NativeMethods.NtClose(handle);
        }
    }
}
