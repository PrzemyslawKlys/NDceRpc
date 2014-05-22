#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <ctype.h>
#include <mba/msgno.h>
#include "sym.h"
#include "midlc.h"

int
emit_decoder_fragment(struct idl *idl,
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
		} else if (sym->orig->ptr || IS_UNION(sym->parent)) {
			if (!IS_PARAMETER(sym)) {
				struct sym *mem = linkedlist_get_last(&sym->mems);

				if (mem && IS_EMBEDDED_CONFORMANT(mem)) {
					print(idl, indent, "uint32_t _%st;\n", sym->name);
					print(idl, indent, "uint32_t _%sz;\n", sym->name);
				}
			}
		} else if (IS_STRUCTURE(sym)) {
			iter_t iter;
			struct sym *mem, sym1;

			idl->for_loop_redecl = 0;
			linkedlist_iterate(&sym->mems, &iter);
			while ((mem = linkedlist_next(&sym->mems, &iter))) {
				sym1 = *mem;

				sprintf(buf, "%s.%s", sym->name, mem->name);

				sym1.name = buf;
				if (emit_decoder_fragment(idl, &sym1, 3, indent) == -1) {
					AMSG("");
					return -1;
				}
			}
		}
	} else if (mode == 103) {
		/* This handles emitting declarations necessary for memory allocation
		 * of [out] only objects if we are decoding [in] parameters and [in] only
		 * objects if we are decoding [out] parameters.
		 */
		if (sym->ptr && !sym->do_array) {
		} else if (IS_UNION(sym)) {
			return 0;
		} else if (IS_ARRAY(sym)) {
			const char *length_is = hashmap_get(&sym->attrs, "length_is");

			if (length_is) {
				print(idl, indent, "uint32_t _%sl;\n", name);
			}
			print(idl, indent, "uint32_t _%ss;\n", name);
		} else if (sym->orig->ptr || IS_UNION(sym->parent)) {
			if (!IS_PARAMETER(sym)) {
				struct sym *mem = linkedlist_get_last(&sym->mems);

				if (mem && IS_EMBEDDED_CONFORMANT(mem)) {
					print(idl, indent, "uint32_t _%st;\n", sym->name);
					print(idl, indent, "uint32_t _%sz;\n", sym->name);
				}
			}
		} else if (IS_STRUCTURE(sym)) {
			iter_t iter;
			struct sym *mem, sym1;

			idl->for_loop_redecl = 0;
			linkedlist_iterate(&sym->mems, &iter);
			while ((mem = linkedlist_next(&sym->mems, &iter))) {
				sym1 = *mem;

				sprintf(buf, "%s.%s", sym->name, mem->name);

				sym1.name = buf;
				if (emit_decoder_fragment(idl, &sym1, 3, indent) == -1) {
					AMSG("");
					return -1;
				}
			}
		}
	} else if (mode == 4) {
		if (IS_ARRAY(sym)) {
			print(idl, indent, "if (_%ss > 0xFFFF) return -1;\n", name);
			print(idl, indent, "if (obj->%s == NULL)\n", name);
			print(idl, indent + 4, "obj->%s = ndr->alloc(ndr->alloc_context, "
						"_%ss * sizeof(%s), ndr->alloc_flags);\n",
						sym->name, sym->name, sym->out_type);
		} else if (sym->orig->ptr && hashmap_get(&sym->attrs, "string") == NULL) {
			print(idl, indent, "if (obj->%s == NULL)\n", name);
			print(idl, indent + 4, "obj->%s = ndr->alloc(ndr->alloc_context, "
						"sizeof(%s), ndr->alloc_flags);\n",
						sym->name, sym->out_type);
		}
	} else if (mode == 0) {
		if (!sym->ptr && sym->orig->ptr && !IS_PARAMETER(sym) && !IS_ARRAY(sym->orig)) {
			struct sym *mem = linkedlist_get_last(&sym->mems);

			if (mem && IS_EMBEDDED_CONFORMANT(mem)) {
				print(idl, indent, "dec_ndr_align(ndr, %d, off);\n", sym->align);
				print(idl, indent, "_%st = *off;\n", sym->name);
				print(idl, indent, "dec_ndr_long(ndr, &_%sz, &_%st);\n",
						sym->name, sym->name);
				print(idl, indent, "_%sz = sizeof(%s) + (_%sz - 1) * sizeof(%s);\n",
						sym->name, sym->idl_type, sym->name, mem->out_type);
				print(idl, indent, "if (obj->%s == NULL)\n", name);
				print(idl, indent + 4, "obj->%s = ndr->alloc(ndr->alloc_context, "
						"_%sz, ndr->alloc_flags);\n",
						sym->name, sym->name);
			} else {
				print(idl, indent, "if (obj->%s == NULL)\n", name);
				print(idl, indent + 4, "obj->%s = ndr->alloc(ndr->alloc_context, "
						"sizeof(%s), ndr->alloc_flags);\n",
						sym->name, sym->out_type);
			}
		}

		if (sym->ptr && !sym->do_array) {
			print(idl, indent, "dec_ndr_long(ndr, (uint32_t *)&obj->%s, off);\n", sym->name);
		} else if (IS_UNION(sym)) {
			struct sym *switch_type;
			char *st;
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

			print(idl, indent, "dec_ndr_%s(ndr, &_descr, off);\n", switch_type->ndr_type);
			print(idl, indent, "switch (_descr) {\n");

			linkedlist_iterate(&sym->mems, &iter);
			while ((mem = linkedlist_next(&sym->mems, &iter))) {
				struct sym sym1;

				sym1 = *mem;
				sym1.name = sym->name;

				print(idl, indent + 4, "case %s:\n", hashmap_get(&mem->attrs, "case"));

				if (mem->ptr) {
					if (emit_decoder_fragment(idl, &sym1, 0, indent + 8) == -1) {
						AMSG("");
						return -1;
					}
	
					fputc('\n', idl->out);
	
					if (emit_decoder_fragment(idl, &sym1, 1, indent + 8) == -1) {
						AMSG("");
						return -1;
					}
				} else {
					if (IS_PRIMATIVE(mem)) {
						if (sym->orig->ptr) {
							print(idl, indent + 8,
									"dec_ndr_%s(ndr, obj->%s, off);\n",
									mem->ndr_type, sym1.name);
						} else {
							print(idl, indent + 8,
									"dec_ndr_%s(ndr, &obj->%s, off);\n",
									mem->ndr_type, sym1.name);
						}
					} else {
						print(idl, indent + 8,
								"dec_%s(ndr, (%s *)&obj->%s, off);\n",
								mem->out_type, mem->idl_type, sym1.name);
					}
				}
				print(idl, indent + 8, "break;\n");
			}

			print(idl, indent, "}\n");

			return 0;
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
				print(idl, indent, "dec_ndr_long(ndr, &_%ss, off);\n", name);
			}
			if (length_is) {
				print(idl, indent, "dec_ndr_long(ndr, &_%sl, off);\n", name);
				print(idl, indent, "dec_ndr_long(ndr, &_%sl, off);\n", name);
			}
			if (!sym->ptr) {
				print(idl, indent, "_%so = *off;\n", name);
				print(idl, indent, "*off += %d * _%s%s;\n",
						sym->ndr_size, name, length_is ? "l" : "s");
			} else { /* must be array of pointers
				char *t = length_is ? "l" : "s";
				print(idl, indent, "for (_i = 0; _i < _%s%s; _i++) {\n",
						name, t);
				print(idl, indent + 4, "dec_ndr_long(ndr, obj->%s[_i], off);\n", sym->name);
				print(idl, indent, "}\n");
*/
			}
		} else if (hashmap_get(&sym->attrs, "string")) {
			print(idl, indent, "dec_ndr_string(ndr, &obj->%s, off);\n", sym->name);
		} else if (IS_PRIMATIVE(sym)) {
			char *addrof = sym->orig->ptr ? "" : "&";
			print(idl, indent, "dec_ndr_%s(ndr, %sobj->%s, off);\n",
					sym->ndr_type, addrof, sym->name);
		} else if (sym->orig->ptr) {
			print(idl, indent, "dec_%s(ndr, obj->%s, off);\n",
					sym->out_type, sym->name);
		} else if (IS_STRUCTURE(sym)) {
			iter_t iter;
			struct sym *mem, sym1;

			linkedlist_iterate(&sym->mems, &iter);
			while ((mem = linkedlist_next(&sym->mems, &iter))) {
				sym1 = *mem;

				sprintf(buf, "%s.%s", sym->name, mem->name);

				sym1.name = buf;
				if (emit_decoder_fragment(idl, &sym1, 0, indent) == -1) {
					AMSG("");
					return -1;
				}
			}
		}
	} else if (mode == 100) {
		if (sym->ptr && !sym->do_array) {
		} else if (IS_UNION(sym)) {
			return 0;
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

			if (emit_decoder_fragment(idl, &sym0, 3, ind) == -1) {
				AMSG("");
				return -1;
			}

			fputc('\n', idl->out);

			if (!IS_PARAMETER(sym)) {
				print(idl, ind, "off = &ndr->deferred;\n");
			}
			if (emit_decoder_fragment(idl, &sym0, 0, ind) == -1) {
				AMSG("");
				return -1;
			}

			fputc('\n', idl->out);

			if (emit_decoder_fragment(idl, &sym0, 1, ind) == -1) {
				AMSG("");
				return -1;
			}

			if (!is_ref) {
				print(idl, indent, "}\n");
			}
		} else if (IS_ARRAY(sym)) {
			const char *length_is = hashmap_get(&sym->attrs, "length_is");
			const char *suffix = length_is ? "l" : "s";

			if (!sym->ptr) {
				print(idl, indent, "off = &_%so;\n", name);
			}

/*
			if (sym->orig->ptr) {
				print(idl, indent, "if (_%s%s > 0xFFFF) return -1;\n", name, suffix);
				print(idl, indent, "if (obj->%s == NULL)\n", name);
				print(idl, indent + 4, "obj->%s = ndr->alloc(ndr->alloc_context, "
							"_%s%s * sizeof(%s), ndr->alloc_flags);\n",
							sym->name, name, suffix, sym->out_type);
			}
*/

			print(idl, indent, "for (_i = 0; _i < _%s%s; _i++) {\n",
						name, suffix);
			if (IS_FIXED(sym)) {
				sprintf(buf, "%s[_i]", sym->name);
			} else {
				sprintf(buf, "%s + _i", sym->name);
			}
			sym0.name = buf;
			sym0.flags &= ~FLAGS_ARRAY;
			sym0.do_array = 0;
			if (emit_decoder_fragment(idl, &sym0, sym->ptr != 0, indent + 4) == -1) {
				AMSG("");
				return -1;
			}
			print(idl, indent, "}\n");
		} else if (sym->orig->ptr) {
		} else if (IS_STRUCTURE(sym)) {
			iter_t iter;
			struct sym *mem, sym1;

			linkedlist_iterate(&sym->mems, &iter);
			while ((mem = linkedlist_next(&sym->mems, &iter))) {
				sym1 = *mem;

				sprintf(buf, "%s.%s", sym->name, mem->name);

				sym1.name = buf;
				if (emit_decoder_fragment(idl, &sym1, 1, indent) == -1) {
					AMSG("");
					return -1;
				}
			}
		}
	}

	return 0;
}
int
emit_decoder(struct idl *idl,
		struct sym *sym,
		int indent)
{
	iter_t iter;
	struct sym *mem;

	emit_marsh_proto(idl, sym, 0, 1);
	print(idl, indent, "\n{\n");

	idl->for_loop_redecl = 0;
	linkedlist_iterate(&sym->mems, &iter);
	while ((mem = linkedlist_next(&sym->mems, &iter))) {
		if (emit_decoder_fragment(idl, mem, 3, indent + 4) == -1) {
			AMSG("");
			return -1;
		}
	}
	fputc('\n', idl->out);

	print(idl, indent + 4, "dec_ndr_align(ndr, %d, off);\n", sym->align);

	linkedlist_iterate(&sym->mems, &iter);
	while ((mem = linkedlist_next(&sym->mems, &iter))) {
		if (!IS_EMBEDDED_CONFORMANT(mem)) {
			continue;
		}
		print(idl, indent + 4, "dec_ndr_long(ndr, &_%ss, off);\n", mem->name);
	}

	fputc('\n', idl->out);

	linkedlist_iterate(&sym->mems, &iter);
	while ((mem = linkedlist_next(&sym->mems, &iter))) {
		if (emit_decoder_fragment(idl, mem, 0, indent + 4) == -1) {
			AMSG("");
			return -1;
		}
	}

	fputc('\n', idl->out);

	linkedlist_iterate(&sym->mems, &iter);
	while ((mem = linkedlist_next(&sym->mems, &iter))) {
		if (emit_decoder_fragment(idl, mem, 1, indent + 4) == -1) {
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
emit_params_decoder(struct idl *idl,
		struct sym *sym,
		int out,
		int indent)
{
	iter_t iter;
	struct sym *mem, sym0;

	emit_params_marsh_proto(idl, sym, 0, out);
	print(idl, indent, "\n{\n");

	idl->for_loop_redecl = 0;
	linkedlist_iterate(&sym->mems, &iter);
	while ((mem = linkedlist_next(&sym->mems, &iter))) {
		int alt = hashmap_get(&mem->attrs, out ? "out" : "in") == NULL;
/*
		if (hashmap_get(&mem->attrs, out ? "out" : "in") == NULL) {
			continue;
		}
*/

		sym0 = *mem;
		if (IS_REF(mem)) {
			sym0.ptr--;
		}
		if (emit_decoder_fragment(idl, &sym0, alt ? 103 : 3, indent + 4) == -1) {
			AMSG("");
			return -1;
		}
	}

	fputc('\n', idl->out);

	linkedlist_iterate(&sym->mems, &iter);
	while ((mem = linkedlist_next(&sym->mems, &iter))) {
		int alt = hashmap_get(&mem->attrs, out ? "out" : "in") == NULL;
/*
		if (hashmap_get(&mem->attrs, out ? "out" : "in") == NULL) {
			continue;
		}
*/

		sym0 = *mem;
		if (IS_REF(mem)) {
			sym0.ptr--;
		}

		if (!IS_ARRAY(mem)) {
			if (emit_decoder_fragment(idl, &sym0, 4, indent + 4) == -1) {
				AMSG("");
				return -1;
			}
		}

		if (emit_decoder_fragment(idl, &sym0, alt ? 100 : 0, indent + 4) == -1) {
			AMSG("");
			return -1;
		}

		if (IS_ARRAY(mem)) {
			if (emit_decoder_fragment(idl, &sym0, 4, indent + 4) == -1) {
				AMSG("");
				return -1;
			}
		}

		if (!alt && emit_decoder_fragment(idl, &sym0, 1, indent + 4) == -1) {
			AMSG("");
			return -1;
		}

		fputc('\n', idl->out);
	}

	fputc('\n', idl->out);
	print(idl, indent + 4, "(void)ndr; (void)obj; (void)off;\n");
	print(idl, indent + 4, "return 0;\n");
	print(idl, indent, "}\n");

	return 0;
}
