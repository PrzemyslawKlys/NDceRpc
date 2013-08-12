using System;
using NUnit.Framework;
using NUnit;
using System.Threading.Tasks;
using System.ServiceModel.Channels;
using System.ServiceModel;
using System.Runtime.Serialization;

namespace NDceRpc.Mono
{


	[NUnit.Framework.TestFixture]
	public class InsideWcfChannels
	{
		[Test]
		public void MessageVersionDefault_isRightAccordingVersion(){
			NUnit.Framework.Assert.AreEqual (System.ServiceModel.Channels.MessageVersion.Default.Envelope, EnvelopeVersion.Soap12);
			NUnit.Framework.Assert.AreEqual (System.ServiceModel.Channels.MessageVersion.Default.Addressing, AddressingVersion.WSAddressing10);
		}

	}
}
