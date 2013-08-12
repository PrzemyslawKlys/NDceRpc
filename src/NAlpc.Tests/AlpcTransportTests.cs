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
    public class AlpcTransportTests
    {
        [Test]
        public void Create()
        {
            var transport = new AlpcTransport();
            transport.Open();
        }
    }
}
