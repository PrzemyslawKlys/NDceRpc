CC         = gcc
CFLAGS     = -Wall -W -g -ansi -pedantic -DMSGNO
VALGRIND   = -v --num-callers=20 --leak-check=yes --leak-resolution=high --show-reachable=yes

_ctest: rpc_s.c lsarpc_s.c
	$(CC) $(CFLAGS) -c -o ndr.o ndr.c
	$(CC) $(CFLAGS) -c -o rpc_s.o rpc_s.c
	$(CC) $(CFLAGS) -c -o lsarpc_s.o lsarpc_s.c
	$(CC) $(CFLAGS) -c -o ctestp.o ctestp.c
	$(CC) $(CFLAGS) -lmba -lencdec ndr.o rpc_s.o lsarpc_s.o ctestp.o -o ctest ctest.c

rpc_s.c: ../idl/rpc.idl
	../midlc -t c -o rpc ../idl/rpc.idl
lsarpc_s.c: ../idl/lsarpc.idl
	../midlc -t c -o lsarpc ../idl/lsarpc.idl

vg:
	valgrind $(VALGRIND) ./ctest 0x07 lsa07.bin

clean:
	$(RM) ctest *.o *_s.c lsarpc.h rpc.h
