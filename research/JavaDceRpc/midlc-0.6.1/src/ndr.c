#include <stdlib.h>
#include <stddef.h>
#include <string.h>

#include "midlc/ndr.h"

void
ndr_init(struct ndr *ndr,
			unsigned char *data,
			uint32_t data_size,
			ndr_alloc alloc,
			ndr_realloc realloc,
			ndr_free free,
			void *alloc_context)
{
	memset(ndr, 0, sizeof(*ndr));
	ndr->data = data;
	ndr->data_size = data_size;
	ndr->alloc = alloc;
	ndr->realloc = realloc;
	ndr->free = free;
	ndr->alloc_context = alloc_context;
}

int
dec_ndr_align(struct ndr *ndr, int boundry, size_t *off)
{
	uint32_t mask;
	size_t o;

	mask = boundry - 1;
	o = (*off + mask) & ~mask;

	if (o > ndr->data_size) {
		return -1;
	}

	*off = o;

	return 0;
}
int
dec_ndr_small(struct ndr *ndr, uint8_t *val, size_t *off)
{
	if ((*off + 1) > ndr->data_size) {
		return -1;
	}

	*val = ndr->data[(*off)++];

	return 0;
}
int
dec_ndr_short(struct ndr *ndr, uint16_t *val, size_t *off)
{
	unsigned char *s;

	if ((*off + 2) > ndr->data_size) {
		return -1;
	}

	s = ndr->data + *off;
	if (ndr->bigendian) {
		*val = ((uint16_t)s[0] << 8) |
				(uint16_t)s[1];
	} else {
		*val = (uint16_t)s[0] |
				((uint16_t)s[1] << 8);
	}
	*off += 2;

	return 0;
}
int
dec_ndr_long(struct ndr *ndr, uint32_t *val, size_t *off)
{
	unsigned char *s;

	if ((*off + 4) > ndr->data_size) {
		return -1;
	}

	s = ndr->data + *off;
	if (ndr->bigendian) {
		*val = ((uint32_t)s[0] << 24) |
				((uint32_t)s[1] << 16) |
				((uint32_t)s[2] << 8) |
				(uint32_t)s[3];
	} else {
		*val = (uint32_t)s[0] |
				((uint32_t)s[1] << 8) |
				((uint32_t)s[2] << 16) |
				((uint32_t)s[3] << 24);
	}
	*off += 4;

	return 0;
}
int
dec_ndr_hyper(struct ndr *ndr, uint64_t *val, size_t *off)
{
	unsigned char *s;

	if ((*off + 4) > ndr->data_size) {
		return -1;
	}

	s = ndr->data + *off;
	if (ndr->bigendian) {
		*val = ((uint64_t)s[0] << 56) |
				((uint64_t)s[1] << 48) |
				((uint64_t)s[2] << 40) |
				((uint64_t)s[3] << 32) |
				((uint64_t)s[4] << 24) |
				((uint64_t)s[5] << 16) |
				((uint64_t)s[6] << 8) |
				(uint64_t)s[7];
	} else {
		*val = (uint64_t)s[0] |
				((uint64_t)s[1] << 8) |
				((uint64_t)s[2] << 16) |
				((uint64_t)s[3] << 24) |
				((uint64_t)s[4] << 32) |
				((uint64_t)s[5] << 40) |
				((uint64_t)s[6] << 48) |
				((uint64_t)s[7] << 56);
	}
	*off += 8;

	return 0;
}
int
ndr_chksize(struct ndr *ndr, size_t size)
{
	size_t newsize;
	unsigned char *newdata;

	if (size <= ndr->data_size) {
		return 0;
	}

	if (ndr->data_size == 0) {
		newsize = NDR_INIT_SIZE;
	} else {
		newsize = ndr->data_size * 2;
	}
	if (size > newsize) {
		newsize = size;
	}

	if ((newdata = ndr->realloc(ndr->alloc_context, ndr->data, newsize)) == NULL) {
		return -1;
	}

	ndr->data = newdata;
	ndr->data_size = newsize;

	return 0;
}
int
enc_ndr_align(struct ndr *ndr, int boundry, size_t *off)
{
	uint32_t mask;
	size_t o;

	mask = boundry - 1;
	o = (*off + mask) & ~mask;

	if (ndr_chksize(ndr, o) == -1) {
		return -1;
	}

	while (*off < o) {
		ndr->data[(*off)++] = 0;
	}

	return 0;
}
int
enc_ndr_small(struct ndr *ndr, uint8_t val, size_t *off)
{
	if (ndr_chksize(ndr, *off + 1) == -1) {
		return -1;
	}

	ndr->data[(*off)++] = val;

	return 0;
}
int
enc_ndr_short(struct ndr *ndr, uint16_t val, size_t *off)
{
	unsigned char *dst;

	if (enc_ndr_align(ndr, 2, off) == -1 ||
				ndr_chksize(ndr, *off + 2) == -1) {
		return -1;
	}

	dst = ndr->data + *off;
	if (ndr->bigendian) {
		*dst++ = (val >> 8) & 0xFF;
		*dst++ = val & 0xFF;
	} else {
		*dst++ = val & 0xFF;
		*dst++ = (val >> 8) & 0xFF;
	}
	*off += 2;

	return 0;
}
int
enc_ndr_long(struct ndr *ndr, uint32_t val, size_t *off)
{
	unsigned char *dst;

	if (enc_ndr_align(ndr, 4, off) == -1 ||
				ndr_chksize(ndr, *off + 4) == -1) {
		return -1;
	}

	dst = ndr->data + *off;
	if (ndr->bigendian) {
		*dst++ = (val >> 24) & 0xFF;
		*dst++ = (val >> 16) & 0xFF;
		*dst++ = (val >> 8) & 0xFF;
		*dst++ = val & 0xFF;
	} else {
		*dst++ = val & 0xFF;
		*dst++ = (val >> 8) & 0xFF;
		*dst++ = (val >> 16) & 0xFF;
		*dst++ = (val >> 24) & 0xFF;
	}
	*off += 4;

	return 0;
}
int
enc_ndr_hyper(struct ndr *ndr, uint64_t val, size_t *off)
{
	unsigned char *dst;

	if (enc_ndr_align(ndr, 8, off) == -1 ||
				ndr_chksize(ndr, *off + 8) == -1) {
		return -1;
	}

	dst = ndr->data + *off;
	if (ndr->bigendian) {
		*dst++ = (val >> 56) & 0xFF;
		*dst++ = (val >> 48) & 0xFF;
		*dst++ = (val >> 40) & 0xFF;
		*dst++ = (val >> 32) & 0xFF;
		*dst++ = (val >> 24) & 0xFF;
		*dst++ = (val >> 16) & 0xFF;
		*dst++ = (val >> 8) & 0xFF;
		*dst++ = val & 0xFF;
	} else {
		*dst++ = val & 0xFF;
		*dst++ = (val >> 8) & 0xFF;
		*dst++ = (val >> 16) & 0xFF;
		*dst++ = (val >> 24) & 0xFF;
		*dst++ = (val >> 32) & 0xFF;
		*dst++ = (val >> 40) & 0xFF;
		*dst++ = (val >> 48) & 0xFF;
		*dst++ = (val >> 56) & 0xFF;
	}
	*off += 8;

	return 0;
}

