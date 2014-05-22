#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <ctype.h>
#include <mba/msgno.h>
#include "sym.h"
#include "midlc.h"

const char *
convexpr(const char *expr, const char *prefix)
{
	static char buf[1024];
	char *blim = buf + 1024, *bp = buf, *end = buf;
	const char *cp = NULL;
	int state = 0;

	if (*expr == '\0') {
		return expr;
	}

	while (*prefix && bp < blim) {
					/* copy last label of prefix */
		if (*prefix == '.') {
			end = bp + 1;
		}
		*bp++ = *prefix++;
	}
	bp = end;
	*bp++ = '\0';
	prefix = buf;

	while (bp < blim) {
		if (isalpha(*expr) || *expr == '_') {
			if (state == 0) {
				bp += sprintf(bp, "obj->%s", prefix);
				cp = bp;
			}
			state = 1;
		} else if (isdigit(*expr)) {
			state = 2;
		} else {
			if (*expr == '\0') {
				break;
			}
			state = 0;
		}
		*bp++ = *expr++;
	}
	if (bp == blim) return "ERROR";
	*bp = '\0';

	return end + 1;
}
int
emit_operation(struct idl *idl,
		struct sym *sym,
		int indent)
{
	iter_t iter;
	struct sym *mem;
	int wrap = linkedlist_size(&sym->mems) > 2;
	const char *ws = "\n\t\t\t";

	if (emit_params_decoder(idl, sym, 1, indent) == -1 ||
				emit_params_encoder(idl, sym, 0, indent) == -1) {
		AMSG("");
		return -1;
	}

	print(idl, indent, "int\n%s_%s(void *context,%sunsigned char *src,%ssize_t sn,%sunsigned char **dst,%ssize_t *dn)\n{\n", sym->interface, sym->name, ws, ws, ws, ws);

	print(idl, indent + 4, "struct ndr ndr;\n");
	print(idl, indent + 4, "struct params_%s params;\n", sym->name);
	fprintf(idl->out, "\n");
	print(idl, indent + 4, "memset(&ndr, 0, sizeof(ndr));\n");
	print(idl, indent + 4, "ndr.data = src;\n");
	print(idl, indent + 4, "ndr.data_size = sn;\n");
	print(idl, indent + 4, "ndr.alloc = allocator_alloc;\n");
	print(idl, indent + 4, "ndr.realloc = allocator_realloc;\n");
	print(idl, indent + 4, "ndr.free = allocator_free;\n");
	print(idl, indent + 4, "if (dec_params_%s(&ndr, &params, &ndr.deferred) == -1) {\n", sym->name);
	print(idl, indent + 8, "return -1;\n");
	print(idl, indent + 4, "}\n");
	fprintf(idl->out, "\n");

	if (strcmp(sym->idl_type, "void") == 0) {
		print(idl, indent + 4, "%s(context", sym->name);
	} else {
		print(idl, indent + 4, "params.retval = %s(context", sym->name);
	}

	linkedlist_iterate(&sym->mems, &iter);
	while ((mem = linkedlist_next(&sym->mems, &iter))) {
		if (wrap) {
			fprintf(idl->out, ",\n");
			sp(idl->out, indent + 12);
		} else {
			fprintf(idl->out, ", ");
		}
		fprintf(idl->out, "params.%s", mem->name);
	}
	fprintf(idl->out, ");\n");

	fprintf(idl->out, "\n");
	print(idl, indent + 4, "memset(&ndr, 0, sizeof(ndr));\n");
	print(idl, indent + 4, "ndr.alloc = allocator_alloc;\n");
	print(idl, indent + 4, "ndr.realloc = allocator_realloc;\n");
	print(idl, indent + 4, "ndr.free = allocator_free;\n");
	print(idl, indent + 4, "if (enc_params_%s(&ndr, &params, &ndr.deferred) == -1) {\n", sym->name);
	print(idl, indent + 8, "return -1;\n");
	print(idl, indent + 4, "}\n");
	print(idl, indent + 4, "*dst = ndr.data;\n");
	print(idl, indent + 4, "*dn = ndr.deferred;\n");
	fprintf(idl->out, "\n");

	print(idl, indent + 4, "return 0;\n");
	fprintf(idl->out, "}\n");

	return 0;
}
int
collect_imported_protos(struct hashmap *map, struct sym *sym)
{
	iter_t iter;
	struct sym *mem;

	if (IS_IMPORTED(sym)) {
		hashmap_put(map, sym->idl_type, sym);
	}

	linkedlist_iterate(&sym->mems, &iter);
	while ((mem = linkedlist_next(&sym->mems, &iter))) {
		if (collect_imported_protos(map, mem) == -1) {
			return -1;
		}
	}

	return 0;
}
int
emit_imported_protos(struct idl *idl, struct sym *sym)
{
	iter_t iter;
	char *key;

	hashmap_clear(idl->tmp, NULL, NULL, NULL);
	if (collect_imported_protos(idl->tmp, sym) == -1) {
		AMSG("");
		return -1;
	}

	hashmap_iterate(idl->tmp, &iter);
	while ((key = hashmap_next(idl->tmp, &iter))) {
		struct sym *s = hashmap_get(idl->tmp, key);

		if (emit_marsh_proto(idl, s, 0, 0) == -1) {
			return -1;
		}
		fputs(";\n", idl->out);
		if (emit_marsh_proto(idl, s, 1, 0) == -1) {
			return -1;
		}
		fputs(";\n", idl->out);
	}

	fputs("\n", idl->out);

	return 0;
}
int
emit_svr_stub_c(struct idl *idl, struct sym *iface)
{
	iter_t iter;
	struct sym *sym;

	idl->opnum = 0;

	print(idl, 0, "#include <stdlib.h>\n");
	print(idl, 0, "#include <string.h>\n\n");
	print(idl, 0, "#include <mba/allocator.h>\n\n");
	print(idl, 0, "#include \"ndr.h\"\n");
	print(idl, 0, "#include \"%s.h\"\n\n", iface->name);

	if (emit_imported_protos(idl, iface) == -1) {
		AMSG("");
		return -1;
	}

	linkedlist_iterate(&iface->mems, &iter);
	while ((sym = linkedlist_next(&iface->mems, &iter))) {
		if (sym->noemit) {
			continue;
		}
		if (IS_OPERATION(sym)) {
			emit_struct(idl, sym, 0);
			fprintf(idl->out, "\n");
		}
	}

	linkedlist_iterate(&iface->mems, &iter);
	while ((sym = linkedlist_next(&iface->mems, &iter))) {
		if (sym->noemit) {
			continue;
		}

		if (IS_OPERATION(sym)) {
			if (emit_operation(idl, sym, 0) == -1) {
				AMSG("");
				return -1;
			}
		} else if (!IS_PRIMATIVE(sym) && !IS_UNION(sym)) {
			if (emit_decoder(idl, sym, 0) == -1 ||
					emit_encoder(idl, sym, 0) == -1) {
				AMSG("");
				return -1;
			}
		}
	}

	return 0;
}

