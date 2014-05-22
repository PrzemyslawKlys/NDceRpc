FOR  BEST  RESULTS,  PLEASE  DIRECT  ALL QUESTIONS REGARDING JARAPAC TO THE
JCIFSatSAMBAdotORG MAILING LIST 

Thu Dec 16 01:39:01 EST 2004
jarapac-0.3.8 released

More  work  went  into  the  workstation management examples. In particular
LsarLookupSids  has been implemented (thanks Ralf) and there's some winreg.
Some  adjustments  to  midlc  were necessary so you need the latest of that
too. 

Sun Nov 14 18:56:01 EST 2004
jarapac-0.3.7 released

Very  minor  adjustments  to ndr routines for compatibilty with midlc (just
renamed from idlc). MIDLC is where all the work went in this last round. 

Wed Oct 20 23:05:33 EDT 2004
jarapac-0.3.6 released

Some  minor  adjustments  to RPC transport and muti-pdu code. Also exmaples
are fleshed out further. Note you will need the latest idlc and jcifs until
for a while. This release needs idlc-0.5.0 and jcifs-1.1.1. 

Thu Sep 23 23:18:05 EDT 2004
jarapac-0.3.5 released

Some more work on example MSRPC services in the examples dir.

Fri Aug 27 03:55:24 EDT 2004
jarapac-0.3.

Fri Aug 20 14:06:46 EDT 2004
jarapac-0.3.0 released
miallen

Jarapac  basically  works  now. See example/TestNetShareEnumAll.java. Still
only  ncacn_np  transport  though. An i386 Linux binary of idlc is included
but it is not quite complete just yet. An idlc release will follow RSN. 

Sun Aug 15 01:52:01 EDT 2004
jarapac-0.2.1 released
miallen

More work just trying to get it to basically work. It's close though.

Sat Aug 14 16:52:34 EDT 2004
jarapac-0.2.0 released
miallen

The  NDR  layer  has  been  completely reimplemented. A MIDL compatible IDL
compiler has been implemented and is now used to generate classes that take
over  all NDR related marshalling. Therefore all (most) NDR related code in
Jarapac   has   been   removed   (to   src/ndr.old)   or   replaced   (with
src/jarapac/ndr).  Unfortunately  I didn't have enough forsight to maintain
exiting  semantics  regarding NdrObject (e.g. encode/decode vs write/read).
So the collision has resulted in quite a few changes.

Anyway,   Jarapac   is   actually   very   close   to   working   now.  The
examples/TestNetShareEnum.java  example  should  be able to work but I have
only   just  completed  compling  'ant  ncanc_np'  without  errors.  Change
example.properties and make it/run it like:

examples$ make
examples$ make run

Even  though  it  doesn't  work  I think I'll release it so that Eric has a
chance to look at it and perhaps get involved. 

Fri Nov 28 03:28:40 EST 2003
miallen

A  bunch  of fixes. NTLM1 signing and sealing was incorrect previously. The
endpoint  mapper  stub  is  implemented so you can make real RPC calls. The
"Mapper"  example  shows  usage;  this  just does a lookup for the endpoint
mapper  itself  (which  returns  an  empty  set,  since the endpoint mapper
doesn't register itself with itself). 

The endpoint mapper was implemented based on the IDL from the spec at:

  http://www.opengroup.org/onlinepubs/9629399/apdxo.htm

The  endpoint  mapper  module currently has some protocol tower floor types
implemented; a description of the rest are at: 

  http://www.opengroup.org/onlinepubs/9629399/apdxi.htm

REQUIREMENTS
--------------------------------------------------------------------------------
Running Jarapac requires the following:

    Java Runtime Environment (JRE 1.3.1 or higher).

The ncacn_np transport provider requires jCIFS, version 0.7.15 or higher:

    http://jcifs.samba.org

The NTLM session security provider requires jCIFS, as well as the following:

    Java Cryptography Extensions (JCE).
    A JCE provider for the ARC4 cipher.

The BouncyCastle clean room JCE implementation satisfies both of these
requirements, and is a recommended solution:

    http://www.bouncycastle.org


Building Jarapac requires Jakarta Ant, version 1.3 or higher, in addition to
the above requirements.
