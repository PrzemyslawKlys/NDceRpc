
//  Microsoft (R) .NET Framework IL Disassembler.  Version 3.5.30729.4916
//  Copyright (c) Microsoft Corporation.  All rights reserved.



// Metadata version: v4.0.30319
.assembly extern mscorlib
{
  .publickeytoken = (B7 7A 5C 56 19 34 E0 89 )                         // .z\V.4..
  .ver 4:0:0:0
}
.assembly extern NDceRpc.ServiceModel.Protobuf
{
  .publickeytoken = (7E F7 6A A0 AD 6F 5C EF )                         // ~.j..o\.
  .ver 0:0:8:0
}
.assembly extern 'protobuf-net'
{
  .publickeytoken = (25 7B 51 D8 7D 2E 4D 67 )                         // %{Q.}.Mg
  .ver 2:0:0:640
}
.assembly NDceRpc.ServiceModel.Protobuf.Serializers
{
  .custom instance void [mscorlib]System.Runtime.Versioning.TargetFrameworkAttribute::.ctor(string) = ( 01 00 1A 2E 4E 45 54 46 72 61 6D 65 77 6F 72 6B   // ....NETFramework
                                                                                                        2C 56 65 72 73 69 6F 6E 3D 76 34 2E 30 01 00 54   // ,Version=v4.0..T
                                                                                                        0E 14 46 72 61 6D 65 77 6F 72 6B 44 69 73 70 6C   // ..FrameworkDispl
                                                                                                        61 79 4E 61 6D 65 10 2E 4E 45 54 20 46 72 61 6D   // ayName..NET Fram
                                                                                                        65 77 6F 72 6B 20 34 )                            // ework 4
  .publickey = (00 24 00 00 04 80 00 00 94 00 00 00 06 02 00 00   // .$..............
                00 24 00 00 52 53 41 31 00 04 00 00 01 00 01 00   // .$..RSA1........
                63 89 9E 61 E6 D9 EA E3 72 DB 49 33 0B B4 5C FD   // c..a....r.I3..\.
                61 04 ED 5B CB D4 51 AC 22 93 E2 EB 15 E8 4D CB   // a..[..Q.".....M.
                9F C5 3C 1B CB 02 E0 16 59 5C CF 91 64 8F AC D5   // ..<.....Y\..d...
                E9 7A FE 71 23 7E 88 1E 23 D2 0C 30 B6 C6 0A F0   // .z.q#~..#..0....
                81 CD 66 7E 05 F6 7C 41 3B CC 48 38 35 EB 9E 8D   // ..f~..|A;.H85...
                CE 84 99 6A 40 04 A8 32 84 21 3A 7B 75 98 A2 0E   // ...j@..2.!:{u...
                06 CF FE 94 83 80 BC 55 29 14 8B C3 C6 C0 A9 F4   // .......U).......
                E6 03 31 E3 BE FB CE 60 CE A5 6A DE B1 2F E0 B1 ) // ..1....`..j../..
  .hash algorithm 0x00008004
  .ver 0:0:0:0
}
.module NDceRpc.ServiceModel.Protobuf.Serializers.dll
// MVID: {EB0F4CF6-382B-4E0E-B9B3-3FCAAD32F3AA}
.imagebase 0x00400000
.file alignment 0x00000200
.stackreserve 0x00100000
.subsystem 0x0003       // WINDOWS_CUI
.corflags 0x00000009    //  ILONLY
// Image base: 0x00310000


// =============== CLASS MEMBERS DECLARATION ===================

