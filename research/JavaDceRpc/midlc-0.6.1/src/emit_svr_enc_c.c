#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <ctype.h>
#include <mba/msgno.h>
#include "sym.h"
#include "midlc.h"

int
emit_encoder_fragment(struct idl *idl,
		struct sym *sym,
		int mode,
		int indent)
{
	char name[255];
	char buf[512];
	struct sym sym0 = *sym;

	strcpy(name, sym->name);
	strreplace(name, '.', '_');

	if (mode == 3) { /* just variable declariations */
		if (sym->ptr && !sym->do_array) {
		} else if (IS_UNION(sym)) {
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

			print(idl, indent, "%s _descr;\n", switch_type->out_type);
			return 0;
		} else if (IS_ARRAY(sym)) {
			const char *length_is = hashmap_get(&sym->attrs, "length_is");

			if (length_is) {
				print(idl, indent, "uint32_t _%sl;\n", name);
			}
			print(idl, indent, "uint32_t _%ss;\n", name);
			if (!sym->ptr) {
				print(idl, indent, "size_t _%so;\n", name);
			}
			if (idl->for_loop_redecl == 0) {
				idl->for_loop_redecl = 1;
				print(idl, indent, "uint32_t _i;\n");
			}
		} else if (sym->orig->ptr) {
		} else if (IS_STRUCTURE(sym)) {
			iter_t iter;
			struct sym *mem, sym1;

			idl->for_loop_redecl = 0;
			linkedlist_iterate(&sym->mems, &iter);
			while ((mem = linkedlist_next(&sym->mems, &iter))) {
				sym1 = *mem;

				sprintf(buf, "%s.%s", sym->name, mem->name);

				sym1.name = buf;
				if (emit_encoder_fragment(idl, &sym1, 3, indent) == -1) {
					AMSG("");
					return -1;
				}
			}
		}
	} else if (mode == 0) {
		if (sym->ptr && !sym->do_array) {
			print(idl, indent, "enc_ndr_referent(ndr, obj->%s, %d, off);\n",
					sym->name, sym->ptr_type);
		} else if (IS_UNION(sym)) {
			struct sym *switch_type;
			char *st;
			const char *switch_is = hashmap_get(&sym->attrs, "switch_is");
			iter_t iter;
			struct sym *mem;

			if ((st = hashmap_get(&sym->attrs, "switch_type"))) {
				if ((switch_type = symlook(idl, st)) == NULL) {
					AMSG("No such switch_type");
					return -1;
				}
			} else if ((switch_type = get_descriminant(sym)) == NULL) {
				PMSG("Failed to find switch_is symbol");
			}

			buf[0] = '\0';
			switch_is = convexpr(switch_is, buf);

			print(idl, indent, "_descr = %s;\n", switch_is);
			print(idl, indent, "enc_ndr_%s(ndr, _descr, off);\n", switch_type->ndr_type);
			print(idl, indent, "switch (_descr) {\n");

			linkedlist_iterate(&sym->mems, &iter);
			while ((mem = linkedlist_next(&sym->mems, &iter))) {
				struct sym sym1;

				sym1 = *mem;
				sym1.name = sym->name;

				print(idl, indent + 4, "case %s:\n", hashmap_get(&mem->attrs, "case"));

				if (mem->ptr) {
					if (emit_encoder_fragment(idl, &sym1, 0, indent + 8) == -1) {
						AMSG("");
						return -1;
					}
	
					fputc('\n', idl->out);
	
					if (emit_encoder_fragment(idl, &sym1, 1, indent + 8) == -1) {
						AMSG("");
						return -1;
					}
				} else {
					if (IS_PRIMATIVE(mem)) {
						int deref = sym->orig->ptr && !IS_ARRAY(sym->orig);
						if (deref) {
							print(idl, indent + 8,
									"enc_ndr_%s(ndr, *(%s *)obj->%s, off);\n",
									mem->ndr_type, mem->idl_type, sym1.name);
						} else {
							print(idl, indent + 8,
									"enc_ndr_%s(ndr, obj->%s, off);\n",
									mem->ndr_type, sym1.name);
						}
					} else {
						print(idl, indent + 8,
								"enc_%s(ndr, (%s *)obj->%s, off);\n",
								mem->out_type, mem->idl_type, sym1.name);
					}
				}
				print(idl, indent + 8, "break;\n");
			}

			print(idl, indent, "}\n");
		} else if (IS_ARRAY(sym)) {
			const char *size_is = hashmap_get(&sym->attrs, "size_is");
			const char *length_is = hashmap_get(&sym->attrs, "length_is");

			buf[0] = '\0';
			if (strcmp(sym->name, sym->orig->name) != 0) {
				sprintf(buf, "%s.", sym->parent->name);
			}

			if (length_is) {
				length_is = convexpr(length_is, buf);
				print(idl, indent, "_%sl = %s;\n", name, length_is);
			}
			if (!IS_EMBEDDED_CONFORMANT(sym)) {
				size_is = convexpr(size_is, buf);
				print(idl, indent, "_%ss = %s;\n", name, size_is);
			}
			if (!IS_FIXED(sym)) {
				print(idl, indent, "enc_ndr_long(ndr, _%ss, off);\n", name);
			}
			if (length_is) {
				print(idl, indent, "enc_ndr_long(ndr, 0, off);\n");
				print(idl, indent, "enc_ndr_long(ndr, _%sl, off);\n", name);
			}
			if (!sym->ptr) {
				print(idl, indent, "_%so = *off;\n", name);
				print(idl, indent, "*off += %d * _%s%s;\n",
						sym->ndr_size, name, length_is ? "l" : "s");
			} else { /* must be array of pointers */
				print(idl, indent, "for (_i = 0; _i < _%s%s; _i++) {\n",
						name, length_is ? "l" : "s");
				print(idl, indent + 4, "enc_ndr_referent(ndr, obj->%s[_i], %d, off);\n",
						sym->name, sym->ptr_type);
				print(idl, indent, "}\n");
			}
		} else if (hashmap_get(&sym->attrs, "string")) {
			print(idl, indent, "enc_ndr_string(ndr, obj->%s, off);\n", sym->name);
		} else if (IS_PRIMATIVE(sym)) {
			char *deref = sym->orig->ptr && !IS_ARRAY(sym->orig) ? "*" : "";
			print(idl, indent, "enc_ndr_%s(ndr, %sobj->%s, off);\n", sym->ndr_type, deref, sym->name);
		} else if (sym->orig->ptr) {
			print(idl, indent, "enc_%s(ndr, obj->%s, off);\n",
					sym->out_type, sym->name);
		} else if (IS_STRUCTURE(sym)) {
			iter_t iter;
			struct sym *mem, sym1;

			linkedlist_iterate(&sym->mems, &iter);
			while ((mem = linkedlist_next(&sym->mems, &iter))) {
				sym1 = *mem;

				sprintf(buf, "%s.%s", sym->name, mem->name);

				sym1.name = buf;
				if (emit_encoder_fragment(idl, &sym1, 0, indent) == -1) {
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
				print(idl, indent, "if (obj->%s != NULL) {\n", sym0.name);
			} else {
				print(idl, indent, "if (obj->%s == NULL) return -1; /* null ref */\n", sym0.name);
			}

			sym0.ptr--;
			if (IS_ARRAY(sym)) {
				sym0.do_array = 1;
			}

			if (emit_encoder_fragment(idl, &sym0, 3, ind) == -1) {
				AMSG("");
				return -1;
			}

			fputc('\n', idl->out);

			if (!IS_PARAMETER(sym)) {
				print(idl, ind, "off = &ndr->deferred;\n");
			}
			if (emit_encoder_fragment(idl, &sym0, 0, ind) == -1) {
				AMSG("");
				return -1;
			}

			fputc('\n', idl->out);

			if (emit_encoder_fragment(idl, &sym0, 1, ind) == -1) {
				AMSG("");
				return -1;
			}

			if (!is_ref) {
				print(idl, indent, "}\n");
			}
		} else if (IS_ARRAY(sym)) {
			const char *length_is = hashmap_get(&sym->attrs, "length_is");

			if (!sym->ptr) {
				print(idl, indent, "off = &_%so;\n", name);
			}
			print(idl, indent, "for (_i = 0; _i < _%s%s; _i++) {\n",
						name, length_is ? "l" : "s");
			if (IS_FIXED(sym) || IS_PRIMATIVE(sym)) {
				sprintf(buf, "%s[_i]", sym->name);
			} else {
				sprintf(buf, "%s + _i", sym->name);
			}
			sym0.name = buf;
			sym0.flags &= ~FLAGS_ARRAY;
			sym0.do_array = 0;
			if (emit_encoder_fragment(idl, &sym0, sym->ptr != 0, indent + 4) == -1) {
				AMSG("");
				return -1;
			}
			print(idl, indent, "}\n\n");
		} else if (sym->orig->ptr) {
		} else if (IS_STRUCTURE(sym)) {
			iter_t iter;
			struct sym *mem, sym1;

			linkedlist_iterate(&sym->mems, &iter);
			while ((mem = linkedlist_next(&sym->mems, &iter))) {
				sym1 = *mem;

				sprintf(buf, "%s.%s", sym->name, mem->name);

				sym1.name = buf;
				if (emit_encoder_fragment(idl, &sym1, 1, indent) == -1) {
					AMSG("");
					return -1;
				}
			}
		}
	}

	return 0;
}
int
emit_encoder(struct idl *idl,
		struct sym *sym,
		int indent)
{
	iter_t iter;
	struct sym *mem;

	emit_marsh_proto(idl, sym, 1, 1);
	print(idl, indent, "\n{\n");

	idl->for_loop_redecl = 0;
	linkedlist_iterate(&sym->mems, &iter);
	while ((mem = linkedlist_next(&sym->mems, &iter))) {
		if (emit_encoder_fragment(idl, mem, 3, indent + 4) == -1) {
			AMSG("");
			return -1;
		}
	}
	fputc('\n', idl->out);

	print(idl, indent + 4, "enc_ndr_align(ndr, %d, off);\n", sym->align);

	linkedlist_iterate(&sym->mems, &iter);
	while ((mem = linkedlist_next(&sym->mems, &iter))) {
		const char *size_is;
		if (!IS_EMBEDDED_CONFORMANT(mem)) {
			continue;
		}
		size_is = hashmap_get(&mem->attrs, "size_is");
		print(idl, indent + 4, "_%ss = obj->%s;\n", mem->name, size_is);
		print(idl, indent + 4, "enc_ndr_long(ndr, _%ss, off);\n", mem->name);
	}

	linkedlist_iterate(&sym->mems, &iter);
	while ((mem = linkedlist_next(&sym->mems, &iter))) {
		if (emit_encoder_fragment(idl, mem, 0, indent + 4) == -1) {
			AMSG("");
			return -1;
		}
	}

	fputc('\n', idl->out);

	linkedlist_iterate(&sym->mems, &iter);
	while ((mem = linkedlist_next(&sym->mems, &iter))) {
		if (emit_encoder_fragment(idl, mem, 1, indent + 4) == -1) {
			AMSG("");
			return -1;
		}
	}

	fputc('\n', idl->out);
	print(idl, indent + 4, "return 0;\n");
	print(idl, indent, "}\n");

	return 0;
}
int
emit_params_marsh_proto(struct idl *idl, struct sym *sym, int enc, int out)
{
	if (enc) {
		print(idl, 0, "static int\nenc_%s_params_%s(struct ndr *ndr, "
				"struct params_%s *obj, size_t *off)",
				out ? "out" : "in", sym->name, sym->name);
	} else {
		print(idl, 0, "static int\ndec_%s_params_%s(struct ndr *ndr, "
				"struct params_%s *obj, size_t *off)",
				out ? "out" : "in", sym->name, sym->name);
	}

	return 0;
}

int
emit_params_encoder(struct idl *idl,
		struct sym *sym,
		int out,
		int indent)
{
	iter_t iter;
	struct sym *mem, sym0;

	emit_params_marsh_proto(idl, sym, 1, out);
	print(idl, indent, "\n{\n");

	idl->for_loop_redecl = 0;
	linkedlist_iterate(&sym->mems, &iter);
	while ((mem = linkedlist_next(&sym->mems, &iter))) {
		if (hashmap_get(&mem->attrs, out ? "out" : "in") == NULL) {
			continue;
		}

		sym0 = *mem;
		if (IS_REF(mem)) {
			sym0.ptr--;
		}
		if (emit_encoder_fragment(idl, &sym0, 3, indent + 4) == -1) {
			AMSG("");
			return -1;
		}
	}

	fputc('\n', idl->out);

	linkedlist_iterate(&sym->mems, &iter);
	while ((mem = linkedlist_next(&sym->mems, &iter))) {
		if (hashmap_get(&mem->attrs, out ? "out" : "in") == NULL) {
			continue;
		}

		sym0 = *mem;
		if (IS_REF(mem)) {
			sym0.ptr--;
		}

		if (emit_encoder_fragment(idl, &sym0, 0, indent + 4) == -1 ||
			emit_encoder_fragment(idl, &sym0, 1, indent + 4) == -1) {
			AMSG("");
			return -1;
		}

		fputc('\n', idl->out);
	}

	if (out && strcmp(sym->idl_type, "void") != 0) { /* emit return value encoder */
		struct sym *s;
		if ((s = symlook(idl, sym->idl_type)) == NULL) {
			AMSG("");
			return -1;
		}
		sym0 = *s;
		sym0.name = "retval";
		if (emit_encoder_fragment(idl, &sym0, 0, indent + 4) == -1) {
			AMSG("");
			return -1;
		}
	}

	fputc('\n', idl->out);
	print(idl, indent + 4, "(void)ndr; (void)obj; (void)off;\n");
	print(idl, indent + 4, "return 0;\n");
	print(idl, indent, "}\n");

	return 0;
}

