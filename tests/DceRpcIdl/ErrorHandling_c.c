

/* this ALWAYS GENERATED file contains the RPC client stubs */


 /* File created by MIDL compiler version 7.00.0555 */
/* at Fri May 23 14:32:24 2014
 */
/* Compiler settings for ErrorHandling.idl:
    Oicf, W1, Zp8, env=Win32 (32b run), target_arch=X86 7.00.0555 
    protocol : dce , ms_ext, c_ext, robust
    error checks: allocation ref bounds_check enum stub_data 
    VC __declspec() decoration level: 
         __declspec(uuid()), __declspec(selectany), __declspec(novtable)
         DECLSPEC_UUID(), MIDL_INTERFACE()
*/
/* @@MIDL_FILE_HEADING(  ) */

#if !defined(_M_IA64) && !defined(_M_AMD64)


#pragma warning( disable: 4049 )  /* more than 64k source lines */
#if _MSC_VER >= 1200
#pragma warning(push)
#endif

#pragma warning( disable: 4211 )  /* redefine extern to static */
#pragma warning( disable: 4232 )  /* dllimport identity*/
#pragma warning( disable: 4024 )  /* array to pointer mapping*/
#pragma warning( disable: 4100 ) /* unreferenced arguments in x86 call */

#pragma optimize("", off ) 

#include <string.h>

#include "ErrorHandling_h.h"

#define TYPE_FORMAT_STRING_SIZE   7                                 
#define PROC_FORMAT_STRING_SIZE   69                                
#define EXPR_FORMAT_STRING_SIZE   1                                 
#define TRANSMIT_AS_TABLE_SIZE    0            
#define WIRE_MARSHAL_TABLE_SIZE   0            

typedef struct _ErrorHandling_MIDL_TYPE_FORMAT_STRING
    {
    short          Pad;
    unsigned char  Format[ TYPE_FORMAT_STRING_SIZE ];
    } ErrorHandling_MIDL_TYPE_FORMAT_STRING;

typedef struct _ErrorHandling_MIDL_PROC_FORMAT_STRING
    {
    short          Pad;
    unsigned char  Format[ PROC_FORMAT_STRING_SIZE ];
    } ErrorHandling_MIDL_PROC_FORMAT_STRING;

typedef struct _ErrorHandling_MIDL_EXPR_FORMAT_STRING
    {
    long          Pad;
    unsigned char  Format[ EXPR_FORMAT_STRING_SIZE ];
    } ErrorHandling_MIDL_EXPR_FORMAT_STRING;


static const RPC_SYNTAX_IDENTIFIER  _RpcTransferSyntax = 
{{0x8A885D04,0x1CEB,0x11C9,{0x9F,0xE8,0x08,0x00,0x2B,0x10,0x48,0x60}},{2,0}};


extern const ErrorHandling_MIDL_TYPE_FORMAT_STRING ErrorHandling__MIDL_TypeFormatString;
extern const ErrorHandling_MIDL_PROC_FORMAT_STRING ErrorHandling__MIDL_ProcFormatString;
extern const ErrorHandling_MIDL_EXPR_FORMAT_STRING ErrorHandling__MIDL_ExprFormatString;

#define GENERIC_BINDING_TABLE_SIZE   0            


/* Standard interface: ErrorHandling, ver. 0.1,
   GUID={0x2630154D,0x9C04,0x41AC,{0xB1,0xDE,0x2A,0xAA,0xA4,0x15,0xF1,0xE3}} */



static const RPC_CLIENT_INTERFACE ErrorHandling___RpcClientInterface =
    {
    sizeof(RPC_CLIENT_INTERFACE),
    {{0x2630154D,0x9C04,0x41AC,{0xB1,0xDE,0x2A,0xAA,0xA4,0x15,0xF1,0xE3}},{0,1}},
    {{0x8A885D04,0x1CEB,0x11C9,{0x9F,0xE8,0x08,0x00,0x2B,0x10,0x48,0x60}},{2,0}},
    0,
    0,
    0,
    0,
    0,
    0x00000000
    };
RPC_IF_HANDLE ErrorHandling_v0_1_c_ifspec = (RPC_IF_HANDLE)& ErrorHandling___RpcClientInterface;

extern const MIDL_STUB_DESC ErrorHandling_StubDesc;

static RPC_BINDING_HANDLE ErrorHandling__MIDL_AutoBindHandle;


/* [comm_status] */ error_status_t DoReturnErrors( 
    /* [in] */ handle_t hBinding,
    /* [fault_status][out] */ error_status_t *fault_s)
{

    CLIENT_CALL_RETURN _RetVal;

    _RetVal = NdrClientCall2(
                  ( PMIDL_STUB_DESC  )&ErrorHandling_StubDesc,
                  (PFORMAT_STRING) &ErrorHandling__MIDL_ProcFormatString.Format[0],
                  ( unsigned char * )&hBinding);
    return ( error_status_t  )_RetVal.Simple;
    
}


void DoThrowCppException( 
    /* [in] */ handle_t hBinding)
{

    NdrClientCall2(
                  ( PMIDL_STUB_DESC  )&ErrorHandling_StubDesc,
                  (PFORMAT_STRING) &ErrorHandling__MIDL_ProcFormatString.Format[40],
                  ( unsigned char * )&hBinding);
    
}


#if !defined(__RPC_WIN32__)
#error  Invalid build platform for this stub.
#endif

