using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Reflection;
using System.Text;
using NDceRpc.ExplicitBytes;
using NDceRpc.Interop;


namespace NDceRpc.Sample
{
    class Program
    {
        static void Main(string[] args)
        {

            var isServer = args.Length < 1;
            if (isServer)
            {
                var iid = Guid.NewGuid();
                Console.WriteLine("Server id = "+iid);
                var server = new ExplicitBytesServer(iid);
                server.AddProtocol(RpcProtseq.ncacn_np, "\\pipe\\testnamedpipe"+iid,byte.MaxValue);
                server.OnExecute+= (client,data) =>
                    {
                        Console.WriteLine(Encoding.Unicode.GetString(data));
                        return Encoding.Unicode.GetBytes("Server response");
                    };
                server.StartListening();
                Console.WriteLine("Server started");
                Process.Start(Assembly.GetExecutingAssembly().CodeBase, iid.ToString());
            }
            else
            {
                var iid = new Guid(args[0]);
                Console.WriteLine("Server id = " + iid);
                var client = new ExplicitBytesClient(iid, new EndpointBindingInfo(RpcProtseq.ncacn_np, null, "\\pipe\\testnamedpipe" + iid));
                Console.WriteLine("Client started");
                for (int i = 0; i < 10; i++)
                {
                    var resp = client.Execute(Encoding.Unicode.GetBytes("Client request"));
                    Console.WriteLine(Encoding.Unicode.GetString(resp));    
                }
            }
  
            Console.ReadKey();
        }
    }
}
