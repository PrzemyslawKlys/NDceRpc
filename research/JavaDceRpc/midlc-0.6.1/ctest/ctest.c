#include <stdlib.h>
#include <errno.h>
#include <string.h>
#include <stdio.h>

#include <mba/msgno.h>
#include <mba/hexdump.h>
#include <mba/allocator.h>

#include "rpc.h"
#include "ndr.h"

#define BSIZ 0x7FFF

int
lsarpc_LsarClose(void *context,
			unsigned char *src,
			size_t sn,
			unsigned char **dst,
			size_t *dn);
int
lsarpc_LsarQueryInformationPolicy(void *context,
			unsigned char *src,
			size_t sn,
			unsigned char **dst,
			size_t *dn);
int
lsarpc_LsarLookupSids(void *context,
			unsigned char *src,
			size_t sn,
			unsigned char **dst,
			size_t *dn);
int
lsarpc_LsarOpenPolicy(void *context,
			unsigned char *src,
			size_t sn,
			unsigned char **dst,
			size_t *dn);


int
run(int opcode, const char *path)
{
	unsigned char src[BSIZ];
	unsigned char *dst;
	size_t dn;
	FILE *input;
	int ret;

	memset(src, 'x', BSIZ);

	if ((input = fopen(path, "r")) == NULL) {
		PMNF(errno, ": %s", path);
		return -1;
	}
	if ((ret = fread(src, 1, BSIZ, input)) < 1) {
		PMSG("Failed to read input");
		return -1;
	}
	fclose(input);

	printf("INPUT:\n");
	hexdump(stdout, src, ret, 16);

	switch (opcode) {
		case 0x0f:
			ret = lsarpc_LsarLookupSids(NULL, src, BSIZ, &dst, &dn);
			break;
		case 0x2c:
			ret = lsarpc_LsarOpenPolicy(NULL, src, BSIZ, &dst, &dn);
			break;
		case 0x07:
			ret = lsarpc_LsarQueryInformationPolicy(NULL, src, BSIZ, &dst, &dn);
			break;
		default:
			PMSG("Opcode 0x%02x not supported.", opcode);
			return -1;
	}

	if (ret == -1) {
		AMSG("");
		return -1;
	}
	printf("OUTPUT: %d\n", dn);
	hexdump(stdout, dst, dn, 16);

	allocator_free(NULL, dst);

	return 0;
}

int
main(int argc, char *argv[])
{
	int opcode;

	if (argc < 3) {
		fprintf(stderr, "usage: %s <opcode> <rawndr>\n", argv[0]);
		return EXIT_FAILURE;
	}

	opcode = strtol(argv[1], NULL, 0);

	if (run(opcode, argv[2]) == -1) {
		MSG("");
		return EXIT_FAILURE;
	}

	return EXIT_SUCCESS;
}
