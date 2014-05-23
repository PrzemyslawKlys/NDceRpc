
`System.ServiceModel` contains attributes and enumerations which form declarative language which is  usable describing lifecylce, synchronization and other contracts of inter process services.

`System.ServiceModel.dll` is big[1] and it is good to avoid loading it if only attributes and enumerations are used.

`GetCustomAttributesData` forces loading dll and is not usable. There are 2 ways of prevent loading dll:

1. IL parsing via tool:
- leads to dependency on parsing dll
- does not solves problem of exceptions type loading
- allows multi attribute markers
- WCF attributes has only WCF specific parts
- e.g. `ServiceBehaviorAttribute' implements `IServiceBehavior` which is tightly coupled to WCF.
- e.g. some attributes and enumerations are related to XML, e.g. `XmlSerializerFormatAttribute`

2. Copy and paste attributes into code.
- need custom WCF `ChannelFactory` and `ServiceHost` if want multiple attributes on interfaces
- can copy and use exceptions and interfaces without WCF dependency


[1]: https://gitorious.org/asdandrizzo/windows/source/7991cc9e2837feb9432014c7ff27456b788bd73a:research-dll-memory