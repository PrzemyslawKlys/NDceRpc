#ifndef SYM_H
#define SYM_H

#include <stdlib.h>
#include <mba/hashmap.h>
#include <mba/linkedlist.h>
#include <mba/allocator.h>

#define PTR_TYPE_UNIQUE 1
#define PTR_TYPE_PTR    2
#define PTR_TYPE_REF    3

#define IS_UNIQUE(p) ((p)->ptr_type == PTR_TYPE_UNIQUE)
#define IS_PTR(p) ((p)->ptr_type == PTR_TYPE_PTR)
#define IS_REF(p) ((p)->ptr_type == PTR_TYPE_REF)

struct sym {
	int id;
	unsigned long flags;
	char *interface;
	char *idl_type;
	char *out_type;
	char *ndr_type;
	size_t ndr_size;
	struct hashmap attrs;
	struct linkedlist mems;
	char *name;
	char *value;
	int ptr_type;
	int ptr;
	int align;
	int noemit;
	int do_array;
	struct sym *parent;
	struct sym *orig;
	const char *filename;
	const char *type_interface;
	int offset; /* not really used for anything */
};

struct idl;

int syminit(struct sym *sym, struct allocator *al);
struct sym *symnew(struct allocator *al);
int symdel(struct sym *sym);
int symcopy(struct sym *from, struct sym *to, struct allocator *al);
struct sym *symadd(struct idl *idl,
		const char *idl_type,
		const char *out_type,
		const char *ndr_type,
		size_t ndr_size);
int symload(struct idl *idl, const char *filename);
struct sym *symlook(struct idl *idl, const char *idl_type);
int evallook(const unsigned char *name, unsigned long *val, void *context);
int symexpand(struct idl *idl, struct sym *sym);
int symresolve(struct idl *idl, struct sym *sym);
int inherit_iface(struct idl *idl, struct sym *sym, char *name);

#endif /* SYM_H */
