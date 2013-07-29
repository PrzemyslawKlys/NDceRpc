using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Linq;
using System.Runtime.InteropServices;
using System.Text;
using NDceRpc.Microsoft.Interop;
using NDceRpc.Native;

namespace NDceRpc.HostOfNativeSample
{


    class Program
    {
        private static GCHandle _handle;
        private static NativeClient _client;
        private const string Implementer = "DceRpcIdl";
        private const string Consumer = "DceRpcIdlClient";

        [DllImport(Implementer,CallingConvention = CallingConvention.StdCall)]
        static extern IntPtr GetDummyServer();

        [DllImport(Consumer, CallingConvention = CallingConvention.StdCall)]
        static extern IntPtr GetDummyClient();

        [DllImport(Implementer, CallingConvention = CallingConvention.StdCall)]
        static extern IntPtr GetExplicitWithCallbacksServer();

        [DllImport(Consumer, CallingConvention = CallingConvention.StdCall)]
        static extern void CallExplicitWithCallbacksServer(IntPtr bindingHandle);

        [DllImport(Consumer, CallingConvention = CallingConvention.StdCall)]
        static extern void CallDummyServer(IntPtr bindingHandle);

        static void Main(string[] args)
        {
            TestDummy();
            TestCallback();
            Console.ReadKey();
        }

        private static void TestCallback()
        {
            var server = GetExplicitWithCallbacksServer();

            var serverInterface = (RPC_SERVER_INTERFACE)Marshal.PtrToStructure(server, typeof(RPC_SERVER_INTERFACE));
            EndpointBindingInfo serverInfo =
                new EndpointBindingInfo(RpcProtseq.ncalrpc, null,
                                        Implementer + serverInterface.InterfaceId.SyntaxGUID.ToString("N"));

            Host.StartServer(serverInfo, server);

 
       
            RPC_STATUS status;
             

            _client = new NativeClient(serverInfo);
            CallExplicitWithCallbacksServer(_client.Binding);
        }

        private static void TestDummy()
        {
            var dummyServer = GetDummyServer();

            var serverInterface = (RPC_SERVER_INTERFACE) Marshal.PtrToStructure(dummyServer, typeof (RPC_SERVER_INTERFACE));
            EndpointBindingInfo serverInfo =
                new EndpointBindingInfo(RpcProtseq.ncalrpc, null,
                                        Implementer + serverInterface.InterfaceId.SyntaxGUID.ToString("N"));

            Host.StartServer(serverInfo, dummyServer);

            var dummyClient = GetDummyClient();
            var clientInterface = (RPC_CLIENT_INTERFACE) Marshal.PtrToStructure(dummyClient, typeof (RPC_CLIENT_INTERFACE));

            RPC_STATUS status;

            EndpointBindingInfo clientInfo =
                new EndpointBindingInfo(RpcProtseq.ncalrpc, null,
                                        Implementer + clientInterface.InterfaceId.SyntaxGUID.ToString("N"));

            _client = new NativeClient(clientInfo);
            CallDummyServer(_client.Binding);
        }
    }






}
