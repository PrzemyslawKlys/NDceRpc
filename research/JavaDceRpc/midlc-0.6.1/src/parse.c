/* Mike Allen did not write this. His evil alternate personality did
 */

#include <stdlib.h>
#include <string.h>
#include <stdio.h>
#include <limits.h>
#include <errno.h>
#include <mba/stack.h>
#include <mba/suba.h>
#include <mba/text.h>
#include <mba/msgno.h>
#include "sym.h"
#include "tok.h"
#include "midlc.h"

#define NN(s) ((s) ? (s) : "")

int
symaddmem(struct sym *parent, struct sym *child)
{
	return linkedlist_add(&parent->mems, child);
}

int
parse(struct idl *idl, TOKFILE *in, struct sym *sym)
{
	unsigned char mem[0xFFF];
	struct allocator *al = suba_init(mem, 0xFFF, 1, 0);
	struct stack stk;
	char tok[TOKMAX], *mark = NULL;
	char buf[BUFSIZ], *bp = buf, *blim = buf + BUFSIZ;
	int ch, n, state, pop_state, retok;

	state = pop_state = retok = 0;

	stack_init(&stk, 1000, al);

	for ( ;; ) {
		if (!retok) {
			if ((n = tokget(in, tok, tok + TOKMAX)) == -1) {
				AMSG("");
				return -1;
			} else if (n == 0) {
				break;
			}
		} else {
			retok = 0;
		}
		ch = tok[0];

if (idl->verbose > 2) {
	fprintf(stderr, "%60d:%4d %s\n", state, pop_state, tok);
}

		switch (state) {
			case 100:
/* [key,key(data)] */
				if (ch == ']' || ch == ',') {
					char *key, *data;

					n = stack_size(&stk);
					if (n == 1) {
						key = data = stack_pop(&stk);
					} else if (n == 2) {
						data = stack_pop(&stk);
						key = stack_pop(&stk);
					} else {
						PMSG("%d: invalid attribute", in->line);
						return -1;
					}
					hashmap_put(&sym->attrs, key, data);
					if (ch == ']') {
						state = 0;
					}
				} else if (ch == '(') {
					int i;

					for (i = 0; (ch = fgetc(in->in)) != ')' && ch != EOF; i++) {
						tok[i] = ch;
					}
					tok[i] = '\0';

					stack_push(&stk, dupstr(tok, idl->al));
				} else {
					stack_push(&stk, dupstr(tok, idl->al));
				}
				break;
			case 200:
/* import "file1.idl", "file2.idl"; */
				if (ch == ';') {
					state = 0;
				} else if (ch == '"') {
					int i;
					struct sym iface;
					char buf[PATH_MAX];

					syminit(&iface, idl->al);

					for (i = 0; (ch = fgetc(in->in)) != '"' && ch != EOF; i++) {
						tok[i] = ch;
					}
					tok[i] = '\0';

					i = path_filename(sym->filename) - sym->filename;
					strncpy(buf, sym->filename, i);
					strcpy(buf + i, tok);

			/* We need to add a symbol to the parse tree for each import so that
			 * the C stub emitters can emit #include statements (and so that *all*
			 * of the IDL is represented in the parse tree)
			 */
					{
						struct sym *s = symnew(idl->al);
						s->flags |= FLAGS_IMPORT | FLAGS_EXPANDED;
						s->filename = sym->filename;
						s->idl_type = "import";
						s->name = dupstr(tok, idl->al);
						s->noemit = 1;
						symaddmem(sym->parent, s);
					}

					if (idl_process_file(idl, dupstr(buf, idl->al), &iface) == -1) {
						AMSG("");
						return -1;
					}
				}
				break;
			case 300:
				/* const int five = 5;
				 * or
				 * u_int8 rpc_vers = 5;
				 * or
				 * three = 3,
				 * or
				 * ten = 10 }
				 */
				bp = buf;
				while (isspace((*bp = fgetc(in->in)))) {
					;
				}
				do {
					if (!isspace(*bp)) {
						mark = bp + 1;
					}
					bp++;
				} while (bp < blim && (*bp = fgetc(in->in)) != ';' && *bp != ',' && *bp != '}');
				if (*bp == '}') {
					ungetc('}', in->in);
				}
				*mark = '\0';
				sym->value = dupstr(buf, idl->al);
				return 1;
			case 500:
/* case 1: */
				if (ch == ':') {
					state = 0;
				} else {
					hashmap_put(&sym->attrs, "case", dupstr(tok, idl->al));
				}
				break;
			case 0:
				if (ch == '}') {
					return 3;
				} else if (ch == '=') {
					state = 600;
					break;
				} else if (ch == ';') {
					if (hashmap_get(&sym->attrs, "default")) {
						return 1;
					}
					return 0;
				}

				if (strcmp(tok, "interface") == 0) {
					sym->flags |= FLAGS_INTERFACE;
					state = 10;
				} else if (strcmp(tok, "typedef") == 0) {
					sym->flags |= FLAGS_TYPEDEFD;
				} else if (ch == '[') {
					state = 100;
				} else if (strcmp(tok, "import") == 0) {
					state = 200;
				} else if (strcmp(tok, "const") == 0) {
					sym->flags |= FLAGS_CONST;
					state = 1000;
				} else if (strcmp(tok, "union") == 0) {
					sym->flags |= FLAGS_UNION;
					state = 20;
				} else if (strcmp(tok, "struct") == 0) {
					sym->flags |= FLAGS_STRUCTURE;
					state = 30;
				} else if (strcmp(tok, "enum") == 0) {
					sym->flags |= FLAGS_ENUM | FLAGS_PRIMATIVE;
					sym->out_type = "int";
					if (hashmap_get(&sym->attrs, "v1_enum")) {
						sym->ndr_type = "long";
					} else {
						sym->ndr_type = "short";
					}
					state = 40;
				} else if (strcmp(tok, "case") == 0) {
					state = 500;
				} else if (strcmp(tok, "default") == 0) {
					sym->flags |= FLAGS_MEMBER;
					sym->idl_type = dupstr("default", idl->al);
					hashmap_put(&sym->attrs, sym->idl_type, sym->idl_type);
					state = 500;
				} else {
					retok = 1;
					state = 1000;
				}
				break;
			case 10:
				sym->name = dupstr(tok, idl->al);
			case 20:
			case 30:
			case 40:
				bp = buf;
				switch (state) {
					case 10:
						bp += sprintf(buf, "%s", "interface");
						break;
					case 20:
						bp += sprintf(buf, "%s", "union");
						break;
					case 30:
						bp += sprintf(buf, "%s", "struct");
						break;
					case 40:
						bp += sprintf(buf, "%s", "enum");
						break;
				}
				if (ch != '{') {
					*bp++ = ' ';
					bp += sprintf(bp, "%s", tok);
				} else {
					retok = 1;
					bp += sprintf(bp, " _tag%x", (int)sym);
					sym->noemit = !IS_ENUM(sym);
				}
				sym->idl_type = dupstr(buf, idl->al);
				state++;
				break;
			case 22:
/* switch(type name) - from union decl */
				if (ch == '{') {
					retok = 1;
					state = 31;
				} else if (ch == '(') {
					int i;

					for (i = 0; (ch = fgetc(in->in)) != ')' && ch != EOF; i++) {
						tok[i] = ch;
					}
					tok[i] = '\0';

					hashmap_put(&sym->attrs, dupstr("switch", idl->al), dupstr(tok, idl->al));
				}
				break;
			case 21:
				if (strcmp(tok, "switch") == 0) {
					state = 22;
					break;
				}
			case 11:
			case 31:
			case 41:
				if (ch != '{') {
					retok = 1;
					mark = NULL;
					state = 1001;
					break;
				}
				state = 44;
			case 44:

/* recursive calls to parse for operation parameters
 * and interface, struct and union members
 */
				do {
					struct sym *s = symnew(idl->al);
								/* -1 error
								 *  0 nothing parsed
								 *  1 parsed one
								 *  2 parsed one but no more follow
								 *  3 parsed right brace
								 *  4 parsed one but typedefd
								 */
					s->filename = sym->filename;
					s->parent = sym;
					n = parse(idl, in, s);
					if (n == -1) {
						AMSG("");
						return -1;
					} else if (n == 0 || n == 3) {
						symdel(s);
						if (n == 3 && IS_TYPEDEFD(sym)) return 4;
					} else if (n == 4) {
						char stok[TOKMAX];
						struct sym *ss = NULL;
						int m, sch = 0, sstate = 1;

						symaddmem(sym, s);

						do {
							if ((m = tokget(in, stok, stok + TOKMAX)) < 1) {
								AMSG("");
								return m;
							}
							sch = stok[0];
if (idl->verbose > 2) {
fprintf(stderr, "%60d:   - %s\n", sstate, stok);
}
							switch (sstate) {
								case 1:
									if (sch == ';') {
										n = 1;
										break;
									}
									ss = symnew(idl->al);
									symcopy(s, ss, idl->al);
									sstate = 2;
								case 2:
									if (sch == '*') {
										ss->ptr++;
										ss->noemit = 1;
									} else if (isalpha(sch)) {
										ss->name = dupstr(stok, idl->al);
										symaddmem(sym, ss);
										sstate = 1;
									} else if (sch != ',') {
										PMSG("invalid identifier: %s", stok);
										return -1;
									}
									break;
							}
						} while (n == 4);
					} else {
						symaddmem(sym, s);
					}
				} while (n == 1);
				state = 888;
				break;
			case 888:
				if (ch == ';') {
					return 1;
				}
				break;
			case 1000:
				bp = buf;
				mark = NULL;
				state = 1001;
			case 1001:
				if (ch == '[') { /* array dimensions */
					pop_state = 1001;
					state = 1002;
				} else if (ch == '(' || ch == ')' || ch == ';' || ch == '=' || ch == ',' || ch == '}') {
					*bp = '\0';
					if (strcmp(buf, "void") == 0 && sym->ptr == 0) {
						return 0;
					}
					if (!mark && (ch == '=' || ch == ',' || ch == '}')) { /* enum */
						sym->name = dupstr(buf, idl->al);
						sym->idl_type = sym->parent->ndr_type;
						if (ch == '}') {
							ungetc('}', in->in);
							return 1;
						}
					} else if (!mark) {
						PMSG("%d: invalid expression: %s", in->line, tok);
						return -1;
					} else {
						*mark++ = '\0';
						sym->name = dupstr(mark, idl->al);
						sym->idl_type = dupstr(buf, idl->al);
					}

					if (ch == '(') {
						sym->flags |= FLAGS_OPERATION;
						retok = 1;
						pop_state = 888;
						state = 44;
					} else if (ch == ')') {
						sym->flags |= FLAGS_PARAMETER;
						return 2;
					} else if (ch == ';') {
						if (IS_TYPEDEFD(sym) == 0) {
							sym->flags |= FLAGS_MEMBER;
						}
						return 1;
					} else if (ch == ',') {
						sym->flags |= FLAGS_PARAMETER;
						return 1;
					} else if (ch == '=') {
						retok = 1;
						state = 300;
					} else {
						retok = 1;
						state = pop_state;
					}
				} else if (ch == '*') {
					sym->ptr++;
				} else {
					if (bp > buf) {
						mark = bp;
						*bp++ = ' ';
					}
					n = str_copy(tok, tok + TOKMAX, bp, blim, -1);
					bp += n;
				}
				break;
			case 1002:
				if (ch == ']') {
					int size = stack_size(&stk);
					char *s;

					sym->flags |= FLAGS_FIXED;

					if (size == 1) {
						s = stack_pop(&stk);
						if (*s != '-') {
							hashmap_put(&sym->attrs, dupstr("size_is", idl->al), s);
						} else {
							sym->flags |= FLAGS_CONFORMANT;
						}
					} else if (size == 2) {
						s = stack_pop(&stk);
						if (*s != '-') {
							hashmap_put(&sym->attrs, dupstr("max_is", idl->al), s);
						}
						s = stack_pop(&stk);
						if (*s != '-') {
							hashmap_put(&sym->attrs, dupstr("min_is", idl->al), s);
						}
					}
					state = pop_state;
					break;
				} else if (ch == '*') {
					stack_push(&stk, "-");
				} else if (ch != '.') {
					stack_push(&stk, dupstr(tok, idl->al));
				}
				break;
		}
	}

	return 0;
}


