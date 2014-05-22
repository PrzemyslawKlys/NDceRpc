midlc
A MIDL Compatible IDL compiler in C

Mon Nov 27 11:59:02 EST 2006
midlc-0.6.1 released

This  release has the minor changes necessary to generate stubs from JCIFS'
MSRPC  infrastructure.  For your convenience this package also now includes
libmba-0.9.1.  To build it will be necessary to first build libmba and then
midlc like: 

  $ cd libmba-0.9.1
  $ make ar
  $ cd ..
  $ make
  # make install

Note:  The  'make  install'  is optional but if you do not do this you will
need to specify the correct symtab*.txt file on the command line like: 

  $ path/to/midlc -s path/to/symtabjcifs.txt foo.idl

Wed Feb  9 23:37:35 EST 2005
midlc-0.6.0 released

This release adds support for Samba4 stubs. Only the rpcecho test interface
has  been  demonstrated  to  work  and  the call-glue used to translate the
Samba4  specific  nature  of PIDL stubs is a little goofy so I don't expect
anyone  to  get  a wide variety of interfaces working easily. But this is a
good first step. The important thing is that marshalling routines should be
fairly  solid  (minus proper error checking and other related things). They
were  tested  externally using a driver program to call LsarLookupSids on a
blob of data exported from Ethereal.

To  run  the  rpcecho example you generate the stub, edit the first line of
the  Makefile  to  point  to  the samba4 directory, compile the stub, build
samba4,  overwrite  samba4/source/librpc/ndr_gen/ndr_echo.o  with  the  new
stub,  and  relink/reinstall the samba4 binaries (or just smbd really). The
command dialog might look like: 

  $ ./midlc -v -t samba -o ndr_echo idl/echo.idl
  <edit first line of Makefile to point to samba4>
  $ make echo
  <build samba4>
  $ cp ndr_echo <samba4dir>/source/librpc/gen_ndr
  $ cd <samba4dir>/source
  $ make
  Linking bin/smbtorture
  Linking bin/ndrdump
  Linking bin/smbd

Now  run  smbd  and  test the echo interface. I was unable to test the last
operation because the version of samba4 I have has an enum bug (that tridge
knows about and fixed in svn). 

Thu Dec 16 00:42:02 EST 2004

It  has  been known for some time that decoder routines of Java stubs would
need  to be adjusted to allocate objects as necessary. In addition to plain
objects  such  as  lsa_TrustInformation.sid any embedded objects and arrays
need  to  be  allocated  on  the  fly if they are null unlike in C where an
embedded  object  occupies the memory of the enclosing structure. The issue
became  a  show  stopper  when  implementing  the somewhat more complicated
lsa_LookupSids  function.  So the Java emitter has been modified to perform
this  allocation.  There  are  two  instances  that  are  still outstanding
however.  One  is  unions.  The  Java  stubs do not actually create a class
definition  for a union. Instead inheritence is used. The type of the union
arm  is  used  in  it's place. Functions that accept unions accept the very
generic  NdrObject  that  all Java stub class definitions extend. It is not
clear how an embedded or [out] only union will be handled. The second issue
is  custom  classes  that  require  constructor  parameters.  Consider  the
UnicodeString  class.  This  class has an alternate contructor that permits
specifying  wheather  or not the string is zero terminated. It is not clear
how  an  emdedded  or  [out]  only  rpc.unicode_string  that  is  not  zero
terminated will be handled. 

Also  an  alignment bug was fixed. A pointer to a type smaller than 4 would
have an alignment of the ndr_size of the type (e.g. short was 2 rather than
4). 

Sun Nov 14 18:59:18 EST 2004

The  name  has been changed to midlc because google claims there is already
an idl compiler named idlc.

This  release  is a vast improvement over the previous release. I have much
more  confidence  that  the  compiler  will  handle  a wide array of inputs
generically. This is evident from looking at the emitter files because they
are  relatively  simple compared to the last release. I think the code will
start to settle down from here. All of the jarapac examples run perfectly.

Wed Oct 20 22:55:30 EDT 2004
idlc-0.5.0 released

Quite  a  bit  of  work  has  gone  into  the  C  server  stub emitter. See
docs/emit_svr_stub_c.txt.  It's  so  much  nicer than the Java emitter that
I've  come  to  the conclusion that the Java emitter needs to be rewritten.
That  shouldn't  be  to bad at this point considering the emit_xxx_fragment
functions  are where all the action is and they're largely the same between
emitters.  I  expect  it  to take me a day or two. But now some of the Java
stubs don't work for reasons that are now obsolete. 

