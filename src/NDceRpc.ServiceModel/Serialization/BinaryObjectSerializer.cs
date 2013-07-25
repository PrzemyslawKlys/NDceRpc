using System;
using System.IO;
using System.Runtime.Serialization;

namespace NDceRpc.Serialization
{
    /// <summary>
    /// Similar to <seealso cref="XmlObjectSerializer"/>
    /// </summary>
    public abstract class BinaryObjectSerializer
    {
        public abstract void WriteObject(Stream stream, object graph);
        public abstract object ReadObject(Stream data, Type type);
        public abstract void AddKnownType(Type type);

    }
}
