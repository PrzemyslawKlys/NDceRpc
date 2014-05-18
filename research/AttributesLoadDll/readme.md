
`System.ServiceModel` contains attributes and enumerations which form declarative language which is  usable describing lifecylce, synchronization and other contracts of inter process services.

`System.ServiceModel.dll` is big[1] and it is good to avoid loading it if only attributes and enumerations are used.

`GetCustomAttributesData` forces loading dll and is not usable. There are 2 ways of prevent loading dll:

1. IL parsing via tool
2. Copy and paste attributes into code.

First will lead to dependency on parsing dll, and will not delay issues that WCF attributes pertain only WCF specific parts for later. Examples:

- `ServiceBehaviorAttribute' implements `IServiceBehavior` which is tightly coupled to WCF.
- some attributes and enumerations are related to XML, e.g. `XmlSerializerFormatAttribute`

So going second path is one to choose.

[1]: https://gitorious.org/asdandrizzo/windows/source/7991cc9e2837feb9432014c7ff27456b788bd73a:research-dll-memory