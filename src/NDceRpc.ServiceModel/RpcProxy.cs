using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.IO;
using System.Linq;
using System.Reflection;
using System.ServiceModel;
using System.Threading;
using System.Threading.Tasks;
using Microsoft.Practices.Unity.InterceptionExtension;
using NDceRpc.ExplicitBytes;
using ProtoBuf;

namespace NDceRpc.ServiceModel
{
    public class RpcProxy : IDisposable, IInterceptionBehavior
    {
        private object _service;
        private IExplicitBytesClient _client;
        private RpcServerStub _dipatcher;
        private object _remote;
        private Dictionary<int, OperationDispatchBase> _operations = new Dictionary<int, OperationDispatchBase>();
        private string _session;
        private BinaryObjectSerializer _serializer;
        private Type _typeOfService;
        private InstanceContext _context;
        private bool _disposed;
        private string _address;
        private ManualResetEvent _operationPending = new ManualResetEvent(true);
        private Binding _binding;
        private Guid _uuid;

        //TODO: split RpcProxy and RpcCallbackProxy
        public RpcProxy(string address, Type typeOfService, Binding binding, bool callback = false, InstanceContext context = null, Guid customUuid = default(Guid))
        {
            _serializer = binding.Serializer;
            _typeOfService = typeOfService;
            _context = context;
            _binding = binding;
            _address = address;

            MethodInfo[] ops = TypeExtensions.GetAllServiceImplmentations(_typeOfService);
            _operations = DispatchFactory.CreateOperations(ops);
 

            _uuid = EndpointMapper.CreateUuid(_address, typeOfService);
            if (customUuid != Guid.Empty) // callback proxy
            {
                _uuid = customUuid;
            }

            //TODO: got to Mono and reuse their approach for WCF
            var type = ProxyEmit.CreateFake(_typeOfService);
            var service = Activator.CreateInstance(type);
            var serviceContract = _typeOfService.GetCustomAttributes(typeof(ServiceContractAttribute), false).SingleOrDefault() as ServiceContractAttribute;
            //TODO: null check only for duplex callback, really should always be here
            if (serviceContract != null && serviceContract.SessionMode == SessionMode.Required)
            {
                _session = Guid.NewGuid().ToString();
            }
            _remote = Intercept.ThroughProxy(_typeOfService, service, new InterfaceInterceptor(), new[] { this });

            _client = TransportFactory.CreateClient(_binding,_uuid, _address);


        }

        public RpcProxy(Uri address, Type typeOfService, Binding binding, bool callback = false, InstanceContext context = null)
            : this(address.ToString(), typeOfService, binding, callback, context)
        {

        }




        public IEnumerable<Type> GetRequiredInterfaces()
        {
            return Type.EmptyTypes;
        }

        public IMethodReturn Invoke(IMethodInvocation input, GetNextInterceptionBehaviorDelegate getNext)
        {
            //TODO: move to RpcCallbackProxy
            if (_context != null)
                _context.Initialize(_typeOfService, _address, _binding, _session);

            IMethodReturn methodReturn;
            var op = _operations[input.MethodBase.MetadataToken];

            var r = new MessageRequest();
            if (_session != null)
            {
                r.Session = _session;
            }
            r.Operation = op.Identifier;
            var ps = new List<RpcParamData>();
            for (int i = 0; i < op.Params.Count; i++)
            {
                var inp = input.Inputs.GetParameterInfo(i).MetadataToken;
                var stream = new MemoryStream();
                _serializer.WriteObject(stream, input.Inputs[i]);
                ps.Add(new RpcParamData { Identifier = inp, Data = stream.ToArray() });
            }
            r.Data = ps.ToArray();
            var rData = new MemoryStream();
            _serializer.WriteObject(rData, r);
            if (op is AsyncOperationDispatch)
            {
                object asyncState = input.Inputs[op.Params.Count + 1];
                var task = Tasks.Factory.StartNew((x) =>
                {
                    try
                    {
                        _operationPending.Reset();

                        byte[] result = null;


                        result = _client.Execute(rData.ToArray());


                        var response = (MessageResponse)_serializer.ReadObject(new MemoryStream(result), typeof(MessageResponse));
                        if (response.Error != null)
                        {
                            throw new Exception(response.Error.Type + response.Error.Message);
                        }
                    }
                    catch (Exception ex)
                    {
                        bool handled = ErrorHandler.Handle(ex);
                        if (!handled) throw;
                    }
                    finally
                    {
                        _operationPending.Set();
                    }
                }, asyncState);
                
                task.ContinueWith(x =>
                    {
                        AsyncCallback asyncCallback = (AsyncCallback)input.Inputs[op.Params.Count] ;
                        
                        if (asyncCallback != null)
                        {
                            asyncCallback(task);
                        }
                    });
               return new VirtualMethodReturn(input, task, Inputs(input).ToArray());
              
            }
            else if (op.Operation.IsOneWay)
            {
                Debug.Assert(op.MethodInfo.ReturnType == typeof(void));
        
                Task task = Tasks.Factory.StartNew(() =>
                {
                    try
                    {
                        _operationPending.Reset();

                        byte[] result = null;


                        result = _client.Execute(rData.ToArray());


                        var response = (MessageResponse)_serializer.ReadObject(new MemoryStream(result), typeof(MessageResponse));
                        if (response.Error != null)
                        {
                            throw new Exception(response.Error.Type + response.Error.Message);
                        }
                    }
                    catch (Exception ex)
                    {

                        bool handled = ErrorHandler.Handle(ex);
                        if (!handled) throw;
                    }
                    finally
                    {
                        _operationPending.Set();
                    }
                }
                );
                return new VirtualMethodReturn(input, null, Inputs(input).ToArray());
            }

            try
            {
                _operationPending.Reset();

                byte[] result = null;

                result = _client.Execute(rData.ToArray());

                var response = (MessageResponse)_serializer.ReadObject(new MemoryStream(result), typeof(MessageResponse));
                if (response.Error != null)
                {
                    throw new Exception(response.Error.Type + response.Error.Message);
                }
                if (op.MethodInfo.ReturnType != typeof(void))
                {
                    var retVal = Serializer.NonGeneric.Deserialize(op.MethodInfo.ReturnType, new MemoryStream(response.Data));
                    var ret = new VirtualMethodReturn(input, retVal, Inputs(input).ToArray());
                    return ret;
                }
            }
            catch (Exception ex)
            {
                bool handled = ErrorHandler.Handle(ex);
                if (!handled) throw;
            }
            finally
            {
                _operationPending.Set();
            }
            return new VirtualMethodReturn(input, null, Inputs(input).ToArray());
        }

        private static IEnumerable<object> Inputs(IMethodInvocation input)
        {
            foreach (var inp in input.Inputs)
            {
                yield return inp;
            }
        }

        public bool WillExecute
        {
            get { return true; }
        }

        public object Channell
        {
            get { return _remote; }
        }


        public void Close(TimeSpan closeTimeout)
        {
            _operationPending.WaitOne(closeTimeout);
            _client.Dispose();
        }

        public void Dispose()
        {
            Close(_binding.CloseTimeout);
        }
    }



}