.class public auto ansi sealed beforefieldinit NDceRpc.ServiceModel.Channels.Messages
       extends ['protobuf-net']ProtoBuf.Meta.TypeModel
{
  .field private static initonly class [mscorlib]System.Type[] knownTypes
  .method private static void  Write(class [NDceRpc.ServiceModel.Protobuf]NDceRpc.ServiceModel.Channels.MessageRequest A_0,
                                     class ['protobuf-net']ProtoBuf.ProtoWriter A_1) cil managed
  {
    // Code size       215 (0xd7)
    .maxstack  4
    .locals init (class [mscorlib]System.Collections.Generic.List`1<class [NDceRpc.ServiceModel.Protobuf]NDceRpc.ServiceModel.Channels.RpcParamData> V_0,
             valuetype [mscorlib]System.Collections.Generic.List`1/Enumerator<class [NDceRpc.ServiceModel.Protobuf]NDceRpc.ServiceModel.Channels.RpcParamData> V_1,
             valuetype ['protobuf-net']ProtoBuf.SubItemToken V_2)
    IL_0000:  ldarg.0
    IL_0001:  callvirt   instance class [mscorlib]System.Type [mscorlib]System.Object::GetType()
    IL_0006:  ldtoken    [NDceRpc.ServiceModel.Protobuf]NDceRpc.ServiceModel.Channels.MessageRequest
    IL_000b:  call       class [mscorlib]System.Type [mscorlib]System.Type::GetTypeFromHandle(valuetype [mscorlib]System.RuntimeTypeHandle)
    IL_0010:  beq.s      IL_0027

    IL_0012:  ldtoken    [NDceRpc.ServiceModel.Protobuf]NDceRpc.ServiceModel.Channels.MessageRequest
    IL_0017:  call       class [mscorlib]System.Type [mscorlib]System.Type::GetTypeFromHandle(valuetype [mscorlib]System.RuntimeTypeHandle)
    IL_001c:  ldarg.0
    IL_001d:  callvirt   instance class [mscorlib]System.Type [mscorlib]System.Object::GetType()
    IL_0022:  call       void ['protobuf-net']ProtoBuf.Meta.TypeModel::ThrowUnexpectedSubtype(class [mscorlib]System.Type,
                                                                                              class [mscorlib]System.Type)
    IL_0027:  ldarg.0
    IL_0028:  callvirt   instance int32 [NDceRpc.ServiceModel.Protobuf]NDceRpc.ServiceModel.Channels.MessageRequest::get_Operation()
    IL_002d:  ldc.i4.1
    IL_002e:  ldc.i4.0
    IL_002f:  ldarg.1
    IL_0030:  call       void ['protobuf-net']ProtoBuf.ProtoWriter::WriteFieldHeader(int32,
                                                                                     valuetype ['protobuf-net']ProtoBuf.WireType,
                                                                                     class ['protobuf-net']ProtoBuf.ProtoWriter)
    IL_0035:  ldarg.1
    IL_0036:  call       void ['protobuf-net']ProtoBuf.ProtoWriter::WriteInt32(int32,
                                                                               class ['protobuf-net']ProtoBuf.ProtoWriter)
    IL_003b:  ldarg.0
    IL_003c:  callvirt   instance class [mscorlib]System.Collections.Generic.List`1<class [NDceRpc.ServiceModel.Protobuf]NDceRpc.ServiceModel.Channels.RpcParamData> [NDceRpc.ServiceModel.Protobuf]NDceRpc.ServiceModel.Channels.MessageRequest::get_Data()
    IL_0041:  dup
    IL_0042:  brtrue.s   IL_004a

    IL_0044:  pop
    IL_0045:  br         IL_009f

    IL_004a:  stloc.0
    IL_004b:  ldloc.0
    IL_004c:  callvirt   instance valuetype [mscorlib]System.Collections.Generic.List`1/Enumerator<!0> class [mscorlib]System.Collections.Generic.List`1<class [NDceRpc.ServiceModel.Protobuf]NDceRpc.ServiceModel.Channels.RpcParamData>::GetEnumerator()
    IL_0051:  stloc.1
    .try
    {
      IL_0052:  br         IL_007b

      IL_0057:  ldloca.s   V_1
      IL_0059:  call       instance !0 valuetype [mscorlib]System.Collections.Generic.List`1/Enumerator<class [NDceRpc.ServiceModel.Protobuf]NDceRpc.ServiceModel.Channels.RpcParamData>::get_Current()
      IL_005e:  ldc.i4.2
      IL_005f:  ldc.i4.2
      IL_0060:  ldarg.1
      IL_0061:  call       void ['protobuf-net']ProtoBuf.ProtoWriter::WriteFieldHeader(int32,
                                                                                       valuetype ['protobuf-net']ProtoBuf.WireType,
                                                                                       class ['protobuf-net']ProtoBuf.ProtoWriter)
      IL_0066:  dup
      IL_0067:  ldarg.1
      IL_0068:  call       valuetype ['protobuf-net']ProtoBuf.SubItemToken ['protobuf-net']ProtoBuf.ProtoWriter::StartSubItem(object,
                                                                                                                              class ['protobuf-net']ProtoBuf.ProtoWriter)
      IL_006d:  stloc.2
      IL_006e:  ldarg.1
      IL_006f:  call       void NDceRpc.ServiceModel.Channels.Messages::Write(class [NDceRpc.ServiceModel.Protobuf]NDceRpc.ServiceModel.Channels.RpcParamData,
                                                                              class ['protobuf-net']ProtoBuf.ProtoWriter)
      IL_0074:  ldloc.2
      IL_0075:  ldarg.1
      IL_0076:  call       void ['protobuf-net']ProtoBuf.ProtoWriter::EndSubItem(valuetype ['protobuf-net']ProtoBuf.SubItemToken,
                                                                                 class ['protobuf-net']ProtoBuf.ProtoWriter)
      IL_007b:  ldloca.s   V_1
      IL_007d:  call       instance bool valuetype [mscorlib]System.Collections.Generic.List`1/Enumerator<class [NDceRpc.ServiceModel.Protobuf]NDceRpc.ServiceModel.Channels.RpcParamData>::MoveNext()
      IL_0082:  brtrue     IL_0057

      IL_0087:  leave      IL_009f

      IL_008c:  leave      IL_009f

    }  // end .try
    finally
    {
      IL_0091:  ldloca.s   V_1
      IL_0093:  constrained. valuetype [mscorlib]System.Collections.Generic.List`1/Enumerator<class [NDceRpc.ServiceModel.Protobuf]NDceRpc.ServiceModel.Channels.RpcParamData>
      IL_0099:  callvirt   instance void [mscorlib]System.IDisposable::Dispose()
      IL_009e:  endfinally
    }  // end handler
    IL_009f:  ldarg.0
    IL_00a0:  callvirt   instance string [NDceRpc.ServiceModel.Protobuf]NDceRpc.ServiceModel.Channels.MessageRequest::get_Session()
    IL_00a5:  dup
    IL_00a6:  brtrue.s   IL_00ae

    IL_00a8:  pop
    IL_00a9:  br         IL_00cf

    IL_00ae:  dup
    IL_00af:  ldstr      ""
    IL_00b4:  call       bool [mscorlib]System.String::op_Equality(string,
                                                                   string)
    IL_00b9:  brtrue     IL_00ce

    IL_00be:  ldc.i4.3
    IL_00bf:  ldc.i4.2
    IL_00c0:  ldarg.1
    IL_00c1:  call       void ['protobuf-net']ProtoBuf.ProtoWriter::WriteFieldHeader(int32,
                                                                                     valuetype ['protobuf-net']ProtoBuf.WireType,
                                                                                     class ['protobuf-net']ProtoBuf.ProtoWriter)
    IL_00c6:  ldarg.1
    IL_00c7:  call       void ['protobuf-net']ProtoBuf.ProtoWriter::WriteString(string,
                                                                                class ['protobuf-net']ProtoBuf.ProtoWriter)
    IL_00cc:  br.s       IL_00cf

    IL_00ce:  pop
    IL_00cf:  ldarg.0
    IL_00d0:  ldarg.1
    IL_00d1:  call       void ['protobuf-net']ProtoBuf.ProtoWriter::AppendExtensionData(class ['protobuf-net']ProtoBuf.IExtensible,
                                                                                        class ['protobuf-net']ProtoBuf.ProtoWriter)
    IL_00d6:  ret
  } // end of method Messages::Write

  .method private static class [NDceRpc.ServiceModel.Protobuf]NDceRpc.ServiceModel.Channels.MessageRequest 
          Read(class [NDceRpc.ServiceModel.Protobuf]NDceRpc.ServiceModel.Channels.MessageRequest A_0,
               class ['protobuf-net']ProtoBuf.ProtoReader A_1) cil managed
  {
    // Code size       263 (0x107)
    .maxstack  4
    .locals init (int32 V_0,
             class [mscorlib]System.Collections.Generic.List`1<class [NDceRpc.ServiceModel.Protobuf]NDceRpc.ServiceModel.Channels.RpcParamData> V_1,
             int32 V_2,
             valuetype ['protobuf-net']ProtoBuf.SubItemToken V_3)
    IL_0000:  br         IL_00e3

    IL_0005:  ldloc.0
    IL_0006:  ldc.i4.1
    IL_0007:  beq.s      IL_000e

    IL_0009:  br         IL_0033

    IL_000e:  ldarg.0
    IL_000f:  brtrue     IL_0022

    IL_0014:  newobj     instance void [NDceRpc.ServiceModel.Protobuf]NDceRpc.ServiceModel.Channels.MessageRequest::.ctor()
    IL_0019:  dup
    IL_001a:  ldarg.1
    IL_001b:  call       void ['protobuf-net']ProtoBuf.ProtoReader::NoteObject(object,
                                                                               class ['protobuf-net']ProtoBuf.ProtoReader)
    IL_0020:  starg.s    A_0
    IL_0022:  ldarg.0
    IL_0023:  ldarg.1
    IL_0024:  callvirt   instance int32 ['protobuf-net']ProtoBuf.ProtoReader::ReadInt32()
    IL_0029:  callvirt   instance void [NDceRpc.ServiceModel.Protobuf]NDceRpc.ServiceModel.Channels.MessageRequest::set_Operation(int32)
    IL_002e:  br         IL_00e3

    IL_0033:  ldloc.0
    IL_0034:  ldc.i4.2
    IL_0035:  beq.s      IL_003c

    IL_0037:  br         IL_0093

    IL_003c:  ldarg.0
    IL_003d:  brtrue     IL_0050

    IL_0042:  newobj     instance void [NDceRpc.ServiceModel.Protobuf]NDceRpc.ServiceModel.Channels.MessageRequest::.ctor()
    IL_0047:  dup
    IL_0048:  ldarg.1
    IL_0049:  call       void ['protobuf-net']ProtoBuf.ProtoReader::NoteObject(object,
                                                                               class ['protobuf-net']ProtoBuf.ProtoReader)
    IL_004e:  starg.s    A_0
    IL_0050:  ldarg.0
    IL_0051:  callvirt   instance class [mscorlib]System.Collections.Generic.List`1<class [NDceRpc.ServiceModel.Protobuf]NDceRpc.ServiceModel.Channels.RpcParamData> [NDceRpc.ServiceModel.Protobuf]NDceRpc.ServiceModel.Channels.MessageRequest::get_Data()
    IL_0056:  stloc.1
    IL_0057:  ldloc.1
    IL_0058:  brtrue.s   IL_0060

    IL_005a:  newobj     instance void class [mscorlib]System.Collections.Generic.List`1<class [NDceRpc.ServiceModel.Protobuf]NDceRpc.ServiceModel.Channels.RpcParamData>::.ctor()
    IL_005f:  stloc.1
    IL_0060:  ldarg.1
    IL_0061:  callvirt   instance int32 ['protobuf-net']ProtoBuf.ProtoReader::get_FieldNumber()
    IL_0066:  stloc.2
    IL_0067:  ldloc.1
    IL_0068:  ldnull
    IL_0069:  ldarg.1
    IL_006a:  call       valuetype ['protobuf-net']ProtoBuf.SubItemToken ['protobuf-net']ProtoBuf.ProtoReader::StartSubItem(class ['protobuf-net']ProtoBuf.ProtoReader)
    IL_006f:  stloc.3
    IL_0070:  ldarg.1
    IL_0071:  call       class [NDceRpc.ServiceModel.Protobuf]NDceRpc.ServiceModel.Channels.RpcParamData NDceRpc.ServiceModel.Channels.Messages::Read(class [NDceRpc.ServiceModel.Protobuf]NDceRpc.ServiceModel.Channels.RpcParamData,
                                                                                                                                                      class ['protobuf-net']ProtoBuf.ProtoReader)
    IL_0076:  ldloc.3
    IL_0077:  ldarg.1
    IL_0078:  call       void ['protobuf-net']ProtoBuf.ProtoReader::EndSubItem(valuetype ['protobuf-net']ProtoBuf.SubItemToken,
                                                                               class ['protobuf-net']ProtoBuf.ProtoReader)
    IL_007d:  callvirt   instance void class [mscorlib]System.Collections.Generic.List`1<class [NDceRpc.ServiceModel.Protobuf]NDceRpc.ServiceModel.Channels.RpcParamData>::Add(!0)
    IL_0082:  ldarg.1
    IL_0083:  ldloc.2
    IL_0084:  callvirt   instance bool ['protobuf-net']ProtoBuf.ProtoReader::TryReadFieldHeader(int32)
    IL_0089:  brtrue     IL_0067

    IL_008e:  br         IL_00e3

    IL_0093:  ldloc.0
    IL_0094:  ldc.i4.3
    IL_0095:  beq.s      IL_009c

    IL_0097:  br         IL_00c8

    IL_009c:  ldarg.0
    IL_009d:  brtrue     IL_00b0

    IL_00a2:  newobj     instance void [NDceRpc.ServiceModel.Protobuf]NDceRpc.ServiceModel.Channels.MessageRequest::.ctor()
    IL_00a7:  dup
    IL_00a8:  ldarg.1
    IL_00a9:  call       void ['protobuf-net']ProtoBuf.ProtoReader::NoteObject(object,
                                                                               class ['protobuf-net']ProtoBuf.ProtoReader)
    IL_00ae:  starg.s    A_0
    IL_00b0:  ldarg.0
    IL_00b1:  ldarg.1
    IL_00b2:  callvirt   instance string ['protobuf-net']ProtoBuf.ProtoReader::ReadString()
    IL_00b7:  dup
    IL_00b8:  brfalse.s  IL_00c1

    IL_00ba:  callvirt   instance void [NDceRpc.ServiceModel.Protobuf]NDceRpc.ServiceModel.Channels.MessageRequest::set_Session(string)
    IL_00bf:  br.s       IL_00c3

    IL_00c1:  pop
    IL_00c2:  pop
    IL_00c3:  br         IL_00e3

    IL_00c8:  ldarg.0
    IL_00c9:  brtrue     IL_00dc

    IL_00ce:  newobj     instance void [NDceRpc.ServiceModel.Protobuf]NDceRpc.ServiceModel.Channels.MessageRequest::.ctor()
    IL_00d3:  dup
    IL_00d4:  ldarg.1
    IL_00d5:  call       void ['protobuf-net']ProtoBuf.ProtoReader::NoteObject(object,
                                                                               class ['protobuf-net']ProtoBuf.ProtoReader)
    IL_00da:  starg.s    A_0
    IL_00dc:  ldarg.1
    IL_00dd:  ldarg.0
    IL_00de:  callvirt   instance void ['protobuf-net']ProtoBuf.ProtoReader::AppendExtensionData(class ['protobuf-net']ProtoBuf.IExtensible)
    IL_00e3:  ldarg.1
    IL_00e4:  callvirt   instance int32 ['protobuf-net']ProtoBuf.ProtoReader::ReadFieldHeader()
    IL_00e9:  dup
    IL_00ea:  stloc.0
    IL_00eb:  ldc.i4.0
    IL_00ec:  bgt        IL_0005

    IL_00f1:  ldarg.0
    IL_00f2:  brtrue     IL_0105

    IL_00f7:  newobj     instance void [NDceRpc.ServiceModel.Protobuf]NDceRpc.ServiceModel.Channels.MessageRequest::.ctor()
    IL_00fc:  dup
    IL_00fd:  ldarg.1
    IL_00fe:  call       void ['protobuf-net']ProtoBuf.ProtoReader::NoteObject(object,
                                                                               class ['protobuf-net']ProtoBuf.ProtoReader)
    IL_0103:  starg.s    A_0
    IL_0105:  ldarg.0
    IL_0106:  ret
  } // end of method Messages::Read

  .method private static void  Write(class [NDceRpc.ServiceModel.Protobuf]NDceRpc.ServiceModel.Channels.MessageResponse A_0,
                                     class ['protobuf-net']ProtoBuf.ProtoWriter A_1) cil managed
  {
    // Code size       120 (0x78)
    .maxstack  4
    .locals init (valuetype ['protobuf-net']ProtoBuf.SubItemToken V_0)
    IL_0000:  ldarg.0
    IL_0001:  callvirt   instance class [mscorlib]System.Type [mscorlib]System.Object::GetType()
    IL_0006:  ldtoken    [NDceRpc.ServiceModel.Protobuf]NDceRpc.ServiceModel.Channels.MessageResponse
    IL_000b:  call       class [mscorlib]System.Type [mscorlib]System.Type::GetTypeFromHandle(valuetype [mscorlib]System.RuntimeTypeHandle)
    IL_0010:  beq.s      IL_0027

    IL_0012:  ldtoken    [NDceRpc.ServiceModel.Protobuf]NDceRpc.ServiceModel.Channels.MessageResponse
    IL_0017:  call       class [mscorlib]System.Type [mscorlib]System.Type::GetTypeFromHandle(valuetype [mscorlib]System.RuntimeTypeHandle)
    IL_001c:  ldarg.0
    IL_001d:  callvirt   instance class [mscorlib]System.Type [mscorlib]System.Object::GetType()
    IL_0022:  call       void ['protobuf-net']ProtoBuf.Meta.TypeModel::ThrowUnexpectedSubtype(class [mscorlib]System.Type,
                                                                                              class [mscorlib]System.Type)
    IL_0027:  ldarg.0
    IL_0028:  callvirt   instance uint8[] [NDceRpc.ServiceModel.Protobuf]NDceRpc.ServiceModel.Channels.MessageResponse::get_Data()
    IL_002d:  dup
    IL_002e:  brtrue.s   IL_0036

    IL_0030:  pop
    IL_0031:  br         IL_0044

    IL_0036:  ldc.i4.1
    IL_0037:  ldc.i4.2
    IL_0038:  ldarg.1
    IL_0039:  call       void ['protobuf-net']ProtoBuf.ProtoWriter::WriteFieldHeader(int32,
                                                                                     valuetype ['protobuf-net']ProtoBuf.WireType,
                                                                                     class ['protobuf-net']ProtoBuf.ProtoWriter)
    IL_003e:  ldarg.1
    IL_003f:  call       void ['protobuf-net']ProtoBuf.ProtoWriter::WriteBytes(uint8[],
                                                                               class ['protobuf-net']ProtoBuf.ProtoWriter)
    IL_0044:  ldarg.0
    IL_0045:  callvirt   instance class [NDceRpc.ServiceModel.Protobuf]NDceRpc.ServiceModel.Channels.RpcErrorData [NDceRpc.ServiceModel.Protobuf]NDceRpc.ServiceModel.Channels.MessageResponse::get_Error()
    IL_004a:  dup
    IL_004b:  brtrue.s   IL_0053

    IL_004d:  pop
    IL_004e:  br         IL_0070

    IL_0053:  ldc.i4.2
    IL_0054:  ldc.i4.2
    IL_0055:  ldarg.1
    IL_0056:  call       void ['protobuf-net']ProtoBuf.ProtoWriter::WriteFieldHeader(int32,
                                                                                     valuetype ['protobuf-net']ProtoBuf.WireType,
                                                                                     class ['protobuf-net']ProtoBuf.ProtoWriter)
    IL_005b:  dup
    IL_005c:  ldarg.1
    IL_005d:  call       valuetype ['protobuf-net']ProtoBuf.SubItemToken ['protobuf-net']ProtoBuf.ProtoWriter::StartSubItem(object,
                                                                                                                            class ['protobuf-net']ProtoBuf.ProtoWriter)
    IL_0062:  stloc.0
    IL_0063:  ldarg.1
    IL_0064:  call       void NDceRpc.ServiceModel.Channels.Messages::Write(class [NDceRpc.ServiceModel.Protobuf]NDceRpc.ServiceModel.Channels.RpcErrorData,
                                                                            class ['protobuf-net']ProtoBuf.ProtoWriter)
    IL_0069:  ldloc.0
    IL_006a:  ldarg.1
    IL_006b:  call       void ['protobuf-net']ProtoBuf.ProtoWriter::EndSubItem(valuetype ['protobuf-net']ProtoBuf.SubItemToken,
                                                                               class ['protobuf-net']ProtoBuf.ProtoWriter)
    IL_0070:  ldarg.0
    IL_0071:  ldarg.1
    IL_0072:  call       void ['protobuf-net']ProtoBuf.ProtoWriter::AppendExtensionData(class ['protobuf-net']ProtoBuf.IExtensible,
                                                                                        class ['protobuf-net']ProtoBuf.ProtoWriter)
    IL_0077:  ret
  } // end of method Messages::Write

  .method private static class [NDceRpc.ServiceModel.Protobuf]NDceRpc.ServiceModel.Channels.MessageResponse 
          Read(class [NDceRpc.ServiceModel.Protobuf]NDceRpc.ServiceModel.Channels.MessageResponse A_0,
               class ['protobuf-net']ProtoBuf.ProtoReader A_1) cil managed
  {
    // Code size       200 (0xc8)
    .maxstack  4
    .locals init (int32 V_0,
             valuetype ['protobuf-net']ProtoBuf.SubItemToken V_1)
    IL_0000:  br         IL_00a4

    IL_0005:  ldloc.0
    IL_0006:  ldc.i4.1
    IL_0007:  beq.s      IL_000e

    IL_0009:  br         IL_0040

    IL_000e:  ldarg.0
    IL_000f:  brtrue     IL_0022

    IL_0014:  newobj     instance void [NDceRpc.ServiceModel.Protobuf]NDceRpc.ServiceModel.Channels.MessageResponse::.ctor()
    IL_0019:  dup
    IL_001a:  ldarg.1
    IL_001b:  call       void ['protobuf-net']ProtoBuf.ProtoReader::NoteObject(object,
                                                                               class ['protobuf-net']ProtoBuf.ProtoReader)
    IL_0020:  starg.s    A_0
    IL_0022:  ldarg.0
    IL_0023:  dup
    IL_0024:  callvirt   instance uint8[] [NDceRpc.ServiceModel.Protobuf]NDceRpc.ServiceModel.Channels.MessageResponse::get_Data()
    IL_0029:  ldarg.1
    IL_002a:  call       uint8[] ['protobuf-net']ProtoBuf.ProtoReader::AppendBytes(uint8[],
                                                                                   class ['protobuf-net']ProtoBuf.ProtoReader)
    IL_002f:  dup
    IL_0030:  brfalse.s  IL_0039

    IL_0032:  callvirt   instance void [NDceRpc.ServiceModel.Protobuf]NDceRpc.ServiceModel.Channels.MessageResponse::set_Data(uint8[])
    IL_0037:  br.s       IL_003b

    IL_0039:  pop
    IL_003a:  pop
    IL_003b:  br         IL_00a4

    IL_0040:  ldloc.0
    IL_0041:  ldc.i4.2
    IL_0042:  beq.s      IL_0049

    IL_0044:  br         IL_0089

    IL_0049:  ldarg.0
    IL_004a:  brtrue     IL_005d

    IL_004f:  newobj     instance void [NDceRpc.ServiceModel.Protobuf]NDceRpc.ServiceModel.Channels.MessageResponse::.ctor()
    IL_0054:  dup
    IL_0055:  ldarg.1
    IL_0056:  call       void ['protobuf-net']ProtoBuf.ProtoReader::NoteObject(object,
                                                                               class ['protobuf-net']ProtoBuf.ProtoReader)
    IL_005b:  starg.s    A_0
    IL_005d:  ldarg.0
    IL_005e:  dup
    IL_005f:  callvirt   instance class [NDceRpc.ServiceModel.Protobuf]NDceRpc.ServiceModel.Channels.RpcErrorData [NDceRpc.ServiceModel.Protobuf]NDceRpc.ServiceModel.Channels.MessageResponse::get_Error()
    IL_0064:  ldarg.1
    IL_0065:  call       valuetype ['protobuf-net']ProtoBuf.SubItemToken ['protobuf-net']ProtoBuf.ProtoReader::StartSubItem(class ['protobuf-net']ProtoBuf.ProtoReader)
    IL_006a:  stloc.1
    IL_006b:  ldarg.1
    IL_006c:  call       class [NDceRpc.ServiceModel.Protobuf]NDceRpc.ServiceModel.Channels.RpcErrorData NDceRpc.ServiceModel.Channels.Messages::Read(class [NDceRpc.ServiceModel.Protobuf]NDceRpc.ServiceModel.Channels.RpcErrorData,
                                                                                                                                                      class ['protobuf-net']ProtoBuf.ProtoReader)
    IL_0071:  ldloc.1
    IL_0072:  ldarg.1
    IL_0073:  call       void ['protobuf-net']ProtoBuf.ProtoReader::EndSubItem(valuetype ['protobuf-net']ProtoBuf.SubItemToken,
                                                                               class ['protobuf-net']ProtoBuf.ProtoReader)
    IL_0078:  dup
    IL_0079:  brfalse.s  IL_0082

    IL_007b:  callvirt   instance void [NDceRpc.ServiceModel.Protobuf]NDceRpc.ServiceModel.Channels.MessageResponse::set_Error(class [NDceRpc.ServiceModel.Protobuf]NDceRpc.ServiceModel.Channels.RpcErrorData)
    IL_0080:  br.s       IL_0084

    IL_0082:  pop
    IL_0083:  pop
    IL_0084:  br         IL_00a4

    IL_0089:  ldarg.0
    IL_008a:  brtrue     IL_009d

    IL_008f:  newobj     instance void [NDceRpc.ServiceModel.Protobuf]NDceRpc.ServiceModel.Channels.MessageResponse::.ctor()
    IL_0094:  dup
    IL_0095:  ldarg.1
    IL_0096:  call       void ['protobuf-net']ProtoBuf.ProtoReader::NoteObject(object,
                                                                               class ['protobuf-net']ProtoBuf.ProtoReader)
    IL_009b:  starg.s    A_0
    IL_009d:  ldarg.1
    IL_009e:  ldarg.0
    IL_009f:  callvirt   instance void ['protobuf-net']ProtoBuf.ProtoReader::AppendExtensionData(class ['protobuf-net']ProtoBuf.IExtensible)
    IL_00a4:  ldarg.1
    IL_00a5:  callvirt   instance int32 ['protobuf-net']ProtoBuf.ProtoReader::ReadFieldHeader()
    IL_00aa:  dup
    IL_00ab:  stloc.0
    IL_00ac:  ldc.i4.0
    IL_00ad:  bgt        IL_0005

    IL_00b2:  ldarg.0
    IL_00b3:  brtrue     IL_00c6

    IL_00b8:  newobj     instance void [NDceRpc.ServiceModel.Protobuf]NDceRpc.ServiceModel.Channels.MessageResponse::.ctor()
    IL_00bd:  dup
    IL_00be:  ldarg.1
    IL_00bf:  call       void ['protobuf-net']ProtoBuf.ProtoReader::NoteObject(object,
                                                                               class ['protobuf-net']ProtoBuf.ProtoReader)
    IL_00c4:  starg.s    A_0
    IL_00c6:  ldarg.0
    IL_00c7:  ret
  } // end of method Messages::Read

  .method private static void  Write(class [NDceRpc.ServiceModel.Protobuf]NDceRpc.ServiceModel.Channels.RpcErrorData A_0,
                                     class ['protobuf-net']ProtoBuf.ProtoWriter A_1) cil managed
  {
    // Code size       124 (0x7c)
    .maxstack  4
    IL_0000:  ldarg.0
    IL_0001:  callvirt   instance class [mscorlib]System.Type [mscorlib]System.Object::GetType()
    IL_0006:  ldtoken    [NDceRpc.ServiceModel.Protobuf]NDceRpc.ServiceModel.Channels.RpcErrorData
    IL_000b:  call       class [mscorlib]System.Type [mscorlib]System.Type::GetTypeFromHandle(valuetype [mscorlib]System.RuntimeTypeHandle)
    IL_0010:  beq.s      IL_0027

    IL_0012:  ldtoken    [NDceRpc.ServiceModel.Protobuf]NDceRpc.ServiceModel.Channels.RpcErrorData
    IL_0017:  call       class [mscorlib]System.Type [mscorlib]System.Type::GetTypeFromHandle(valuetype [mscorlib]System.RuntimeTypeHandle)
    IL_001c:  ldarg.0
    IL_001d:  callvirt   instance class [mscorlib]System.Type [mscorlib]System.Object::GetType()
    IL_0022:  call       void ['protobuf-net']ProtoBuf.Meta.TypeModel::ThrowUnexpectedSubtype(class [mscorlib]System.Type,
                                                                                              class [mscorlib]System.Type)
    IL_0027:  ldarg.0
    IL_0028:  callvirt   instance string [NDceRpc.ServiceModel.Protobuf]NDceRpc.ServiceModel.Channels.RpcErrorData::get_Type()
    IL_002d:  dup
    IL_002e:  brtrue.s   IL_0036

    IL_0030:  pop
    IL_0031:  br         IL_0044

    IL_0036:  ldc.i4.1
    IL_0037:  ldc.i4.2
    IL_0038:  ldarg.1
    IL_0039:  call       void ['protobuf-net']ProtoBuf.ProtoWriter::WriteFieldHeader(int32,
                                                                                     valuetype ['protobuf-net']ProtoBuf.WireType,
                                                                                     class ['protobuf-net']ProtoBuf.ProtoWriter)
    IL_003e:  ldarg.1
    IL_003f:  call       void ['protobuf-net']ProtoBuf.ProtoWriter::WriteString(string,
                                                                                class ['protobuf-net']ProtoBuf.ProtoWriter)
    IL_0044:  ldarg.0
    IL_0045:  callvirt   instance string [NDceRpc.ServiceModel.Protobuf]NDceRpc.ServiceModel.Channels.RpcErrorData::get_Message()
    IL_004a:  dup
    IL_004b:  brtrue.s   IL_0053

    IL_004d:  pop
    IL_004e:  br         IL_0074

    IL_0053:  dup
    IL_0054:  ldstr      ""
    IL_0059:  call       bool [mscorlib]System.String::op_Equality(string,
                                                                   string)
    IL_005e:  brtrue     IL_0073

    IL_0063:  ldc.i4.2
    IL_0064:  ldc.i4.2
    IL_0065:  ldarg.1
    IL_0066:  call       void ['protobuf-net']ProtoBuf.ProtoWriter::WriteFieldHeader(int32,
                                                                                     valuetype ['protobuf-net']ProtoBuf.WireType,
                                                                                     class ['protobuf-net']ProtoBuf.ProtoWriter)
    IL_006b:  ldarg.1
    IL_006c:  call       void ['protobuf-net']ProtoBuf.ProtoWriter::WriteString(string,
                                                                                class ['protobuf-net']ProtoBuf.ProtoWriter)
    IL_0071:  br.s       IL_0074

    IL_0073:  pop
    IL_0074:  ldarg.0
    IL_0075:  ldarg.1
    IL_0076:  call       void ['protobuf-net']ProtoBuf.ProtoWriter::AppendExtensionData(class ['protobuf-net']ProtoBuf.IExtensible,
                                                                                        class ['protobuf-net']ProtoBuf.ProtoWriter)
    IL_007b:  ret
  } // end of method Messages::Write

  .method private static class [NDceRpc.ServiceModel.Protobuf]NDceRpc.ServiceModel.Channels.RpcErrorData 
          Read(class [NDceRpc.ServiceModel.Protobuf]NDceRpc.ServiceModel.Channels.RpcErrorData A_0,
               class ['protobuf-net']ProtoBuf.ProtoReader A_1) cil managed
  {
    // Code size       174 (0xae)
    .maxstack  3
    .locals init (int32 V_0)
    IL_0000:  br         IL_008a

    IL_0005:  ldloc.0
    IL_0006:  ldc.i4.1
    IL_0007:  beq.s      IL_000e

    IL_0009:  br         IL_003a

    IL_000e:  ldarg.0
    IL_000f:  brtrue     IL_0022

    IL_0014:  newobj     instance void [NDceRpc.ServiceModel.Protobuf]NDceRpc.ServiceModel.Channels.RpcErrorData::.ctor()
    IL_0019:  dup
    IL_001a:  ldarg.1
    IL_001b:  call       void ['protobuf-net']ProtoBuf.ProtoReader::NoteObject(object,
                                                                               class ['protobuf-net']ProtoBuf.ProtoReader)
    IL_0020:  starg.s    A_0
    IL_0022:  ldarg.0
    IL_0023:  ldarg.1
    IL_0024:  callvirt   instance string ['protobuf-net']ProtoBuf.ProtoReader::ReadString()
    IL_0029:  dup
    IL_002a:  brfalse.s  IL_0033

    IL_002c:  callvirt   instance void [NDceRpc.ServiceModel.Protobuf]NDceRpc.ServiceModel.Channels.RpcErrorData::set_Type(string)
    IL_0031:  br.s       IL_0035

    IL_0033:  pop
    IL_0034:  pop
    IL_0035:  br         IL_008a

    IL_003a:  ldloc.0
    IL_003b:  ldc.i4.2
    IL_003c:  beq.s      IL_0043

    IL_003e:  br         IL_006f

    IL_0043:  ldarg.0
    IL_0044:  brtrue     IL_0057

    IL_0049:  newobj     instance void [NDceRpc.ServiceModel.Protobuf]NDceRpc.ServiceModel.Channels.RpcErrorData::.ctor()
    IL_004e:  dup
    IL_004f:  ldarg.1
    IL_0050:  call       void ['protobuf-net']ProtoBuf.ProtoReader::NoteObject(object,
                                                                               class ['protobuf-net']ProtoBuf.ProtoReader)
    IL_0055:  starg.s    A_0
    IL_0057:  ldarg.0
    IL_0058:  ldarg.1
    IL_0059:  callvirt   instance string ['protobuf-net']ProtoBuf.ProtoReader::ReadString()
    IL_005e:  dup
    IL_005f:  brfalse.s  IL_0068

    IL_0061:  callvirt   instance void [NDceRpc.ServiceModel.Protobuf]NDceRpc.ServiceModel.Channels.RpcErrorData::set_Message(string)
    IL_0066:  br.s       IL_006a

    IL_0068:  pop
    IL_0069:  pop
    IL_006a:  br         IL_008a

    IL_006f:  ldarg.0
    IL_0070:  brtrue     IL_0083

    IL_0075:  newobj     instance void [NDceRpc.ServiceModel.Protobuf]NDceRpc.ServiceModel.Channels.RpcErrorData::.ctor()
    IL_007a:  dup
    IL_007b:  ldarg.1
    IL_007c:  call       void ['protobuf-net']ProtoBuf.ProtoReader::NoteObject(object,
                                                                               class ['protobuf-net']ProtoBuf.ProtoReader)
    IL_0081:  starg.s    A_0
    IL_0083:  ldarg.1
    IL_0084:  ldarg.0
    IL_0085:  callvirt   instance void ['protobuf-net']ProtoBuf.ProtoReader::AppendExtensionData(class ['protobuf-net']ProtoBuf.IExtensible)
    IL_008a:  ldarg.1
    IL_008b:  callvirt   instance int32 ['protobuf-net']ProtoBuf.ProtoReader::ReadFieldHeader()
    IL_0090:  dup
    IL_0091:  stloc.0
    IL_0092:  ldc.i4.0
    IL_0093:  bgt        IL_0005

    IL_0098:  ldarg.0
    IL_0099:  brtrue     IL_00ac

    IL_009e:  newobj     instance void [NDceRpc.ServiceModel.Protobuf]NDceRpc.ServiceModel.Channels.RpcErrorData::.ctor()
    IL_00a3:  dup
    IL_00a4:  ldarg.1
    IL_00a5:  call       void ['protobuf-net']ProtoBuf.ProtoReader::NoteObject(object,
                                                                               class ['protobuf-net']ProtoBuf.ProtoReader)
    IL_00aa:  starg.s    A_0
    IL_00ac:  ldarg.0
    IL_00ad:  ret
  } // end of method Messages::Read

  .method private static void  Write(class [NDceRpc.ServiceModel.Protobuf]NDceRpc.ServiceModel.Channels.RpcParamData A_0,
                                     class ['protobuf-net']ProtoBuf.ProtoWriter A_1) cil managed
  {
    // Code size       96 (0x60)
    .maxstack  4
    IL_0000:  ldarg.0
    IL_0001:  callvirt   instance class [mscorlib]System.Type [mscorlib]System.Object::GetType()
    IL_0006:  ldtoken    [NDceRpc.ServiceModel.Protobuf]NDceRpc.ServiceModel.Channels.RpcParamData
    IL_000b:  call       class [mscorlib]System.Type [mscorlib]System.Type::GetTypeFromHandle(valuetype [mscorlib]System.RuntimeTypeHandle)
    IL_0010:  beq.s      IL_0027

    IL_0012:  ldtoken    [NDceRpc.ServiceModel.Protobuf]NDceRpc.ServiceModel.Channels.RpcParamData
    IL_0017:  call       class [mscorlib]System.Type [mscorlib]System.Type::GetTypeFromHandle(valuetype [mscorlib]System.RuntimeTypeHandle)
    IL_001c:  ldarg.0
    IL_001d:  callvirt   instance class [mscorlib]System.Type [mscorlib]System.Object::GetType()
    IL_0022:  call       void ['protobuf-net']ProtoBuf.Meta.TypeModel::ThrowUnexpectedSubtype(class [mscorlib]System.Type,
                                                                                              class [mscorlib]System.Type)
    IL_0027:  ldarg.0
    IL_0028:  callvirt   instance int32 [NDceRpc.ServiceModel.Protobuf]NDceRpc.ServiceModel.Channels.RpcParamData::get_Identifier()
    IL_002d:  ldc.i4.1
    IL_002e:  ldc.i4.0
    IL_002f:  ldarg.1
    IL_0030:  call       void ['protobuf-net']ProtoBuf.ProtoWriter::WriteFieldHeader(int32,
                                                                                     valuetype ['protobuf-net']ProtoBuf.WireType,
                                                                                     class ['protobuf-net']ProtoBuf.ProtoWriter)
    IL_0035:  ldarg.1
    IL_0036:  call       void ['protobuf-net']ProtoBuf.ProtoWriter::WriteInt32(int32,
                                                                               class ['protobuf-net']ProtoBuf.ProtoWriter)
    IL_003b:  ldarg.0
    IL_003c:  callvirt   instance uint8[] [NDceRpc.ServiceModel.Protobuf]NDceRpc.ServiceModel.Channels.RpcParamData::get_Data()
    IL_0041:  dup
    IL_0042:  brtrue.s   IL_004a

    IL_0044:  pop
    IL_0045:  br         IL_0058

    IL_004a:  ldc.i4.2
    IL_004b:  ldc.i4.2
    IL_004c:  ldarg.1
    IL_004d:  call       void ['protobuf-net']ProtoBuf.ProtoWriter::WriteFieldHeader(int32,
                                                                                     valuetype ['protobuf-net']ProtoBuf.WireType,
                                                                                     class ['protobuf-net']ProtoBuf.ProtoWriter)
    IL_0052:  ldarg.1
    IL_0053:  call       void ['protobuf-net']ProtoBuf.ProtoWriter::WriteBytes(uint8[],
                                                                               class ['protobuf-net']ProtoBuf.ProtoWriter)
    IL_0058:  ldarg.0
    IL_0059:  ldarg.1
    IL_005a:  call       void ['protobuf-net']ProtoBuf.ProtoWriter::AppendExtensionData(class ['protobuf-net']ProtoBuf.IExtensible,
                                                                                        class ['protobuf-net']ProtoBuf.ProtoWriter)
    IL_005f:  ret
  } // end of method Messages::Write

  .method private static class [NDceRpc.ServiceModel.Protobuf]NDceRpc.ServiceModel.Channels.RpcParamData 
          Read(class [NDceRpc.ServiceModel.Protobuf]NDceRpc.ServiceModel.Channels.RpcParamData A_0,
               class ['protobuf-net']ProtoBuf.ProtoReader A_1) cil managed
  {
    // Code size       173 (0xad)
    .maxstack  3
    .locals init (int32 V_0)
    IL_0000:  br         IL_0089

    IL_0005:  ldloc.0
    IL_0006:  ldc.i4.1
    IL_0007:  beq.s      IL_000e

    IL_0009:  br         IL_0033

    IL_000e:  ldarg.0
    IL_000f:  brtrue     IL_0022

    IL_0014:  newobj     instance void [NDceRpc.ServiceModel.Protobuf]NDceRpc.ServiceModel.Channels.RpcParamData::.ctor()
    IL_0019:  dup
    IL_001a:  ldarg.1
    IL_001b:  call       void ['protobuf-net']ProtoBuf.ProtoReader::NoteObject(object,
                                                                               class ['protobuf-net']ProtoBuf.ProtoReader)
    IL_0020:  starg.s    A_0
    IL_0022:  ldarg.0
    IL_0023:  ldarg.1
    IL_0024:  callvirt   instance int32 ['protobuf-net']ProtoBuf.ProtoReader::ReadInt32()
    IL_0029:  callvirt   instance void [NDceRpc.ServiceModel.Protobuf]NDceRpc.ServiceModel.Channels.RpcParamData::set_Identifier(int32)
    IL_002e:  br         IL_0089

    IL_0033:  ldloc.0
    IL_0034:  ldc.i4.2
    IL_0035:  beq.s      IL_003c

    IL_0037:  br         IL_006e

    IL_003c:  ldarg.0
    IL_003d:  brtrue     IL_0050

    IL_0042:  newobj     instance void [NDceRpc.ServiceModel.Protobuf]NDceRpc.ServiceModel.Channels.RpcParamData::.ctor()
    IL_0047:  dup
    IL_0048:  ldarg.1
    IL_0049:  call       void ['protobuf-net']ProtoBuf.ProtoReader::NoteObject(object,
                                                                               class ['protobuf-net']ProtoBuf.ProtoReader)
    IL_004e:  starg.s    A_0
    IL_0050:  ldarg.0
    IL_0051:  dup
    IL_0052:  callvirt   instance uint8[] [NDceRpc.ServiceModel.Protobuf]NDceRpc.ServiceModel.Channels.RpcParamData::get_Data()
    IL_0057:  ldarg.1
    IL_0058:  call       uint8[] ['protobuf-net']ProtoBuf.ProtoReader::AppendBytes(uint8[],
                                                                                   class ['protobuf-net']ProtoBuf.ProtoReader)
    IL_005d:  dup
    IL_005e:  brfalse.s  IL_0067

    IL_0060:  callvirt   instance void [NDceRpc.ServiceModel.Protobuf]NDceRpc.ServiceModel.Channels.RpcParamData::set_Data(uint8[])
    IL_0065:  br.s       IL_0069

    IL_0067:  pop
    IL_0068:  pop
    IL_0069:  br         IL_0089

    IL_006e:  ldarg.0
    IL_006f:  brtrue     IL_0082

    IL_0074:  newobj     instance void [NDceRpc.ServiceModel.Protobuf]NDceRpc.ServiceModel.Channels.RpcParamData::.ctor()
    IL_0079:  dup
    IL_007a:  ldarg.1
    IL_007b:  call       void ['protobuf-net']ProtoBuf.ProtoReader::NoteObject(object,
                                                                               class ['protobuf-net']ProtoBuf.ProtoReader)
    IL_0080:  starg.s    A_0
    IL_0082:  ldarg.1
    IL_0083:  ldarg.0
    IL_0084:  callvirt   instance void ['protobuf-net']ProtoBuf.ProtoReader::AppendExtensionData(class ['protobuf-net']ProtoBuf.IExtensible)
    IL_0089:  ldarg.1
    IL_008a:  callvirt   instance int32 ['protobuf-net']ProtoBuf.ProtoReader::ReadFieldHeader()
    IL_008f:  dup
    IL_0090:  stloc.0
    IL_0091:  ldc.i4.0
    IL_0092:  bgt        IL_0005

    IL_0097:  ldarg.0
    IL_0098:  brtrue     IL_00ab

    IL_009d:  newobj     instance void [NDceRpc.ServiceModel.Protobuf]NDceRpc.ServiceModel.Channels.RpcParamData::.ctor()
    IL_00a2:  dup
    IL_00a3:  ldarg.1
    IL_00a4:  call       void ['protobuf-net']ProtoBuf.ProtoReader::NoteObject(object,
                                                                               class ['protobuf-net']ProtoBuf.ProtoReader)
    IL_00a9:  starg.s    A_0
    IL_00ab:  ldarg.0
    IL_00ac:  ret
  } // end of method Messages::Read

  .method family hidebysig newslot virtual final 
          instance int32  GetKeyImpl(class [mscorlib]System.Type A_1) cil managed
  {
    .override ['protobuf-net']ProtoBuf.Meta.TypeModel::GetKeyImpl
    // Code size       12 (0xc)
    .maxstack  8
    IL_0000:  ldsfld     class [mscorlib]System.Type[] NDceRpc.ServiceModel.Channels.Messages::knownTypes
    IL_0005:  ldarg.1
    IL_0006:  callvirt   instance int32 [mscorlib]System.Collections.IList::IndexOf(object)
    IL_000b:  ret
  } // end of method Messages::GetKeyImpl

  .method famorassem hidebysig newslot virtual final 
          instance void  Serialize(int32 A_1,
                                   object A_2,
                                   class ['protobuf-net']ProtoBuf.ProtoWriter A_3) cil managed
  {
    .override ['protobuf-net']ProtoBuf.Meta.TypeModel::Serialize
    // Code size       75 (0x4b)
    .maxstack  2
    IL_0000:  ldarg.1
    IL_0001:  switch     ( 
                          IL_0017,
                          IL_0024,
                          IL_0031,
                          IL_003e)
    IL_0016:  ret

    IL_0017:  ldarg.2
    IL_0018:  castclass  [NDceRpc.ServiceModel.Protobuf]NDceRpc.ServiceModel.Channels.MessageRequest
    IL_001d:  ldarg.3
    IL_001e:  call       void NDceRpc.ServiceModel.Channels.Messages::Write(class [NDceRpc.ServiceModel.Protobuf]NDceRpc.ServiceModel.Channels.MessageRequest,
                                                                            class ['protobuf-net']ProtoBuf.ProtoWriter)
    IL_0023:  ret

    IL_0024:  ldarg.2
    IL_0025:  castclass  [NDceRpc.ServiceModel.Protobuf]NDceRpc.ServiceModel.Channels.MessageResponse
    IL_002a:  ldarg.3
    IL_002b:  call       void NDceRpc.ServiceModel.Channels.Messages::Write(class [NDceRpc.ServiceModel.Protobuf]NDceRpc.ServiceModel.Channels.MessageResponse,
                                                                            class ['protobuf-net']ProtoBuf.ProtoWriter)
    IL_0030:  ret

    IL_0031:  ldarg.2
    IL_0032:  castclass  [NDceRpc.ServiceModel.Protobuf]NDceRpc.ServiceModel.Channels.RpcErrorData
    IL_0037:  ldarg.3
    IL_0038:  call       void NDceRpc.ServiceModel.Channels.Messages::Write(class [NDceRpc.ServiceModel.Protobuf]NDceRpc.ServiceModel.Channels.RpcErrorData,
                                                                            class ['protobuf-net']ProtoBuf.ProtoWriter)
    IL_003d:  ret

    IL_003e:  ldarg.2
    IL_003f:  castclass  [NDceRpc.ServiceModel.Protobuf]NDceRpc.ServiceModel.Channels.RpcParamData
    IL_0044:  ldarg.3
    IL_0045:  call       void NDceRpc.ServiceModel.Channels.Messages::Write(class [NDceRpc.ServiceModel.Protobuf]NDceRpc.ServiceModel.Channels.RpcParamData,
                                                                            class ['protobuf-net']ProtoBuf.ProtoWriter)
    IL_004a:  ret
  } // end of method Messages::Serialize

  .method famorassem hidebysig newslot virtual final 
          instance object  Deserialize(int32 A_1,
                                       object A_2,
                                       class ['protobuf-net']ProtoBuf.ProtoReader A_3) cil managed
  {
    .override ['protobuf-net']ProtoBuf.Meta.TypeModel::Deserialize
    // Code size       76 (0x4c)
    .maxstack  2
    IL_0000:  ldarg.1
    IL_0001:  switch     ( 
                          IL_0018,
                          IL_0025,
                          IL_0032,
                          IL_003f)
    IL_0016:  ldnull
    IL_0017:  ret

    IL_0018:  ldarg.2
    IL_0019:  castclass  [NDceRpc.ServiceModel.Protobuf]NDceRpc.ServiceModel.Channels.MessageRequest
    IL_001e:  ldarg.3
    IL_001f:  call       class [NDceRpc.ServiceModel.Protobuf]NDceRpc.ServiceModel.Channels.MessageRequest NDceRpc.ServiceModel.Channels.Messages::Read(class [NDceRpc.ServiceModel.Protobuf]NDceRpc.ServiceModel.Channels.MessageRequest,
                                                                                                                                                        class ['protobuf-net']ProtoBuf.ProtoReader)
    IL_0024:  ret

    IL_0025:  ldarg.2
    IL_0026:  castclass  [NDceRpc.ServiceModel.Protobuf]NDceRpc.ServiceModel.Channels.MessageResponse
    IL_002b:  ldarg.3
    IL_002c:  call       class [NDceRpc.ServiceModel.Protobuf]NDceRpc.ServiceModel.Channels.MessageResponse NDceRpc.ServiceModel.Channels.Messages::Read(class [NDceRpc.ServiceModel.Protobuf]NDceRpc.ServiceModel.Channels.MessageResponse,
                                                                                                                                                         class ['protobuf-net']ProtoBuf.ProtoReader)
    IL_0031:  ret

    IL_0032:  ldarg.2
    IL_0033:  castclass  [NDceRpc.ServiceModel.Protobuf]NDceRpc.ServiceModel.Channels.RpcErrorData
    IL_0038:  ldarg.3
    IL_0039:  call       class [NDceRpc.ServiceModel.Protobuf]NDceRpc.ServiceModel.Channels.RpcErrorData NDceRpc.ServiceModel.Channels.Messages::Read(class [NDceRpc.ServiceModel.Protobuf]NDceRpc.ServiceModel.Channels.RpcErrorData,
                                                                                                                                                      class ['protobuf-net']ProtoBuf.ProtoReader)
    IL_003e:  ret

    IL_003f:  ldarg.2
    IL_0040:  castclass  [NDceRpc.ServiceModel.Protobuf]NDceRpc.ServiceModel.Channels.RpcParamData
    IL_0045:  ldarg.3
    IL_0046:  call       class [NDceRpc.ServiceModel.Protobuf]NDceRpc.ServiceModel.Channels.RpcParamData NDceRpc.ServiceModel.Channels.Messages::Read(class [NDceRpc.ServiceModel.Protobuf]NDceRpc.ServiceModel.Channels.RpcParamData,
                                                                                                                                                      class ['protobuf-net']ProtoBuf.ProtoReader)
    IL_004b:  ret
  } // end of method Messages::Deserialize

  .method public specialname rtspecialname 
          instance void  .ctor() cil managed
  {
    // Code size       7 (0x7)
    .maxstack  8
    IL_0000:  ldarg.0
    IL_0001:  call       instance void ['protobuf-net']ProtoBuf.Meta.TypeModel::.ctor()
    IL_0006:  ret
  } // end of method Messages::.ctor

  .method private specialname rtspecialname static 
          void  .cctor() cil managed
  {
    // Code size       64 (0x40)
    .maxstack  4
    IL_0000:  ldc.i4.4
    IL_0001:  newarr     [mscorlib]System.Type
    IL_0006:  dup
    IL_0007:  ldc.i4.0
    IL_0008:  ldtoken    [NDceRpc.ServiceModel.Protobuf]NDceRpc.ServiceModel.Channels.MessageRequest
    IL_000d:  call       class [mscorlib]System.Type [mscorlib]System.Type::GetTypeFromHandle(valuetype [mscorlib]System.RuntimeTypeHandle)
    IL_0012:  stelem.ref
    IL_0013:  dup
    IL_0014:  ldc.i4.1
    IL_0015:  ldtoken    [NDceRpc.ServiceModel.Protobuf]NDceRpc.ServiceModel.Channels.MessageResponse
    IL_001a:  call       class [mscorlib]System.Type [mscorlib]System.Type::GetTypeFromHandle(valuetype [mscorlib]System.RuntimeTypeHandle)
    IL_001f:  stelem.ref
    IL_0020:  dup
    IL_0021:  ldc.i4.2
    IL_0022:  ldtoken    [NDceRpc.ServiceModel.Protobuf]NDceRpc.ServiceModel.Channels.RpcErrorData
    IL_0027:  call       class [mscorlib]System.Type [mscorlib]System.Type::GetTypeFromHandle(valuetype [mscorlib]System.RuntimeTypeHandle)
    IL_002c:  stelem.ref
    IL_002d:  dup
    IL_002e:  ldc.i4.3
    IL_002f:  ldtoken    [NDceRpc.ServiceModel.Protobuf]NDceRpc.ServiceModel.Channels.RpcParamData
    IL_0034:  call       class [mscorlib]System.Type [mscorlib]System.Type::GetTypeFromHandle(valuetype [mscorlib]System.RuntimeTypeHandle)
    IL_0039:  stelem.ref
    IL_003a:  stsfld     class [mscorlib]System.Type[] NDceRpc.ServiceModel.Channels.Messages::knownTypes
    IL_003f:  ret
  } // end of method Messages::.cctor

} // end of class NDceRpc.ServiceModel.Channels.Messages


// =============================================================

// *********** DISASSEMBLY COMPLETE ***********************