Thu Sep 23 23:20:00 EDT 2004
idlc-0.4.0 released

The  Java  emitter  has been entirely rewritten after making an observation
based on how the following IDL is actually encoded by MIDL: 

	typedef struct {
		[length_is(v + 0)] small m1[5];
		int *i;
		[length_is(v + 1)] small m2[5];
		[length_is(v + 2)] small m3[5];
		int v;
	} wacko_t;

Because  this  is successfully encoded without using additional conformance
information  (as  an  embedded  conformant  array  does  by  prefixing  the
structure  with  it's  size)  the  encoder MUST decode each member in order
before  recurring  to  decode  sub-objects (e.g. elements of the array that
contain pointers for instance) to get to v. In the previous version of IDLC
the compiler would pre-compute the size of a structure or array and set the
deferred  pointer  to  that  location  before  decoding  each  member. That
technique   was   surprisingly   successful   but  alas  not  correct.  The
getDeferred() semantic has been dissolved.

It  is  also  noteworthy  that  this  version  correctly  encodes  embedded
conformant arrays such as: 

	typedef struct {
		short a1[3];
		byte count;
		[size_is(s - 1)] TestInfo1 *a2;
		short s;
		[size_is(count)] int a3[*];  <-- embedded conformant array
	} shi_t;

This  is  encoded  as  normal  but  the  number of elements in the embedded
conformant  array  is  preceeds the structure. These don't occur frequently
but SIDs have them so might as well get it straight right from the start. 

Sat Aug 28 01:31:04 EDT 2004
idlc-0.3.6 released

I have released an updated libmba that builds cleanly with this release.

  http://www.ioplex.com/~miallen/libmba/

Fri Aug 27 03:41:48 EDT 2004
idlc-0.3.5 released

Lot's  of  little  fixes  and  changes to streamline the whole process with
Jarapac.  Also  some  things  were just wrong. I documented the test code a
little (test/README.txt). Also enum support. 

Wed Aug 25 02:03:01 EDT 2004
idlc-0.3.0 released

This is the first real release of idlc and it should be able to compile 90%
of what user's would want. Of course it doesn't support every attribute and
feature of MIDL but that will be fleshed out as demand arises. Currently it
generates  client  stubs in Java but the emit_stub_java.c file that handles
Java  specific  code  generate  is  only  500  lines and generating another
language  should  be  considerably  easier with a reference. The Java stubs
have      been      varified      to      work     with     the     Jarapac
(http://jarapac.sourceforge.net) DCE implementation. 

INSTALLATION

Midlc  must  be  compiled  from  source. It is known to compile on Linux and
Windows  although  the  Windows  implementation  currently does not run the
preprocessor   (need   to   implement   preprocw.c).   Libmba  is  required
(http://www.ioplex.com/~miallen/libmba/).  Follow  the  libmba instructions
for  the target platform, modify the first line of the Makefile to point to
the   libmba   directory,   and  run  'make'.  On  Windows  run  'nmake  -f
Makefile.msvc'. 

RUNNING

usage: ./midlc [-v|-d] [-s <symtab>] [-t c|java] [-o outfile] [-Dmacro=defn] <filename>

-v verbose   - List partial parse tree
-d debug     - List full parse tree and insert symbol comments into stub
-s <symtab>  - Specify primative data type symbol table (default is
               currently symtabjava.txt)
-t c|java    - Specify the code type generated (currently only java
               is supported)
-o <outfile> - Specify name of output file. Otherwise outfile is same
               name as input pathname but with different extension
               (e.g. .java)
-Dmacro=defn - Specify a preprocessor macro. All idl files are first
               processed by the preprocessor. Additionally setting the
               special macro 'package' will insert a 'package xyz;'
               statement at the top of the generated Java stub.
<filename>   - The pathname of the input IDL file.

Beware  that  validation  is  totally  non-existant.  The input IDL MUST be
syntactically perfect. Try running your IDL though MIDL to be certain it is
valid.

EXAMPLE

  $ ./midlc -v src/HelloWorld.idl

or

  C:\> midlc -v src\HelloWorld.idl

This  will  generate  a  class file src/HelloWorld.java that can encode and
decode  NDR  into an arbitrary byte array. Create a driver program like the
Jarapac example and run it. 
