#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <ctype.h>
#include <mba/msgno.h>
#include "sym.h"
#include "midlc.h"

int
emit_decoder_frag(struct idl *idl,
		struct sym *sym,
		int mode,
		int indent)
{
	char name[255];
	char buf[512];
	struct sym sym0 = *sym;

	strcpy(name, sym->name);
	strreplace(name, '.', '_');

	if (mode == 0) {
		if (sym->ptr && !sym->do_array) {
			print(idl, indent, "int _%sp = _src.dec_ndr_long();\n", name);
		} else if (IS_UNION(sym)) {
			struct sym sym1 = *((struct sym *)linkedlist_get(&sym->mems, 0));
			struct sym *switch_type;
			char *st;

			if ((st = hashmap_get(&sym->attrs, "switch_type"))) {
				if ((switch_type = symlook(idl, st)) == NULL) {
					AMSG("No such switch_type");
					return -1;
				}
			} else if ((switch_type = get_descriminant(sym)) == NULL) {
				PMSG("Failed to find switch_is symbol");
			}

			print(idl, indent, "_src.dec_ndr_%s(); /* union discriminant */\n", switch_type->ndr_type);
			if (IS_PARAMETER(sym)) {
				sym1.flags &= ~FLAGS_PRIMATIVE;
			}
			sym1.name = sym->name;
			if (emit_decoder_frag(idl, &sym1, 0, indent) == -1) {
				AMSG("");
				return -1;
			}
			return 0;
		} else if (IS_ARRAY(sym)) {
			const char *length_is = hashmap_get(&sym->attrs, "length_is");
			const char *size_is = hashmap_get(&sym->attrs, "size_is");

			if (!IS_FIXED(sym)) {
				print(idl, indent, "int _%ss = _src.dec_ndr_long();\n", name);
			} else if (!IS_EMBEDDED_CONFORMANT(sym)) {
				print(idl, indent, "int _%ss = %s;\n", name, size_is);
			}
			if (length_is) {
				print(idl, indent, "_src.dec_ndr_long();\n");
				print(idl, indent, "int _%sl = _src.dec_ndr_long();\n", name);
			}
			if (!sym->ptr) {
				print(idl, indent, "int _%si = _src.index;\n", name);
				print(idl, indent, "_src.advance(%d * _%s%s);\n",
						sym->ndr_size, name, length_is ? "l" : "s");
			} else { /* must be array of pointers */
				print(idl, indent, "for (int _i = 0; _i < _%s%s; _i++) {\n",
							name, length_is ? "l" : "s");
				if (!IS_PRIMATIVE(sym)) {
					print(idl, indent + 4, "if (%s[_i] == null) {\n", sym->name);
					print(idl, indent + 8, "%s[_i] = new %s();\n", sym->name, sym->out_type);
					print(idl, indent + 4, "}\n", sym->name);
				}
				print(idl, indent + 4, "int _%sp = _src.dec_ndr_long();\n", name);
				print(idl, indent, "}\n");
			}
		} else if (hashmap_get(&sym->attrs, "string")) {
			print(idl, indent, "%s = _src.dec_ndr_string();\n", sym->name);
		} else if (IS_PRIMATIVE(sym) && memcmp(sym->out_type, "Ndr", 3) != 0) {
			print(idl, indent, "%s = (%s)_src.dec_ndr_%s();\n", sym->name, sym->out_type, sym->ndr_type);
		} else if (sym->orig->ptr || IS_UNION(sym->parent)) {
			if (strcmp(idl->type, "jcifs") == 0) {
				print(idl, indent, "%s.decode(_src);\n", sym->name);
			} else {
				print(idl, indent, "%s.decode(_ndr, _src);\n", sym->name);
			}
		} else if (IS_STRUCTURE(sym)) {
			iter_t iter;
			struct sym *mem, sym1;

			print(idl, indent, "_src.align(%d);\n", sym->align);
			print(idl, indent, "if (%s == null) {\n", sym->name);
			print(idl, indent + 4, "%s = new %s();\n", sym->name, sym->out_type);
			print(idl, indent, "}\n");

			linkedlist_iterate(&sym->mems, &iter);
			while ((mem = linkedlist_next(&sym->mems, &iter))) {
				sym1 = *mem;

				sprintf(buf, "%s.%s", sym->name, mem->name);

				sym1.name = buf;
				if (emit_decoder_frag(idl, &sym1, 0, indent) == -1) {
					AMSG("");
					return -1;
				}
			}
		}
	} else if (mode == 1) {
		if (sym->ptr && !sym->do_array) {
			int is_ref = IS_REF(sym);
			int ind = indent + 4 * !is_ref;

			if (!is_ref) {
				print(idl, indent, "if (_%sp != 0) {\n", name);
				if (!IS_PRIMATIVE(sym) && !IS_ARRAY(sym) && !IS_UNION(sym)) {
					print(idl, indent + 4, "if (%s == null) { /* YOYOYO */\n", name);
					print(idl, indent + 8, "%s = new %s();\n", name, sym->out_type);
					print(idl, indent + 4, "}\n");
				}
			}

			if (!IS_PARAMETER(sym)) {
				print(idl, ind, "_src = _src.deferred;\n");
			}
			sym0.ptr--;
			if (IS_ARRAY(sym)) {
				sym0.do_array = 1;
			}
			if (emit_decoder_frag(idl, &sym0, 0, ind) == -1) {
				AMSG("");
				return -1;
			}

			fputc('\n', idl->out);

			if (emit_decoder_frag(idl, &sym0, 1, ind) == -1) {
				AMSG("");
				return -1;
			}

			if (!is_ref) {
				print(idl, indent, "}\n");
			}
		} else if (IS_UNION(sym)) {
			struct sym sym1 = *((struct sym *)linkedlist_get(&sym->mems, 0));
			sym1.name = sym->name;

			if (emit_decoder_frag(idl, &sym1, 1, indent) == -1) {
				AMSG("");
				return -1;
			}
			return 0;
		} else if (IS_ARRAY(sym)) {
			const char *length_is = hashmap_get(&sym->attrs, "length_is");

			print(idl, indent, "if (%s == null) {\n", sym->name);
			print(idl, indent + 4, "if (_%ss < 0 || _%ss > 0xFFFF) throw new NdrException( NdrException.INVALID_CONFORMANCE );\n", name, name);
			print(idl, indent + 4, "%s = new %s[_%ss];\n", sym->name, sym->out_type, name);
			print(idl, indent, "}\n");
			print(idl, indent, "_src = _src.derive(_%si);\n", name);
			print(idl, indent, "for (int _i = 0; _i < _%s%s; _i++) {\n",
						name, length_is ? "l" : "s");
			if (!IS_PRIMATIVE(sym)) {
				print(idl, indent + 4, "if (%s[_i] == null) {\n", sym->name);
				print(idl, indent + 8, "%s[_i] = new %s();\n", sym->name, sym->out_type);
				print(idl, indent + 4, "}\n", sym->name);
			}
			sprintf(buf, "%s[_i]", sym->name);
			sym0.name = buf;
			sym0.flags &= ~FLAGS_ARRAY;
			sym0.do_array = 0;
			if (emit_decoder_frag(idl, &sym0, sym->ptr != 0, indent + 4) == -1) {
				AMSG("");
				return -1;
			}
			print(idl, indent, "}\n");
		} else if (sym->orig->ptr || IS_UNION(sym->parent)) {
		} else if (IS_STRUCTURE(sym)) {
			iter_t iter;
			struct sym *mem, sym1;

			linkedlist_iterate(&sym->mems, &iter);
			while ((mem = linkedlist_next(&sym->mems, &iter))) {
				sym1 = *mem;

				sprintf(buf, "%s.%s", sym->name, mem->name);

				sym1.name = buf;
				if (emit_decoder_frag(idl, &sym1, 1, indent) == -1) {
					AMSG("");
					return -1;
				}
			}
		}
	}

	return 0;
}
int
emit_dec(struct idl *idl,
		struct sym *sym,
		int indent)
{
	iter_t iter;
	struct sym *mem;

	if (strcmp(idl->type, "jcifs") == 0) {
		print(idl, indent, "public void decode(NdrBuffer _src) throws NdrException {\n");
	} else {
		print(idl, indent, "public void decode(NetworkDataRepresentation _ndr, NdrBuffer _src) throws NdrException {\n");
	}

	print(idl, indent + 4, "_src.align(%d);\n", sym->align);

	linkedlist_iterate(&sym->mems, &iter);
	while ((mem = linkedlist_next(&sym->mems, &iter))) {
		if (!IS_EMBEDDED_CONFORMANT(mem)) {
			continue;
		}
		print(idl, indent + 4, "int _%ss = _src.dec_ndr_long();\n", mem->name);
	}

	linkedlist_iterate(&sym->mems, &iter);
	while ((mem = linkedlist_next(&sym->mems, &iter))) {
		if (emit_decoder_frag(idl, mem, 0, indent + 4) == -1) {
			AMSG("");
			return -1;
		}
	}

	fputc('\n', idl->out);

	linkedlist_iterate(&sym->mems, &iter);
	while ((mem = linkedlist_next(&sym->mems, &iter))) {
		if (emit_decoder_frag(idl, mem, 1, indent + 4) == -1) {
			AMSG("");
			return -1;
		}
	}

	print(idl, indent, "}\n");

	return 0;
}
int
emit_params_dec(struct idl *idl,
		struct sym *sym,
		int indent)
{
	iter_t iter;
	struct sym *mem, sym0;

	if (strcmp(idl->type, "jcifs") == 0) {
		print(idl, indent, "public void decode_out(NdrBuffer _src) throws NdrException {\n");
	} else {
		print(idl, indent, "public void decode(NetworkDataRepresentation _ndr, NdrBuffer _src) throws NdrException {\n");
	}

	linkedlist_iterate(&sym->mems, &iter);
	while ((mem = linkedlist_next(&sym->mems, &iter))) {
		if (hashmap_get(&mem->attrs, "out") == NULL) {
			continue;
		}

		sym0 = *mem;
		if (IS_REF(mem)) {
			sym0.ptr--;
		}

		if (emit_decoder_frag(idl, &sym0, 0, indent + 4) == -1) {
			AMSG("");
			return -1;
		}
		if (emit_decoder_frag(idl, &sym0, 1, indent + 4) == -1) {
			AMSG("");
			return -1;
		}
	}
	if (strcmp(sym->idl_type, "void") != 0) { /* emit return value encoder */
		struct sym *s;
		if ((s = symlook(idl, sym->idl_type)) == NULL) {
			AMSG("");
			return -1;
		}
		sym0 = *s;
		sym0.name = "retval";
		if (emit_decoder_frag(idl, &sym0, 0, indent + 4) == -1) {
			AMSG("");
			return -1;
		}
	}

	print(idl, indent, "}\n");

	return 0;
}

