NDceRpc
=======

Currently there is no good managed RPC for local IPC on Windows. I did research. Dce/Rpc is cool, but for native code. WCF is big and slow. Should do something.

### Design

* Like WCF with flexible interface and hooks with no public process wide methods.
* Like Dce/Rpc compatible with native code and fast.
* In the end should work on Mono/Linux.

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

#### How to work with IDL generated C RPC

* Use explicit binding handles (read MSDN docs).
* Define memory allocation and dealocation RPC routines in C as done for pure native host (read MSDN docs).
* Return interfaces specification structures to managed code(NativeServer and NativeClient) which do the hosting.


### TODO:

* Use Mono WCF tests
* Use Mono WCF code
* Add WCF tests, pull back to mono
* Add callback and async IDL generated structs in C#
* Add IDL parser (use one of DceRpc implementatios yacc lexx files)
* Make behave like WCF with different encodings, serilizers and marshalers (NRD, BinaryDataContract)
* Support SyncrhonizationContext of callback, "ref" params, Task/event based asycn
* Add some another Binary serializer compatible with C++ (e.g. BSON).
* Improve start perfomance
* Improve runtime prefromace

### Related

* http://code.google.com/p/csharptest-net/
* http://code.google.com/p/protobuf-csharp-rpc/
* https://github.com/mono/mono/tree/master/mcs/class/System.ServiceModel
* https://github.com/asd-and-Rizzo/ipc-win