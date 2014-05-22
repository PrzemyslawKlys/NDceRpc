#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <ctype.h>
#include <mba/msgno.h>
#include "sym.h"
#include "midlc.h"

/* Translate a C expression int an equivalent Java one
 * e.g.
 *  length / 2   -->  length / 2
 *  *length / 2  -->  length / 2
 *  2 * length   -->  2 * length
 *  2 * *length  -->  2 * length
 *  2 * *length  -->  2 * length.value
 */
static int
isoperator(int ch)
{
	switch (ch) {
		case '+': case '-': case '*': case '/':
			return 1;
	}
	return 0;
}
static const char *
transexpr(const char *expr, const char *prefix, struct linkedlist *siblings)
{
	static char buf[1024];
	char *blim = buf + 1024, *bp = buf, *end = buf;
	const char *cp = NULL;
	int state = 0;
	int prev_was_operand = 0;

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
				bp += sprintf(bp, "%s", prefix);
				cp = bp;
			}
			state = 1;
			prev_was_operand = 1;
		} else if (isdigit(*expr)) {
			state = 2;
			prev_was_operand = 1;
		} else if (isoperator(*expr)) {
			if (*expr == '*' && !prev_was_operand) {
				expr++;
				continue;
			}
			prev_was_operand = 0;
		} else {
			if (state == 1) {
				char tmp[255];
				iter_t iter;
				struct sym *mem;

				*bp = '\0';
				strcpy(tmp, cp);

				linkedlist_iterate(siblings, &iter);
				while ((mem = linkedlist_next(siblings, &iter))) {
					if (strcmp(mem->name, tmp) == 0) {
						if (memcmp(mem->out_type, "Ndr", 3) == 0) {
							bp += sprintf(bp, ".value");
						}
						break;
					}
				}
			}
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
emit_encoder_frag(struct idl *idl,
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
			print(idl, indent, "_dst.enc_ndr_referent(%s, %d);\n", sym->name, sym->ptr_type);
		} else if (IS_UNION(sym)) {
			const char *switch_is = hashmap_get(&sym->attrs, "switch_is");
			struct sym sym1 = *((struct sym *)linkedlist_get(&sym->mems, 0));
			struct sym *switch_type;
			char *st;

			if ((st = hashmap_get(&sym->attrs, "switch_type")) == NULL) {
				if ((switch_type = get_descriminant(sym)) == NULL) {
					PMSG("Failed to find switch_is symbol");
				}
                /* For an enum the out_type is always 'int' but passing
                 * an int to enc_ndr_short is harmless */
				st = switch_type->idl_type;
			}
			if ((switch_type = symlook(idl, st)) == NULL) {
				AMSG("No such switch_type");
				return -1;
			}
			st = switch_type->out_type;

			buf[0] = '\0';
			switch_is = transexpr(switch_is, buf, &sym->parent->mems);
			print(idl, indent, "%s _descr = %s;\n", st, switch_is);
			print(idl, indent, "_dst.enc_ndr_%s(_descr);\n", switch_type->ndr_type);

			sym1.name = sym->name;
			if (IS_PARAMETER(sym)) {
				sym1.flags &= ~FLAGS_PRIMATIVE;
			}
			if (emit_encoder_frag(idl, &sym1, 0, indent) == -1) {
				AMSG("");
				return -1;
			}
			return 0;
		} else if (IS_ARRAY(sym)) {
			const char *size_is = hashmap_get(&sym->attrs, "size_is");
			const char *length_is = hashmap_get(&sym->attrs, "length_is");

			buf[0] = '\0';
			if (strcmp(sym->name, sym->orig->name) != 0) {
				sprintf(buf, "%s.", sym->parent->name);
			}

			if (length_is) {
				length_is = transexpr(length_is, buf, &sym->parent->mems);
				print(idl, indent, "int _%sl = %s;\n", name, length_is);
			}
			if (!IS_EMBEDDED_CONFORMANT(sym)) {
				size_is = transexpr(size_is, buf, &sym->parent->mems);
				print(idl, indent, "int _%ss = %s;\n", name, size_is);
			}
			if (!IS_FIXED(sym)) {
				print(idl, indent, "_dst.enc_ndr_long(_%ss);\n", name);
			}
			if (length_is) {
				print(idl, indent, "_dst.enc_ndr_long(0);\n");
				print(idl, indent, "_dst.enc_ndr_long(_%sl);\n", name);
			}
			if (!sym->ptr) {
				print(idl, indent, "int _%si = _dst.index;\n", name);
				print(idl, indent, "_dst.advance(%d * _%s%s);\n",
						sym->ndr_size, name, length_is ? "l" : "s");
			} else { /* must be array of pointers */
				print(idl, indent, "for (int _i = 0; _i < _%s%s; _i++) {\n",
						name, length_is ? "l" : "s");
				print(idl, indent + 4, "_dst.enc_ndr_referent(%s[_i], %d);\n", sym->name, sym->ptr_type);
				print(idl, indent, "}\n");
			}
		} else if (hashmap_get(&sym->attrs, "string")) {
			print(idl, indent, "_dst.enc_ndr_string(%s);\n", sym->name);
		} else if (IS_PRIMATIVE(sym) && memcmp(sym->out_type, "Ndr", 3) != 0) {
			print(idl, indent, "_dst.enc_ndr_%s(%s);\n", sym->ndr_type, sym->name);
		} else if (sym->orig->ptr || IS_UNION(sym->parent)) {
			if (strcmp(idl->type, "jcifs") == 0) {
				print(idl, indent, "%s.encode(_dst);\n", sym->name);
			} else {
				print(idl, indent, "%s.encode(_ndr, _dst);\n", sym->name);
			}
		} else if (IS_UNION(sym)) {
		} else if (IS_STRUCTURE(sym)) {
			iter_t iter;
			struct sym *mem, sym1;

			linkedlist_iterate(&sym->mems, &iter);
			while ((mem = linkedlist_next(&sym->mems, &iter))) {
				sym1 = *mem;

				sprintf(buf, "%s.%s", sym->name, mem->name);

				sym1.name = buf;
				if (emit_encoder_frag(idl, &sym1, 0, indent) == -1) {
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
				print(idl, indent, "if (%s != null) {\n", sym0.name);
			} else {
				print(idl, indent, "if (%s == null) throw new NdrException( NdrException.NO_NULL_REF );\n", sym0.name);
			}

			if (!IS_PARAMETER(sym)) {
				print(idl, ind, "_dst = _dst.deferred;\n");
			}
			sym0.ptr--;
			if (IS_ARRAY(sym)) {
				sym0.do_array = 1;
			}
			if (emit_encoder_frag(idl, &sym0, 0, ind) == -1) {
				AMSG("");
				return -1;
			}

			fputc('\n', idl->out);

			if (emit_encoder_frag(idl, &sym0, 1, ind) == -1) {
				AMSG("");
				return -1;
			}

			if (!is_ref) {
				print(idl, indent, "}\n");
			}
		} else if (IS_UNION(sym)) {
			struct sym sym1 = *((struct sym *)linkedlist_get(&sym->mems, 0));
			sym1.name = sym->name;
			if (emit_encoder_frag(idl, &sym1, 1, indent) == -1) {
				AMSG("");
				return -1;
			}
			return 0;
		} else if (IS_ARRAY(sym)) {
			const char *length_is = hashmap_get(&sym->attrs, "length_is");

			if (!sym->ptr) {
				print(idl, indent, "_dst = _dst.derive(_%si);\n", name);
			}
			print(idl, indent, "for (int _i = 0; _i < _%s%s; _i++) {\n",
						name, length_is ? "l" : "s");
			sprintf(buf, "%s[_i]", sym->name);
			sym0.name = buf;
			sym0.flags &= ~FLAGS_ARRAY;
			sym0.do_array = 0;
			if (emit_encoder_frag(idl, &sym0, sym->ptr != 0, indent + 4) == -1) {
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
				if (emit_encoder_frag(idl, &sym1, 1, indent) == -1) {
					AMSG("");
					return -1;
				}
			}
		}
	}

	return 0;
}
int
emit_enc(struct idl *idl,
		struct sym *sym,
		int indent)
{
	iter_t iter;
	struct sym *mem;

	if (strcmp(idl->type, "jcifs") == 0) {
		print(idl, indent, "public void encode(NdrBuffer _dst) throws NdrException {\n");
	} else {
		print(idl, indent, "public void encode(NetworkDataRepresentation _ndr, NdrBuffer _dst) throws NdrException {\n");
	}

	print(idl, indent + 4, "_dst.align(%d);\n", sym->align);

	linkedlist_iterate(&sym->mems, &iter);
	while ((mem = linkedlist_next(&sym->mems, &iter))) {
		const char *size_is;
		if (!IS_EMBEDDED_CONFORMANT(mem)) {
			continue;
		}
		size_is = hashmap_get(&mem->attrs, "size_is");
		print(idl, indent + 4, "int _%ss = %s;\n", mem->name, size_is);
		print(idl, indent + 4, "_dst.enc_ndr_long(_%ss);\n", mem->name);
	}

	linkedlist_iterate(&sym->mems, &iter);
	while ((mem = linkedlist_next(&sym->mems, &iter))) {
		if (emit_encoder_frag(idl, mem, 0, indent + 4) == -1) {
			AMSG("");
			return -1;
		}
	}

	fputc('\n', idl->out);

	linkedlist_iterate(&sym->mems, &iter);
	while ((mem = linkedlist_next(&sym->mems, &iter))) {
		if (emit_encoder_frag(idl, mem, 1, indent + 4) == -1) {
			AMSG("");
			return -1;
		}
	}

	print(idl, indent, "}\n");

	return 0;
}
int
emit_params_enc(struct idl *idl,
		struct sym *sym,
		int indent)
{
	iter_t iter;
	struct sym *mem, sym0;

	if (strcmp(idl->type, "jcifs") == 0) {
		print(idl, indent, "public void encode_in(NdrBuffer _dst) throws NdrException {\n");
	} else {
		print(idl, indent, "public void encode(NetworkDataRepresentation _ndr, NdrBuffer _dst) throws NdrException {\n");
	}

	linkedlist_iterate(&sym->mems, &iter);
	while ((mem = linkedlist_next(&sym->mems, &iter))) {
		if (hashmap_get(&mem->attrs, "in") == NULL) {
			continue;
		}

		sym0 = *mem;
		if (IS_REF(mem)) {
			sym0.ptr--;
		}

		if (emit_encoder_frag(idl, &sym0, 0, indent + 4) == -1) {
			AMSG("");
			return -1;
		}
		if (emit_encoder_frag(idl, &sym0, 1, indent + 4) == -1) {
			AMSG("");
			return -1;
		}
	}

	print(idl, indent, "}\n");

	return 0;
}

