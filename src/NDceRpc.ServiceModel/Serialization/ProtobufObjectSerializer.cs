using System;
using System.IO;
using System.Runtime.Serialization;
using ProtoBuf.Meta;

namespace NDceRpc.ServiceModel
{
    public class ProtobufObjectSerializer : BinaryObjectSerializer
    {
        private RuntimeTypeModel _proto;
      // private DataContractSerializer _dataContractSerializer;

        public ProtobufObjectSerializer()
        {
            _proto = TypeModel.Create();
            _proto.AutoCompile = true;
            _proto.AutoAddMissingTypes = true;
        }

        public override void WriteObject(Stream stream, object graph)
        {
            if (graph == null) return;
            //_dataContractSerializer = new DataContractSerializer(graph.GetType());
            //_dataContractSerializer.WriteObject(stream,graph);

            //return;
            _proto.Serialize(stream,graph);
        }

        public override object ReadObject(Stream data,Type type)
        {
           // _dataContractSerializer = new DataContractSerializer(type);
            //return _dataContractSerializer.ReadObject(data);

            //TODO: this is hack for async methods with null, should go from here to RPC related stuff
            if (typeof(IAsyncResult) == type || typeof(AsyncCallback) == type)
                return null;
            if (data.Length == 0 && typeof(object) == type )
                return null;
            return _proto.Deserialize(data, null, type);
        }

        private object GetDefaultValue(Type t)
        {
            if (t.IsValueType)
            {
                return Activator.CreateInstance(t);
            }
            else
            {
                return null;
            }
        }

        public override void AddKnownType(Type type)
        {
            _proto.Add(type, true);
        }
    }
}