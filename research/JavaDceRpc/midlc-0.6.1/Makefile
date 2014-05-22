prefix     = /usr/local
bindir     = $(prefix)/bin
datadir    = $(prefix)/share
libmba     = libmba-0.9.1
DISTRO     = midlc-0.6.1
CC         = gcc
CFLAGS     = -Wall -W -g -I $(libmba)/src -O2 -DMSGNO -D_XOPEN_SOURCE=500 -D_DATADIR=\"$(datadir)/midlc\"
OBJS       = src/emit_stub_samba.o src/emit_hdr_c.o src/emit_svr_stub_c.o src/emit_svr_dec_c.o src/emit_svr_enc_c.o src/emit_enc_java.o src/emit_dec_java.o src/emit_stub_java.o src/preproc.o src/parse.o src/sym.o src/tok.o

midlc: $(OBJS) src/midlc.c src/midlc.h src/sym.h src/tok.h
	make -C $(libmba) ar
	$(CC) $(CFLAGS) $(OBJS) -o midlc src/midlc.c $(libmba)/libmba.a

.c.o:
	$(CC) $(CFLAGS) -c -o $*.o $<

clean:
	$(RM) midlc $(OBJS) src/ndr.o
	make -C $(libmba) clean

install: midlc
	install -d $(bindir)
	install -d $(datadir)/midlc
	install -m 755 midlc $(bindir)
	install -m 644 symtabjcifs.txt $(datadir)/midlc
	install -m 644 symtabjava.txt $(datadir)/midlc
	install -m 644 symtabsamba.txt $(datadir)/midlc

uninstall:
	$(RM) $(bindir)/midlc
	$(RM) -rf $(datadir)/midlc

zip:
	rm -rf /tmp/$(DISTRO) /tmp/$(DISTRO).zip
	cp -a . /tmp/$(DISTRO)
	cd /tmp/$(DISTRO) && crlf * docs/* src/* test/* test/ndr/*
	cd /tmp && zip -r $(DISTRO).zip $(DISTRO) -x "*/.*"

crlf:
	crlf -u * docs/* src/* test/* test/ndr/*

samba = ../../samba4

echo:
	$(CC) -g -Wall -W -I$(samba)/source -I$(samba)/source/include -Isrc -c -o ndr_echo.o ndr_echo.c

