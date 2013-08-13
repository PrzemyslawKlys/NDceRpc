using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Reflection;
using System.Text;
using NDceRpc.ExplicitBytes;
using NDceRpc.Microsoft.Interop;


namespace NDceRpc.Sample
{
    class Program
    {
        static void Main(string[] args)
        {
            var iid = Guid.Parse("FF9B1856-934A-459B-92AF-18AEBD745BC1");
            Console.WriteLine("Server id = " + iid);
            var server = new ExplicitBytesServer(iid);
            server.AddProtocol(RpcProtseq.ncacn_np, "\\pipe\\testnamedpipe" + iid, byte.MaxValue);
            server.OnExecute += (client, data) =>
                {
                    Console.WriteLine(Encoding.Unicode.GetString(data));
                    return Encoding.Unicode.GetBytes("Server response");
                };
            server.StartListening();
            Console.WriteLine("Server started");
            

            Console.ReadKey();
        }
    }
}
