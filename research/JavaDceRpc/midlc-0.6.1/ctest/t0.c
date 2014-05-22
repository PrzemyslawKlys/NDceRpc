#include <stdlib.h>
#include <string.h>
#include <stdio.h>

#include <mba/hexdump.h>

#include "rpc.h"
#include "ndr.h"

int
enc_policy_handle(struct ndr *_ndr,
			policy_handle *_obj,
			unsigned char **_dst,
			unsigned char **_deferred,
			unsigned char *_dlim);

int
main(void)
{
	unsigned char buf[1024], *dst = buf, *dlim = buf + 1024, *deferred;
	struct ndr ndr;
	policy_handle h;

	h.type = 0xff;
	h.uuid.time_low = 1;
	h.uuid.time_mid = 2;
	h.uuid.time_hi_and_version = 3;
	h.uuid.clock_seq_hi_and_reserved = 4;
	h.uuid.clock_seq_low = 5;
	memcpy(h.uuid.node, "hellya", 6);

	ndr_create(&ndr, deferred = dst);
	if (enc_policy_handle(&ndr, &h, &dst, &deferred, dlim) == -1) {
		fprintf(stderr, "encoder failure\n");
		return EXIT_FAILURE;
	}
	ndr_destroy(&ndr);

	hexdump(stdout, buf, dst - buf, 16);

	return EXIT_SUCCESS;
}
