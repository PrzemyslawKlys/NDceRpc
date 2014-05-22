#ifndef NDR_H
#define NDR_H

#include <stdlib.h>
#include <stdint.h>

#define NDR_INIT_SIZE 4

typedef void *(*ndr_alloc)(void *context, size_t size, int flags);
typedef void *(*ndr_realloc)(void *context, void *obj, size_t size);
typedef int (*ndr_free)(void *context, void *obj);

typedef unsigned char char_t;

struct dce_referent {
	int referent;
	const void *ptr;
};

struct ndr {
	unsigned char *data;
	uint32_t data_size;
	uint32_t deferred;
	ndr_alloc alloc;
	ndr_realloc realloc;
	ndr_free free;
	void *alloc_context;
	int alloc_flags;
	int bigendian;
	struct dce_referent *referents;
	size_t rcount;
	uint32_t rnext;
	uint32_t salt;
};

void ndr_init(struct ndr *ndr,
			unsigned char *data,
			uint32_t data_size,
			ndr_alloc alloc,
			ndr_realloc realloc,
			ndr_free free,
			void *alloc_context);

int enc_ndr_align(struct ndr *ndr, int boundry, size_t *off);
int enc_ndr_small(struct ndr *ndr, uint8_t val, size_t *off);
int enc_ndr_short(struct ndr *ndr, uint16_t val, size_t *off);
int enc_ndr_long(struct ndr *ndr, uint32_t val, size_t *off);
int enc_ndr_hyper(struct ndr *ndr, uint64_t val, size_t *off);
int enc_ndr_string(struct ndr *ndr, unsigned char *str, size_t *off);

int enc_ndr_referent(struct ndr *ndr, void *ptr, int type, size_t *off);

int dec_ndr_align(struct ndr *ndr, int boundry, size_t *off);
int dec_ndr_small(struct ndr *ndr, uint8_t *val, size_t *off);
int dec_ndr_short(struct ndr *ndr, uint16_t *val, size_t *off);
int dec_ndr_long(struct ndr *ndr, uint32_t *val, size_t *off);
int dec_ndr_hyper(struct ndr *ndr, uint64_t *val, size_t *off);
int dec_ndr_string(struct ndr *ndr, unsigned char **str, size_t *off);

#endif /* NDR_H */
