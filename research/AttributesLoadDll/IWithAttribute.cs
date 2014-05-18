
using System;
using System.ServiceModel;

namespace AttributesLoadDll
{
	
	[ServiceContract]
	public interface IWithAttribute
	{
		[OperationContract()]
		void Do();
	}
}
