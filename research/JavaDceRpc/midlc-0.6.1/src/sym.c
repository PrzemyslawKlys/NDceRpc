#include <stdlib.h>
#include <string.h>
#include <stdio.h>
#include <errno.h>
#include <limits.h>
#include <mba/text.h>
#include <mba/csv.h>
#include <mba/msgno.h>
#include "midlc.h"
#include "sym.h"

int
syminit(struct sym *sym, struct allocator *al)
{
	memset(sym, 0, sizeof *sym);
	if (hashmap_init(&sym->attrs, 0, hash_str, cmp_str, NULL, al) == -1 ||
				linkedlist_init(&sym->mems, 0, al) == -1) {
		AMSG("");
		return -1;
	}

	return 0;
}
int
symdel(struct sym *sym)
{
	sym = NULL;
	return 0;
}
struct sym *
symnew(struct allocator *al)
{
	struct sym *sym;

	if ((sym = allocator_alloc(al, sizeof *sym, 1)) == NULL) {
		AMSG("");
		return NULL;
	}
	if (syminit(sym, al) == -1) {
		AMSG("");
		return NULL;
	}

	return sym;
}
int
symcopy(struct sym *from, struct sym *to, struct allocator *al)
{
	iter_t iter;
	void *key, *data;

	to->flags = from->flags & 0xFFFF;
	to->idl_type = dupstr(from->idl_type, al);
	to->ndr_type = dupstr(from->ndr_type, al);
	to->interface = from->interface;

	hashmap_iterate(&from->attrs, &iter);
	while ((key = hashmap_next(&from->attrs, &iter))) {
		if ((data = hashmap_get(&from->attrs, key)) == NULL ||
				hashmap_put(&to->attrs, key, data) == -1) {
			AMSG("");
			return -1;
		}
	}
	linkedlist_iterate(&from->mems, &iter);
	while ((data = linkedlist_next(&from->mems, &iter))) {
		if (linkedlist_add(&to->mems, data) == -1) {
			AMSG("");
			return -1;
		}
	}

	to->name = dupstr(from->name, al);
	to->value = dupstr(from->value, al);
	to->ptr = from->ptr;
	to->align = from->align;

	return 0;
}
struct sym *
symadd(struct idl *idl,
		const char *idl_type,
		const char *out_type,
		const char *ndr_type,
		size_t ndr_size)
{
	struct sym *sym;

	if (idl == NULL || idl_type == NULL || *idl_type == '\0') {
		PMNO(EINVAL);
		return NULL;
	}

	if ((sym = symnew(idl->al)) == NULL) {
		AMSG("");
		return NULL;
	}

	sym->idl_type = dupstr(idl_type, idl->al);

	if (out_type) {
		sym->out_type = dupstr(out_type, idl->al);
	}
	if (ndr_type) {
		sym->ndr_type = dupstr(ndr_type, idl->al);
	}
	sym->ndr_size = ndr_size;

	if (hashmap_put(idl->syms, (void *)sym->idl_type, sym) == -1) {
		AMSG("");
		return NULL;
	}

	return sym;
}
int
symload(struct idl *idl, const char *filename)
{
	FILE *in;
	int n;
	unsigned char buf[512], *row[4];

	if ((in = fopen(filename, "r")) == NULL) {
		PMNF(errno, ": %s", filename);
		return -1;
	}

	while ((n = csv_row_fread(in, buf, 512, row, 4, '\t', CSV_TRIM | CSV_QUOTES)) > 0) {
		struct sym *sym;

		if (*row[0] == '\0' || *row[0] == '#') {
			continue;
		}
		if ((sym = symadd(idl, row[0], row[1], row[2], atoi(row[3]))) == NULL) {
			AMSG("");
			return -1;
		}
		sym->flags |= FLAGS_PRIMATIVE;
	}

	fclose(in);
	if (n == -1) {
		AMSG("");
		return -1;
	}

	return 0;
}
struct sym *
symlook(struct idl *idl, const char *idl_type)
{
	struct sym *sym;

	if ((sym = hashmap_get(idl->syms, idl_type)) == NULL) {
		AMSG("");
	}

	return sym;
}
int
evallook(const unsigned char *name, unsigned long *val, void *context)
{
	struct idl *idl = context;
	struct sym *sym;

	if ((sym = hashmap_get(idl->consts, name)) == NULL) {
		return -1;
	}
	if ((*val = strtoul(sym->value, NULL, 0)) == ULONG_MAX) {
		return -1;
	}

	return 0;
}
int
symexpand(struct idl *idl, struct sym *sym)
{
	char *key;
	struct sym *mem;
	iter_t iter;

	if (IS_EXPANDED(sym)) {
		return 0;
	}
	sym->flags |= FLAGS_EXPANDED;
	sym->orig = sym;

	if (IS_INTERFACE(sym)) {
		const char *pd = hashmap_get(&sym->attrs, "pointer_default");
		idl->ptr_default = PTR_TYPE_UNIQUE;
		if (pd) {
			if (strcmp(pd, "ptr") == 0) {
				idl->ptr_default = PTR_TYPE_PTR;
			} else if (strcmp(pd, "ref") == 0) {
				idl->ptr_default = PTR_TYPE_REF;
			}
		}
	} else if (IS_ENUM(sym)) {
		char buf[16];
		int val = 0;

		linkedlist_iterate(&sym->mems, &iter);
		while ((mem = linkedlist_next(&sym->mems, &iter))) {
			mem->flags = FLAGS_CONST | FLAGS_PRIMATIVE;
			if (mem->value) {
				val = strtoul(mem->value, NULL, 0);
			}
			sprintf(buf, "%d", val++);
			mem->value = dupstr(buf, idl->al);
		}
	} else if (sym->ptr) {
		if (hashmap_get(&sym->attrs, "unique")) {
			sym->ptr_type = PTR_TYPE_UNIQUE;
		} else if (hashmap_get(&sym->attrs, "ptr")) {
			sym->ptr_type = PTR_TYPE_PTR;
		} else if (hashmap_get(&sym->attrs, "ref")) {
			sym->ptr_type = PTR_TYPE_REF;
		} else if (IS_PARAMETER(sym) || IS_OPERATION(sym)) {
			sym->ptr_type = PTR_TYPE_REF;
		} else {
			sym->ptr_type = idl->ptr_default;
		}
	}

	/* If the symbol is typedef'd add it to the table using the
	 * typedef'd name too
	 */
	if (IS_TYPEDEFD(sym) && sym->name) {
		if (IS_ENUM(sym) && (mem = hashmap_get(idl->syms, sym->idl_type))) {
			mem->noemit = 1; /* supress redundant enum */
		}
		key = sym->name;
	} else {
		key = sym->idl_type;
	}
	if (hashmap_get(idl->syms, key) == NULL) {
		if (hashmap_put(idl->syms, key, sym) == -1) {
			AMSG("");
		}
	}

	/* If the symbol has members it is already expanded
	 */
	if (linkedlist_size(&sym->mems) == 0) {
		struct sym *s = symlook(idl, sym->idl_type);
		if (s) {
			sym->interface = s->interface;
			linkedlist_iterate(&s->mems, &iter);
			while ((mem = linkedlist_next(&s->mems, &iter))) {
				struct sym *cpy = symnew(idl->al);
				symcopy(mem, cpy, idl->al);
				linkedlist_add(&sym->mems, cpy);
			}
		} else {
			AMSG("");
			return -1;
		}
	}

	sym->id = idl->symid++;

	/* Perform expansion recursively on all symbols
	 */
	linkedlist_iterate(&sym->mems, &iter);
	while ((mem = linkedlist_next(&sym->mems, &iter))) {
		symexpand(idl, mem);
		mem->parent = sym;
	}

	return 0;
}
int
symresolve(struct idl *idl, struct sym *sym)
{
	struct sym *s;
	char *key;
	iter_t iter;
	int align;
	size_t size;

 	s = symlook(idl, sym->idl_type);
	if (s) {

                                                      /* merge stuff */
		sym->flags |= s->flags & 0xFFFF;

		sym->ndr_type = s->ndr_type;
		if (sym->ptr && (!IS_PRIMATIVE(sym) || hashmap_get(&sym->attrs, "string"))) {
			sym->ndr_size = 4;
		} else {
			sym->ndr_size = s->ndr_size;
		}

		sym->align = sym->ptr ? 4 : sym->ndr_size;

                                               /* inherit attributes */
		hashmap_iterate(&s->attrs, &iter);
		while ((key = hashmap_next(&s->attrs, &iter))) {
			/* Only add attributes that the symbol does not have already
			 */
			if (hashmap_get(&sym->attrs, key) == NULL) {
				char *data;
				data = hashmap_get(&s->attrs, key);
				hashmap_put(&sym->attrs, key, data);
			}
		}

		if (sym->out_type == NULL) {
			if (s->out_type) {
				sym->out_type = s->out_type;
			} else if (IS_TYPEDEFD(sym)) {
				sym->out_type = sym->idl_type = sym->name;
			} else if (strncmp(sym->idl_type, "struct ", 7) == 0 ||
						strncmp(sym->idl_type, "union ", 6) == 0) {
					char buf[255];
					if (*sym->idl_type == 's') {
						sprintf(buf, "struct_%s", sym->idl_type + 7);
					} else {
						sprintf(buf, "union_%s", sym->idl_type + 7);
					}
					sym->out_type = dupstr(buf, idl->al);
			}
			if ((strcmp(idl->type, "java") == 0 || strcmp(idl->type, "jcifs") == 0) && IS_IMPORTED(sym)) {
				char buf[255];
				sprintf(buf, "%s.%s", sym->interface, sym->out_type);
				sym->out_type = dupstr(buf, idl->al);
			}
		}
	}

	align = 0;
	size = 0;

	linkedlist_iterate(&sym->mems, &iter);
	while ((s = linkedlist_next(&sym->mems, &iter))) {
		size_t msiz, mali, m;

		symresolve(idl, s);

		if (s->align > align) {
			align = s->align;
		}

		if (s->ptr) {
			msiz = mali = 4;
		} else {
			msiz = s->ndr_size;
			mali = s->align;
		}
		m = mali - 1;
		size = (size + m) & ~m;                                 /* align the type */
		s->offset = size;

		if (IS_ENUM(sym)) {
			size = msiz;   /* size only size of member not sum of mems */
		} else if (IS_UNION(sym)) {
                /* size is largest of mems (not really used for any logic so far) */
			if (msiz > size) {
				size = msiz;
			}
		} else {
			size += msiz;                                     /* and size of type */
		}
	}

	if (align) {
		sym->align = align;
	}
	if (size) {
		sym->ndr_size = size;
	}

                                                       /* check array conformance */
	hashmap_iterate(&sym->attrs, &iter);
	while ((key = hashmap_next(&sym->attrs, &iter))) {
		if (!IS_FIXED(sym) && (strcmp(key, "size_is") == 0 || strcmp(key, "max_is") == 0)) {
			sym->flags |= FLAGS_CONFORMANT;
			break;
		}
	}

	if (IS_PRIMATIVE(sym)) {
		if (!IS_OPERATION(sym)) {
			sym->interface = NULL;
		}
		sym->flags &= ~FLAGS_IMPORTED;
	}

	return 0;
}
int
inherit_iface(struct idl *idl, struct sym *sym, char *name)
{
	iter_t iter;
	struct sym *mem;

	if (!sym->interface) {
		sym->interface = name;
	}
	if (strcmp(idl->interface, sym->interface) != 0) {
		sym->flags |= FLAGS_IMPORTED;
	}

	linkedlist_iterate(&sym->mems, &iter);
	while ((mem = linkedlist_next(&sym->mems, &iter))) {
		if (inherit_iface(idl, mem, name) == -1) {
			AMSG("");
			return -1;
		}
	}

	return 0;
}