#if !(TARGET_IS_NT50_OR_LATER)
#error You need Windows 2000 or later to run this stub because it uses these features:
#error   /robust command line switch.
#error However, your C/C++ compilation flags indicate you intend to run this app on earlier systems.
#error This app will fail with the RPC_X_WRONG_STUB_VERSION error.
#endif


static const ErrorHandling_MIDL_PROC_FORMAT_STRING ErrorHandling__MIDL_ProcFormatString =
    {
        0,
        {

	/* Procedure DoReturnErrors */

			0x0,		/* 0 */
			0x68,		/* Old Flags:  comm or fault/decode */
/*  2 */	NdrFcLong( 0x0 ),	/* 0 */
/*  6 */	NdrFcShort( 0x0 ),	/* 0 */
/*  8 */	NdrFcShort( 0xc ),	/* x86 Stack size/offset = 12 */
/* 10 */	0x32,		/* FC_BIND_PRIMITIVE */
			0x0,		/* 0 */
/* 12 */	NdrFcShort( 0x0 ),	/* x86 Stack size/offset = 0 */
/* 14 */	NdrFcShort( 0x0 ),	/* 0 */
/* 16 */	NdrFcShort( 0x24 ),	/* 36 */
/* 18 */	0x44,		/* Oi2 Flags:  has return, has ext, */
			0x2,		/* 2 */
/* 20 */	0x8,		/* 8 */
			0x1,		/* Ext Flags:  new corr desc, */
/* 22 */	NdrFcShort( 0x0 ),	/* 0 */
/* 24 */	NdrFcShort( 0x0 ),	/* 0 */
/* 26 */	NdrFcShort( 0x0 ),	/* 0 */

	/* Parameter hBinding */

/* 28 */	NdrFcShort( 0x2150 ),	/* Flags:  out, base type, simple ref, srv alloc size=8 */
/* 30 */	NdrFcShort( 0x4 ),	/* x86 Stack size/offset = 4 */
/* 32 */	0x10,		/* FC_ERROR_STATUS_T */
			0x0,		/* 0 */

	/* Parameter fault_s */

/* 34 */	NdrFcShort( 0x70 ),	/* Flags:  out, return, base type, */
/* 36 */	NdrFcShort( 0x8 ),	/* x86 Stack size/offset = 8 */
/* 38 */	0x10,		/* FC_ERROR_STATUS_T */
			0x0,		/* 0 */

	/* Procedure DoThrowCppException */


	/* Return value */

/* 40 */	0x0,		/* 0 */
			0x48,		/* Old Flags:  */
/* 42 */	NdrFcLong( 0x0 ),	/* 0 */
/* 46 */	NdrFcShort( 0x1 ),	/* 1 */
/* 48 */	NdrFcShort( 0x4 ),	/* x86 Stack size/offset = 4 */
/* 50 */	0x32,		/* FC_BIND_PRIMITIVE */
			0x0,		/* 0 */
/* 52 */	NdrFcShort( 0x0 ),	/* x86 Stack size/offset = 0 */
/* 54 */	NdrFcShort( 0x0 ),	/* 0 */
/* 56 */	NdrFcShort( 0x0 ),	/* 0 */
/* 58 */	0x40,		/* Oi2 Flags:  has ext, */
			0x0,		/* 0 */
/* 60 */	0x8,		/* 8 */
			0x1,		/* Ext Flags:  new corr desc, */
/* 62 */	NdrFcShort( 0x0 ),	/* 0 */
/* 64 */	NdrFcShort( 0x0 ),	/* 0 */
/* 66 */	NdrFcShort( 0x0 ),	/* 0 */

			0x0
        }
    };

static const ErrorHandling_MIDL_TYPE_FORMAT_STRING ErrorHandling__MIDL_TypeFormatString =
    {
        0,
        {
			NdrFcShort( 0x0 ),	/* 0 */
/*  2 */	
			0x11, 0xc,	/* FC_RP [alloced_on_stack] [simple_pointer] */
/*  4 */	0x10,		/* FC_ERROR_STATUS_T */
			0x5c,		/* FC_PAD */

			0x0
        }
    };

static const unsigned short ErrorHandling_FormatStringOffsetTable[] =
    {
    0,
    40
    };


static const COMM_FAULT_OFFSETS ErrorHandling_CommFaultOffsets[] = 
{
	{ -1, 4 },	/* x86 Offsets for DoReturnErrors */
	{ -2, -2 }
};


static const MIDL_STUB_DESC ErrorHandling_StubDesc = 
    {
    (void *)& ErrorHandling___RpcClientInterface,
    MIDL_user_allocate,
    MIDL_user_free,
    &ErrorHandling__MIDL_AutoBindHandle,
    0,
    0,
    0,
    0,
    ErrorHandling__MIDL_TypeFormatString.Format,
    1, /* -error bounds_check flag */
    0x50002, /* Ndr library version */
    0,
    0x700022b, /* MIDL Version 7.0.555 */
    ErrorHandling_CommFaultOffsets,
    0,
    0,  /* notify & notify_flag routine table */
    0x1, /* MIDL flag */
    0, /* cs routines */
    0,   /* proxy/server info */
    0
    };
#pragma optimize("", on )
#if _MSC_VER >= 1200
#pragma warning(pop)
#endif


#endif /* !defined(_M_IA64) && !defined(_M_AMD64)*/

