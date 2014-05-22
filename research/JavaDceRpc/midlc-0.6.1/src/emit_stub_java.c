#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <ctype.h>
#include <mba/msgno.h>
#include "sym.h"
#include "midlc.h"

int emit_encoder_frag(struct idl *idl, struct sym *sym, int mode, const char *base, int indent);
int emit_enc(struct idl *idl, struct sym *sym, int indent);
int emit_params_enc(struct idl *idl, struct sym *sym, int indent);
int emit_decoder_frag(struct idl *idl, struct sym *sym, int mode, const char *base, int indent);
int emit_dec(struct idl *idl, struct sym *sym, int indent);
int emit_params_dec(struct idl *idl, struct sym *sym, int indent);

int
emit_mem(struct idl *idl,
		struct sym *sym,
		int indent)
{
	const char *out_type;
	char *is_string = hashmap_get(&sym->attrs, "string");

	if (is_string) {
		out_type = "String";
	} else if (IS_UNION(sym)) {
		out_type = "NdrObject";
	} else {
		out_type = sym->out_type;
	}

	if (IS_ARRAY(sym)) {
		print(idl, indent, "%s[] %s", out_type, sym->name);
	} else {
		print(idl, indent, "%s %s", out_type, sym->name);
	}

	return 0;
}
int
emit_class_def(struct idl *idl,
		struct sym *sym,
		int indent)
{
	iter_t iter;
	struct sym *mem;

	if (IS_UNION(sym)) {
		return 0;
	}

	print(idl, indent, "public static class %s extends NdrObject {\n\n", sym->out_type);

	linkedlist_iterate(&sym->mems, &iter);
	while ((mem = linkedlist_next(&sym->mems, &iter))) {
		print(idl, indent + 4, "public ");
		emit_mem(idl, mem, 0);
		fputs(";\n", idl->out);
	}

	fputc('\n', idl->out);

	if (emit_enc(idl, sym, indent + 4) == -1 ||
				emit_dec(idl, sym, indent + 4) == -1) {
		AMSG("");
		return -1;
	}

	print(idl, indent, "}\n");

	return 0;
}
int
emit_oper(struct idl *idl,
		struct sym *sym,
		int indent)
{
	iter_t iter;
	struct sym *mem;
	int wrap = linkedlist_size(&sym->mems) > 3;
	const char *op = hashmap_get(&sym->attrs, "op");

	if (strcmp(idl->type, "jcifs") == 0) {
		print(idl, indent, "public static class %s extends DcerpcMessage {\n\n", sym->name);
	} else {
		print(idl, indent, "public static class %s extends NdrObject {\n\n", sym->name);
	}

	if (op) {
		print(idl, indent + 4, "public int getOpnum() { return %s; }\n\n", op);
		idl->opnum = strtoul(op, NULL, 0);
	} else {
		print(idl, indent + 4, "public int getOpnum() { return %d; }\n\n", idl->opnum);
	}
	idl->opnum++;

                                                          /* emit members */
	if (strcmp(sym->idl_type, "void") != 0) {
		struct sym sym0 = *sym;
		sym0.name = "retval";
		print(idl, indent + 4, "public ");
		emit_mem(idl, &sym0, 0);
		fputs(";\n", idl->out);
	}
	linkedlist_iterate(&sym->mems, &iter);
	while ((mem = linkedlist_next(&sym->mems, &iter))) {
		print(idl, indent + 4, "public ");
		emit_mem(idl, mem, 0);
		fputs(";\n", idl->out);
	}

	fputc('\n', idl->out);

                                                      /* emit constructor */
	print(idl, indent + 4, "public %s(", sym->name);

	linkedlist_iterate(&sym->mems, &iter);
	while ((mem = linkedlist_next(&sym->mems, &iter))) {
		emit_mem(idl, mem, 0);
		if (mem != linkedlist_get_last(&sym->mems)) {
			if (wrap) {
				fprintf(idl->out, ",\n");
				sp(idl->out, indent + 16);
			} else {
				fprintf(idl->out, ", ");
			}
		}
	}
	fprintf(idl->out, ") {\n");
	linkedlist_iterate(&sym->mems, &iter);
	while ((mem = linkedlist_next(&sym->mems, &iter))) {
		print(idl, 12, "this.%s = %s;\n", mem->name, mem->name);
	}
	sp(idl->out, indent + 4);
	fprintf(idl->out, "}\n\n");

	if (emit_params_enc(idl, sym, indent + 4) == -1 ||
				emit_params_dec(idl, sym, indent + 4) == -1) {
		AMSG("");
		return -1;
	}

	print(idl, indent, "}\n");

	return 0;
}
static char *
mkptrtype(const char *type, struct allocator *al)
{
	char buf[512], *bp = buf;

	bp += sprintf(bp, "Ndr");
	*bp++ = toupper(*type++);
	do {
		*bp++ = *type;
	} while (*type++);

	return dupstr(buf, al);
}
int
convert_primative_pointers(struct idl *idl, struct sym *sym)
{
	iter_t iter;
	struct sym *mem;

	if (IS_PRIMATIVE(sym) &&
				sym->ptr &&
				hashmap_get(&sym->attrs, "string") == NULL &&
				hashmap_get(&sym->attrs, "size_is") == NULL &&
				hashmap_get(&sym->attrs, "max_is") == NULL) {
		if (IS_PARAMETER(sym) && IS_REF(sym)) {
			sym->ptr--;
			sym->ptr_type = 0;
		} else {
			sym->out_type = mkptrtype(sym->ndr_type, idl->al);
		}
	}

	linkedlist_iterate(&sym->mems, &iter);
	while ((mem = linkedlist_next(&sym->mems, &iter))) {
		if (mem->noemit) {
			continue;
		}
		if (convert_primative_pointers(idl, mem) == -1) {
			return -1;
		}
	}

	return 0;
}
char *
trimquotes(char *src, char *dst)
{
	char *dp = dst;
	int quote = 0;

	if (!src) return NULL;

	while (*src) {
		if (*src == '"') {
			if (quote) {
				break;
			}
			quote = 1;
			src++;
			continue;
		}
		*dp++ = *src++;
	}
	*dp = '\0';

	return dst;
}
int
emit_stub_java(struct idl *idl, struct sym *iface)
{
	iter_t iter;
	struct sym *sym;
	char uuid[512];
	const char *package, *version;

	if ((package = hashmap_get(idl->macros, "package"))) {
		fprintf(idl->out, "package %s;\n\n", package);
	}
	if (strcmp(idl->type, "jcifs") == 0) {
		if (package == NULL)
			fprintf(idl->out, "package jcifs.dcerpc.msrpc;\n\n");
		fprintf(idl->out, "import jcifs.dcerpc.*;\n");
		fprintf(idl->out, "import jcifs.dcerpc.ndr.*;\n\n");
	} else {
		if (hashmap_get(idl->macros, "_TEST") == NULL) {
			fprintf(idl->out, "import rpc.*;\n");
		}
		fprintf(idl->out, "import ndr.*;\n\n");
	}

	trimquotes(hashmap_get(&iface->attrs, "uuid"), uuid);
	version = hashmap_get(&iface->attrs, "version");

	if (uuid && version) {
		if (hashmap_get(idl->macros, "_TEST")) {
			fprintf(idl->out, "public class %s {\n\n", iface->name);
		} else if (strcmp(idl->type, "jcifs") == 0) {
			fprintf(idl->out, "public class %s {\n\n", iface->name);
		} else {
			fprintf(idl->out, "public class %s extends Stub {\n\n", iface->name);
		}
		print(idl, 4, "public static String getSyntax() {\n");
		print(idl, 8, "return \"%s:%s\";\n", uuid, version);
		print(idl, 4, "}\n\n");
	} else {
		fprintf(idl->out, "public class %s {\n\n", iface->name);
	}

	if (convert_primative_pointers(idl, iface) == -1) {
		AMSG("");
		return -1;
	}

	idl->opnum = 0;

	linkedlist_iterate(&iface->mems, &iter);
	while ((sym = linkedlist_next(&iface->mems, &iter))) {
		if (sym->noemit) {
			continue;
		}

		if (IS_ENUM(sym)) {
			struct sym *mem;
			iter_t iter2;

			linkedlist_iterate(&sym->mems, &iter2);
			while ((mem = linkedlist_next(&sym->mems, &iter2))) {
				print(idl, 4, "public static final int %s = %s;\n", mem->name, mem->value);
			}
			fputc('\n', idl->out);
		}
		if (IS_CONST(sym)) {
			print(idl, 4, "public static final %s %s = %s;\n",
					sym->out_type, sym->name, sym->value);
		} else if (IS_OPERATION(sym)) {
			if (emit_oper(idl, sym, 4) == -1) {
				AMSG("");
				return -1;
			}
		} else if (!IS_PRIMATIVE(sym) && !IS_UNION(sym)) {
			if (emit_class_def(idl, sym, 4) == -1) {
				AMSG("");
				return -1;
			}
		}
	}

	fprintf(idl->out, "}\n");

	return 0;
}
int
emit_stub_jcifs(struct idl *idl, struct sym *iface)
{
	if (emit_stub_java(idl, iface) < 0) {
		AMSG("");
		return -1;
	}

	return 0;
}

