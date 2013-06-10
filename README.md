NDceRpc
=======

Currently there is no good managed RPC for local IPC on Windows. I did research. Dce/Rpc is cool, but for native code. WCF is big and slow. Should do something.

### Design

* Like WCF with flexible interface and hooks with no public process wide methods.
* Like Dce/Rpc compatible with native code, custom and existing IDL
* Sevaral times faster communication then WCF
* Several times faster to start then WCF, codegeneraion during compilations 
* In the end should work on Mono/Linux
* Is gentle request to MS to create such tech which suits 

`NOTE`: Please send me you note if you have reasons to consider such endaviour no fruitful

#### Low level
Mono and protobuf-net is used for replacing WCF in C# over RPC. Moving WCF toward C++ interop because RPC and protobuf are ready for C++. Mono like layer proxies service calls coding/decoding of WCF interfaces into RPC calls, data is serialized via protibuf.
MS-PRC interop PIvokes calls are used to route data accross processes. MS-RPC can be replaced by other transport.

### How tos

#### How to migrate WCF `ServiceOperation` with `DataContract`s

* Mark all data objects with DataContract/DataMember(Order=X). Also protobuf spec does not distinguish empty collection and null collection, which can be problem sometimes.
* Mark all OperationContract with DispId(Y). CLR has some method handles encoding, DispId provides direct user defined encoding so that native part can interpret RPC calls.

Next test goes via RPC + Protobuf, very alpha version for now:

```csharp
            var address = @"net.pipe://127.0.0.1/1/test.test/test" + MethodBase.GetCurrentMethod().Name;
            var binding = new NetNamedPipeBinding();

            var data = new CallbackData { Data = "1" };
            var srv = new CallbackService(data);
            var callback = new CallbackServiceCallback();

            using (var host = new ServiceHost(srv, address))
            {
                host.AddServiceEndpoint(typeof(ICallbackService), binding, address);
                host.Open();


                using (var factory = new DuplexChannelFactory<ICallbackService>(new InstanceContext(callback), binding))
                {
                    var client = factory.CreateChannel(new EndpointAddress(address));
                    client.Call();
                }
          
                callback.Wait.WaitOne();

                Assert.AreEqual(data.Data, callback.Called.Data);
            }
```

#### How to call C++ from C# and vice versa
For now need to wrap C# or C++ IPC services clients into Reg Free COM.

C# uses next interfaces compatible with C++:
```csharp
internal delegate uint ExplicitBytesExecute(
        IntPtr clientHandle, uint szInput, IntPtr input, out uint szOutput, out IntPtr output); 
```

```idl
[
   uuid(00000002-EAF3-4A7A-A0F2-BCE4C30DA77E),//GUID CAN BE REPLACED DURING RUNTIME
   version(1.0)
]
interface ExplicitBytes
{
   void ExplicitBytesExecute(
	   [in] handle_t clientHandle,
	   [in] long szInput, [in,size_is(szInput)] const byte input[],
       [out] long * szOutput,[out, size_is( , *szOutput)] byte ** output
   );
}
```

```proto
message RpcErrorData {
  required string Type = 1;
  optional string Message = 2;
}

message MessageResponse
{
   required bytes Data = 1;
   optional RpcErrorData Error = 2;
}


message  RpcParamData
{ 
   required int32 Identifier = 1;
   required bytes Data = 2;
}

message MessageRequest
{
  required int32 Operation = 1;
  repeated RpcParamData Data = 2;
  optional string Session = 3;
}
```
C++ can interpret call done by C# according outlined interfaces.

#### How to work with IDL generated C RPC

C# code can host pure C++ RPC services:

* Use explicit binding handles (read MSDN docs).
* Define memory allocation and dealocation RPC routines in C as done for pure native host (read MSDN docs).
* Return interfaces specification structures to managed code(NativeServer and NativeClient) which do the hosting.


### TODO:
* Use NRefactory to generate code from WCF interfaces code during compilations to speed up start up
* Generate C++ from CS based IDL - WCF contacts.
* Make convetional interface generation (no WCF attributes used).
* Do not use System.ServiceModel.dll, copy and paste Mono WCF attributes into code (Dismiss not related parts - e.g. SOAP
* Try IKVM.Emit to generate in runtime (better API and potentially faster) 
* Use Mono WCF tests
* Use Mono WCF code more
* Add WCF tests, pull back to mono
* Add callback and async IDL generated structs in C#
* Add IDL parser (use one of open soruce DceRpc implementations yacc lexx defitions, F# lexx and yacc)
* Investigate [user_marshal] to integrate protobuf into IDL.
* Make behave like WCF with different encodings, serilizers(Thrift) and marshalers (NRD, BinaryDataContract)
* Support SyncrhonizationContext of callback, "ref" params, Task/event based asycn
* Add some another Binary serializer compatible with C++ (e.g. Thrift).
* Improve start perfomance
* Improve runtime prefromace

### Related

* http://code.google.com/p/csharptest-net/
* http://code.google.com/p/protobuf-csharp-rpc/
* https://github.com/mono/mono/tree/master/mcs/class/System.ServiceModel
* https://github.com/asd-and-Rizzo/ipc-win