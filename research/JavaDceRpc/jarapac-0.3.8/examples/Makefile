CLASSPATH=../jarapac.jar:../ncacn_np.jar:../ntlm-security.jar:../lib/jcifs-1.1.2.jar:idl:.
PROPERTIES=../../miallen.prp
#SERVER=miallen
SERVER=rnyxwbf8s5v31
#SERVER=nyrsch0506
#DC=146.125.177.156
#DC=172.26.136.232
DC=miallen
#DC=rnyxwbf8s5v31

IDLCLASS=idl/rpc.class idl/winreg.class idl/lsarpc.class idl/samr.class idl/srvsvc.class idl/atsvc.class idl/svcctl.class
IDLJAVA=idl/rpc.java idl/winreg.java idl/lsarpc.java idl/samr.java idl/srvsvc.java idl/atsvc.java idl/svcctl.java

.SUFFIXES: .idl .java
.SUFFIXES: .java .class
.SECONDARY: $(IDLJAVA)

runlsarpc: TestLsarpc.class SID.class UnicodeString.class
	java -classpath $(CLASSPATH) -Djcifs.properties=$(PROPERTIES) TestLsarpc $(DC)
runwinreg: TestWinreg.class
	java -classpath $(CLASSPATH) -Djcifs.properties=$(PROPERTIES) TestWinreg $(SERVER)
runsrvsvc: TestSrvsvc.class
	java -classpath $(CLASSPATH) -Djcifs.properties=$(PROPERTIES) TestSrvsvc $(SERVER)
runsamr: TestSamr.class
	java -classpath $(CLASSPATH) -Djcifs.properties=$(PROPERTIES) TestSamr $(DC)
runsvcctl: TestSvcctl.class
	java -classpath $(CLASSPATH) -Djcifs.properties=$(PROPERTIES) TestSvcctl $(SERVER)
runatsvc: TestAtsvc.class
	java -classpath $(CLASSPATH) -Djcifs.properties=$(PROPERTIES) TestAtsvc $(SERVER)

TestWinreg.class: $(IDLCLASS)
TestSvcctl.class: $(IDLCLASS)
TestAtsvc.class: $(IDLCLASS)
TestSrvsvc.class: $(IDLCLASS)
TestLsarpc.class: $(IDLCLASS)
TestSamr.class: $(IDLCLASS)

.idl.java:
	midlc $<
.java.class:
	javac -g -classpath $(CLASSPATH) $<

clean:
	$(RM) *.class idl/*.class $(IDLJAVA)
