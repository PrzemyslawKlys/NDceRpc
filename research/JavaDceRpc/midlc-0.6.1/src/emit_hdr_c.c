#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <limits.h>
#include <ctype.h>
#include <mba/msgno.h>
#include "sym.h"
#include "midlc.h"

int
emit_member(struct idl *idl, struct sym *sym, const char *name, int indent)
{
	const char *out_type;
	char *is_string = hashmap_get(&sym->attrs, "string");

	if (is_string) {
		out_type = "char_t";
	} else {
		out_type = sym->idl_type;
	}

	if (IS_ARRAY(sym)) {
		if (IS_FIXED(sym)) {
			const char *size_is = hashmap_get(&sym->attrs, "size_is");
			if (IS_PARAMETER(sym)) {
				size_is = "";
			} else if (IS_CONFORMANT(sym)) {
				size_is = "1";
			}
			print(idl, indent, "%s %s[%s]", out_type, name, size_is);
		} else if (sym->ptr) {
			print(idl, indent, "%s *%s", out_type, name);
		} else {
			print(idl, indent, "%s %s[1]", out_type, name);
		}
	} else {
		int ptr = sym->ptr;

		print(idl, indent, "%s ", out_type);
		while (ptr--) {
			fputc('*', idl->out);
		}
		print(idl, 0, "%s", name);
	}

	return 0;
}
int
emit_struct(struct idl *idl, struct sym *sym, int indent)
{
	iter_t iter;
	struct sym *mem;

	if (IS_TYPEDEFD(sym)) {
		print(idl, indent, "typedef %s {\n", IS_UNION(sym) ? "union" : "struct");
	} else if (IS_OPERATION(sym)) {
		print(idl, indent, "struct params_%s {\n", sym->name);
	} else {
		print(idl, indent, "%s {\n", sym->idl_type);
	}

	if (IS_OPERATION(sym) && strcmp(sym->idl_type, "void") != 0) {
		emit_member(idl, sym, "retval", indent + 4);
		fprintf(idl->out, ";\n");
	}

	linkedlist_iterate(&sym->mems, &iter);
	while ((mem = linkedlist_next(&sym->mems, &iter))) {
		struct sym sym0 = *mem;
		if (IS_PARAMETER(mem)) {
			sym0.flags &= ~FLAGS_PARAMETER;
			if (IS_FIXED(mem)) {
				sym0.flags &= ~FLAGS_FIXED;
				sym0.ptr++;
			}
		}
		emit_member(idl, &sym0, mem->name, indent + 4);
		fprintf(idl->out, ";\n");
	}

	if (IS_TYPEDEFD(sym)) {
		print(idl, indent, "} %s;\n", sym->name);
	} else {
		print(idl, indent, "};\n");
	}

	return 0;
}
int
emit_proto(struct idl *idl, struct sym *sym, int indent)
{
	iter_t iter;
	struct sym *mem;
	int wrap = linkedlist_size(&sym->mems) > 2;

	print(idl, indent, "%s %s(void *context", sym->out_type, sym->name);

	linkedlist_iterate(&sym->mems, &iter);
	while ((mem = linkedlist_next(&sym->mems, &iter))) {
		if (wrap) {
			fprintf(idl->out, ",\n");
			sp(idl->out, indent + 12);
		} else {
			fprintf(idl->out, ", ");
		}
		emit_member(idl, mem, mem->name, 0);
	}
	print(idl, indent, ");\n");

	return 0;
}
int
emit_marsh_proto(struct idl *idl, struct sym *sym, int enc, int nl)
{
	if (enc) {
		print(idl, 0, "int%senc_%s(struct ndr *ndr, %s *obj, size_t *off)",
				nl ? "\n" : " ", sym->out_type, sym->idl_type);
	} else {
		print(idl, 0, "int%sdec_%s(struct ndr *ndr, %s *obj, size_t *off)",
				nl ? "\n" : " ", sym->out_type, sym->idl_type);
	}

	return 0;
}
int
emit_hdr_c(struct idl *idl, struct sym *iface)
{
	iter_t iter;
	struct sym *sym;
	int nl = 0;

	idl->opnum = 0;

	print(idl, 0, "#ifndef _%s_h\n", iface->name);
	print(idl, 0, "#define _%s_h\n\n", iface->name);
	print(idl, 0, "#ifdef __cplusplus\nextern \"C\" {\n#endif\n\n", iface->name);

	linkedlist_iterate(&iface->mems, &iter);
	while ((sym = linkedlist_next(&iface->mems, &iter))) {
		if (IS_IMPORT(sym)) {
			char buf[PATH_MAX + 1];
			mkoutname(buf, sym->name, ".h");
			print(idl, 0, "#include \"%s\"\n", buf);
			nl = 1;
		}
	}
	while (nl) {
		nl--;
		fprintf(idl->out, "\n");
	}

	linkedlist_iterate(&iface->mems, &iter);
	while ((sym = linkedlist_next(&iface->mems, &iter))) {
		if (IS_TYPEDEFD(sym) && IS_PRIMATIVE(sym) && !IS_OPERATION(sym) && !IS_ENUM(sym)) {
			print(idl, 0, "typedef %s %s;\n", sym->idl_type, sym->name);
			nl = 1;
		}
	}
	while (nl) {
		nl--;
		fprintf(idl->out, "\n");
	}

	linkedlist_iterate(&iface->mems, &iter);
	while ((sym = linkedlist_next(&iface->mems, &iter))) {
		if (sym->noemit) {
			continue;
		}

		if (IS_ENUM(sym) && sym->name) {
			struct sym *mem;
			iter_t iter2;
			char fmt[64];
			int maxnamelen = 0;

			linkedlist_iterate(&sym->mems, &iter2);
			while ((mem = linkedlist_next(&sym->mems, &iter2))) {
				int n = strlen(mem->name);
				if (n > maxnamelen) {
					maxnamelen = n;
				}
			}
			if (IS_TYPEDEFD(sym)) {
				fprintf(idl->out, "typedef enum {\n");
			} else if (sym->name) {
				fprintf(idl->out, "enum %s {\n", sym->name);
			} else {
				fprintf(idl->out, "enum {\n");
			}

			sprintf(fmt, "%%-%ds = %%s", maxnamelen);

			linkedlist_iterate(&sym->mems, &iter2);
			while ((mem = linkedlist_next(&sym->mems, &iter2))) {
				print(idl, 4, fmt, mem->name, mem->value);
				if (mem != linkedlist_get_last(&sym->mems)) {
					fputc(',', idl->out);
				}
				fputc('\n', idl->out);
			}

			if (IS_TYPEDEFD(sym)) {
				fprintf(idl->out, "} %s;\n", sym->name);
			} else {
				fprintf(idl->out, "};\n");
			}
			fputc('\n', idl->out);
		}
		if (IS_CONST(sym)) {
			print(idl, 4, "const %s %s = %s;\n",
					sym->out_type, sym->name, sym->value);
		}
		if (IS_STRUCTURE(sym) || IS_UNION(sym)) {
			if (emit_struct(idl, sym, 0) == -1) {
				AMSG("");
				return -1;
			}
			fprintf(idl->out, "\n");
		}
		if (IS_OPERATION(sym)) {
			if (emit_proto(idl, sym, 0) == -1) {
				AMSG("");
				return -1;
			}
			fprintf(idl->out, "\n");
		}
	}

	print(idl, 0, "#ifdef __cplusplus\n}\n#endif\n\n", iface->name);
	print(idl, 0, "#endif /* _%s_h */\n", iface->name);

	return 0;
}