int
enc_ndr_string(struct ndr *ndr, unsigned char *str, size_t *off)
{
	size_t hdr, len;

	if (ndr_chksize(ndr, *off + 12) == -1) {
		return -1;
	}

	hdr = *off;
	*off += 12; /* skip header momentarily */

	for (len = 0; *str; len++) {
		if (len > 0x7FFF) {
			return -1;
		}
		if (enc_ndr_short(ndr, *str++ & 0xFF, off) == -1) {
			return -1;
		}
	}
	enc_ndr_short(ndr, 0, off);
	len++;

	enc_ndr_long(ndr, len, &hdr);
	enc_ndr_long(ndr, 0, &hdr);
	enc_ndr_long(ndr, len, &hdr);

	return 0;
}
int
dec_ndr_string(struct ndr *ndr, unsigned char **str, size_t *off)
{
	uint32_t len, i;
	unsigned char *tmp;

	if ((*off + 12) > ndr->data_size) {
		return -1;
	}

	*off += 8;
	dec_ndr_long(ndr, &len, off);
	len++;
	if ((*off + len * 2) > ndr->data_size) {
		return -1;
	}

	if ((tmp = ndr->alloc(ndr->alloc_context, len, ndr->alloc_flags)) == NULL) {
		return -1;
	}

	for (i = 0; i < len; i++) {
		uint16_t ucs;
		dec_ndr_short(ndr, &ucs, off);
		if (ucs > 0xFF) {
			ucs = '?';
		}
		tmp[i] = ucs & 0xFF;
	}

	*str = tmp;

	return 0;
}
static int
ndr_get_dce_referent(struct ndr *ndr, const void *ptr, uint32_t *ref)
{
	struct dce_referent *r, *rlim;

	r = ndr->referents;
	rlim = r + ndr->rcount;

	while (r < rlim) {                     /* search for ptr */
		if (r->ptr == ptr) {
			return r->referent;
		} else if (!r->ptr) {
			break;
		}
		r++;
	}

	if (r == rlim) {                     /* reallocate array */
		size_t rcount = ndr->rcount ? ndr->rcount << 2 : 2;
		if ((r = ndr->realloc(ndr->alloc_context, ndr->referents,
					rcount * sizeof *r)) == NULL) {
			return -1;
		}
		ndr->referents = r;
		r = r + ndr->rcount;       /* position r at new area */
		memset(r, 0, (rcount - ndr->rcount) * sizeof *r);
		ndr->rcount = rcount;
	}
                                  /* set next referent value */
	*ref = r->referent = ++(ndr->rnext);
	r->ptr = ptr;

	return 0;
}
int
enc_ndr_referent(struct ndr *ndr, void *ptr, int type, size_t *off)
{
	uint32_t ref;

	if (ptr == NULL) {
		return enc_ndr_long(ndr, 0, off);
	}

	switch (type) {
		case 1: /* unique */
		case 3: /* ref */
			return enc_ndr_long(ndr, (uint32_t)ptr + ndr->salt, off);
		case 2: /* ptr */
			if (ndr_get_dce_referent(ndr, ptr, &ref) == -1) {
				return -1;
			}
			return enc_ndr_long(ndr, ref, off);
	}

	return 0;
}

