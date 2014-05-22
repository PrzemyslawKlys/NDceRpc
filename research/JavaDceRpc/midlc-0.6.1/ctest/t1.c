#include <stdlib.h>
#include <string.h>
#include <stdio.h>

#include <mba/hexdump.h>

#include "rpc.h"
#include "ndr.h"

int
enc_unicode_string(struct ndr *_ndr,
			unicode_string *_obj,
			unsigned char **_dst,
			unsigned char **_deferred,
			unsigned char *_dlim);

int
main(void)
{
	unsigned char buf[1024], *dst = buf, *dlim = buf + 1024;
	struct ndr ndr;
	uint16_t buffer[] = { 'a', 'b', 'c' };
	unicode_string u;
	int n;

	u.length = 6;
	u.maximum_length = 6;
	u.buffer = buffer;

	ndr_create(&ndr, dst);
	ndr.bigendian = 1;
	if ((n = enc_unicode_string(&ndr, &u, &dst, &dst, dlim)) == -1) {
		fprintf(stderr, "encoder failure\n");
		return EXIT_FAILURE;
	}
	ndr_destroy(&ndr);

	hexdump(stdout, buf, n, 16);

	return EXIT_SUCCESS;
}
