using System;
using NUnit.Framework;
using NUnit;
using System.Threading.Tasks;
using System.ServiceModel.Channels;
using System.ServiceModel;
using System.Runtime.Serialization;

namespace NDceRpc.Mono
{
	class MainClass
	{
		public static void Main (string[] args)
		{
			var wcf = new InsideWcf ();
			wcf.CreateMessageFault_isFault ();
			Console.ReadKey ();
		}
	}

	[NUnit.Framework.TestFixture]
	public class InsideWcf
	{
		[Test]
		public void MessageVersionDefault_isRightAccordingVersion(){
			NUnit.Framework.Assert.AreEqual (System.ServiceModel.Channels.MessageVersion.Default.Envelope, EnvelopeVersion.Soap12);
			NUnit.Framework.Assert.AreEqual (System.ServiceModel.Channels.MessageVersion.Default.Addressing, AddressingVersion.WSAddressing10);
		}

		[Test]
		public void CreateMessage_givenActionWithStrinParameterAndDefaults_messageContainsActionAndBody(){
			var msg = Message.CreateMessage(MessageVersion.Default,"urn:DoStuff","body");
			var content = msg.ToString ();
			StringAssert.Contains ("DoStuff",content);
			StringAssert.Contains ("body", content);
		}

		private sealed class MyClrData{
			internal MyClrData(int i){}
		}

		[Test]
		[ExpectedException(typeof(InvalidDataContractException))]
		public void CreateMessage_givenNotSerializableObjectAndDefaults_error(){
			var msg = Message.CreateMessage(MessageVersion.Default,"urn:DoStuff",new MyClrData(0));
			var content = msg.ToString ();
		}

		[Test]
		public void CreateMessageFault_isFault(){
			var fault = MessageFault.CreateFault (new FaultCode ("Sender"), new FaultReason ("Dummy"));
			var msg = Message.CreateMessage(MessageVersion.Default,fault,"urn:DoFault");
			Assert.IsTrue (msg.IsFault);
			var type = msg.GetType();
		
			var content = msg.ToString ();
		}
	}
}
