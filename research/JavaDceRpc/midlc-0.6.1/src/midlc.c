#include <stdlib.h>
#include <string.h>
#include <stdio.h>
#include <stdarg.h>
#include <errno.h>
#include <limits.h>
#include <mba/text.h>
#include <mba/hashmap.h>
#include <mba/msgno.h>
#include "sym.h"
#include "midlc.h"

const char *FLAGS = "ITCSPEMUAOpfci+xx";
const char *PTR_TYPES = " upr";

#define NN(p) ((p) ? (p) : "-")
#define FILL(buf, bp, n) do { *(bp)++ = ' '; } while ((bp) < ((buf) + (n)))
#define ISIDENT(ch) (isalnum(ch) || (ch) == '_')

static const char *
ident_index(const char *expr, const char *ident)
{
	int state = 0, ei, ii = 0, ech;
	const char *ret = expr;

	for (ei = 0;; ei++) {
		ech = expr[ei];
		switch (state) {
			case 0:
				if (ISIDENT(ech) == 0) {
					break;          /* skip spaces, operators, ... etc */
				}
				state = 1;          /* start of identifier (or number) */
				ret = expr + ei;
				ii = 0;
			case 1:
				if (ident[ii] == '\0' && (ech == '\0' || ISIDENT(ech) == 0)) {
					return ret;                           /* found it! */
				} else if (ech == ident[ii++]) {
					break;
				}
				state = 2;                            /* doesn't match */
			case 2:
				if (ISIDENT(ech)) {    /* skip rest of the ident chars */
					break;
				}
				state = 0;
				break;
		}

		if (ech == '\0') {
			break;
		}
	}

	return NULL;
}
struct sym *
get_descriminant(struct sym *u)
{
	const char *switch_is = hashmap_get(&u->attrs, "switch_is");
	iter_t iter;
	struct sym *mem;

	linkedlist_iterate(&u->parent->mems, &iter);
	while ((mem = linkedlist_next(&u->parent->mems, &iter))) {
		if (ident_index(switch_is, mem->name)) {
			return mem;
		}
	}

	return NULL;
}
void
strreplace(char *s, int from, int to)
{
	if (s == NULL || *s == '\0') return;
	do {
		if (*s == from)
			*s = to;
	} while (*s++);
}
void
sp(FILE *stream, int n)
{
	while (n--) fputc(' ', stream);
}
int
print(struct idl *idl, int indent, const char *fmt, ...)
{
	va_list ap;
	int n;

	va_start(ap, fmt);
	sp(idl->out, indent);
	n = vfprintf(idl->out, fmt, ap);
	va_end(ap);

	return n;
}
int
print_comment(struct idl *idl, const char *comment, struct sym *sym, int indent, int depth)
{
	char buf[1024];

	if (idl->verbose < 2) {
		return 0;
	}

	sp(idl->out, indent);
	fprintf(idl->out, "/* %-30s depth: %d\n", comment, depth);
	sp(idl->out, indent);
	sprint_sym(buf, sym, 0, 10);
	buf[50] = '\0';
	fprintf(idl->out, " * %s\n", buf);
	sp(idl->out, indent);
	fputs(" */\n", idl->out);

	return 0;
}

int
sprint_sym(char *buf, struct sym *sym, int indent, int spacer)
{
	char *bp = buf, *key;
	int i, fill = spacer * 2;
	iter_t iter;

	bp += sprintf(bp, "%3d ", sym->id);

	for (i = 0; i < 15; i++) {
		if ((sym->flags & (1 << i))) {
			*bp++ = FLAGS[i];
		}
	}
	FILL(buf, bp, 9 + indent);
	bp += sprintf(bp, "%s", NN(sym->idl_type));
	FILL(buf, bp, fill); fill += spacer;
	bp += sprintf(bp, "%d ", sym->ptr);
	bp += sprintf(bp, "%c ", PTR_TYPES[sym->ptr_type]);
	bp += sprintf(bp, "%s", NN(sym->name));
	FILL(buf, bp, fill); fill += spacer;
	bp += sprintf(bp, "%s", NN(sym->out_type));
	FILL(buf, bp, fill); fill += spacer / 2;
	bp += sprintf(bp, "%s", NN(sym->ndr_type));
	FILL(buf, bp, fill); fill += spacer;
	bp += sprintf(bp, "%3d %3d %3d ", sym->ndr_size, sym->align, sym->offset);
	bp += sprintf(bp, "(%s) %d ", NN(sym->interface), IS_IMPORTED(sym));

	i = 0;
	hashmap_iterate(&sym->attrs, &iter);
	while ((key = hashmap_next(&sym->attrs, &iter))) {
		const char *val = hashmap_get(&sym->attrs, key);
		if (i++) {
			*bp++ = ',';
		}
		if (key == val) {
			bp += sprintf(bp, "%s", key);
		} else {
			bp += sprintf(bp, "%s=%s", key, val);
		}
	}

	return bp - buf;
}
int
print_tree(struct idl *idl, struct sym *sym, int indent)
{
	char buf[1024];
	iter_t iter;
	struct sym *mem;

	sprint_sym(buf, sym, indent, 14);
	fputs(buf, stderr);
if (*buf)
	fputc('\n', stderr);

	linkedlist_iterate(&sym->mems, &iter);
	while ((mem = linkedlist_next(&sym->mems, &iter))) {
		print_tree(idl, mem, indent + 2);
	}

	return 0;
}
const char *
path_filename(const char *path)
{
	const char *p, *e;
	int ch;

	if (!path) return NULL;

	p = e = path;
	while ((ch = *p++)) {
		if (ch == SEP && *p && *p != SEP) {
			e = p;
		}
	}

	return e;
}
char *
dupstr(const char *s, struct allocator *al)
{
	unsigned char *dst;
	str_copy_new(s, s + 0xFFFF, &dst, -1, al);
	return dst;
}
int
idl_process_file(struct idl *idl, const char *filename, struct sym *sym)
{
	TOKFILE *in_save = idl->in;
	char ppfilename[PATH_MAX + 1];

	if (preprocess(idl, filename, ppfilename) == -1) {
		AMSG("");
		return -1;
	}

	if ((idl->in = tok_fopen(ppfilename)) == NULL) {
		AMSG("");
		return -1;
	}

	sym->filename = filename;
	if (parse(idl, idl->in, sym) == -1) {
		AMSG("parse failure");
		tok_fclose(idl->in);
		return -1;
	}

	tok_fclose(idl->in);

	idl->symid = 1;
	idl->interface = sym->name;
	if (symexpand(idl, sym) == -1 ||
				inherit_iface(idl, sym, sym->name) == -1 ||
				symresolve(idl, sym) == -1) {
		AMSG("");
		return -1;
	}

	idl->in = in_save;

	return 0;
}
static int
run_one(struct idl *idl,
		struct sym *iface,
		const char *outname,
		int (*emit)(struct idl *, struct sym *))
{
	if ((idl->out = fopen(outname, "w")) == NULL) {
		PMNF(errno, ": %s", outname);
		return -1;
	}

	if (emit(idl, iface) == -1) {
		AMSG("");
		return -1;
	}

	fclose(idl->out);

	return 0;
}
int
mkoutname(char *outname, const char *basename, const char *suffix)
{
	int i, j;

	i = j = 0;
	while (basename[i] && (basename[i] != '.' || basename[i + 1] == '.')) {
		outname[j++] = basename[i++];
	}
	while (basename[i]) {
		if (basename[i] == '.' && basename[i + 1] != '.' && basename[i + 1] != '/') {
			/* reached suffix (and not '..' or './' */
			break;
		}
		outname[j++] = basename[i++];
	}
	strcpy(outname + j, suffix);

	return 0;
}
static int
run(int argc, char **argv,
		const char *filename,
		const char *outname,
		const char *type,
		const char *symtabpath,
		struct hashmap *macros,
		int verbose)
{
	struct idl idl;
	struct sym iface;
	unsigned char _outname[PATH_MAX];

	memset(&idl, 0, sizeof(idl));
	idl.argc = argc;
	idl.argv = argv;
	idl.type = type;
	idl.macros = macros;
	idl.verbose = verbose;
	idl.al = NULL;
	if ((idl.syms = hashmap_new(hash_str, cmp_str, NULL, idl.al)) == NULL ||
				(idl.consts = hashmap_new(hash_str, cmp_str, NULL, idl.al)) == NULL ||
				(idl.tmp = hashmap_new(hash_str, cmp_str, NULL, idl.al)) == NULL) {
		AMSG("");
		return -1;
	}
	if (symload(&idl, symtabpath) == -1) {
		if (errno != ENOENT || symload(&idl, path_filename(symtabpath)) == -1) {
			AMSG("");
			return -1;
		}
	}
                                            /* generate parse tree in iface */
	syminit(&iface, idl.al);
	if (idl_process_file(&idl, filename, &iface) == -1) {
		AMSG("");
		return -1;
	}

	if (idl.verbose > 1) {
		print_tree(&idl, &iface, 0);                     /* print everything */
	} else if (idl.verbose) {
		iter_t iter;
		struct sym *mem;
	
		linkedlist_iterate(&iface.mems, &iter);
		while ((mem = linkedlist_next(&iface.mems, &iter))) {
			if (IS_OPERATION(mem) == 0) {
				continue;
			}
			print_tree(&idl, mem, 0); /* only print operations and their params */
		}
	}
	if (idl.verbose)
		fprintf(stderr, " No Flg   Type            Ptr   Name      OutType       NdrType Siz Aln Off Attributes\n");

	mkoutname(_outname, outname ? outname : filename, "");
	idl.outname = dupstr(path_filename(_outname), NULL);

	if (strcmp(type, "jcifs") == 0) {
		mkoutname(_outname, outname ? outname : filename, ".java");
		if (run_one(&idl, &iface, _outname, emit_stub_jcifs) == -1) {
			AMSG("");
			return -1;
		}
	} else if (strcmp(type, "java") == 0) {
		mkoutname(_outname, outname ? outname : filename, ".java");
		if (run_one(&idl, &iface, _outname, emit_stub_java) == -1) {
			AMSG("");
			return -1;
		}
	} else if (*type == 's') {
		mkoutname(_outname, outname ? outname : filename, ".c");
		if (run_one(&idl, &iface, _outname, emit_stub_samba) == -1) {
			AMSG("");
			return -1;
		}
	} else if (*type == 'c') {
		mkoutname(_outname, outname ? outname : filename, ".h");
		if (run_one(&idl, &iface, _outname, emit_hdr_c) == -1) {
			AMSG("");
			return -1;
		}
		mkoutname(_outname, outname ? outname : filename, "_s.c");
		if (run_one(&idl, &iface, _outname, emit_svr_stub_c) == -1) {
			AMSG("");
			return -1;
		}
		mkoutname(_outname, outname ? outname : filename, "_c.c");
	}

	return 0;
}

int
main(int argc, char *argv[])
{
	char **args;
	char *type = "jcifs";
	int verbose = 0;
	char *filename = NULL, *outname = NULL;
	char symtabpath[PATH_MAX + 1], *sp;
	struct hashmap macros;

	if (argc < 2) {
usage:
		fprintf(stderr, "usage: %s [-v|-d] [-s <symtab>] [-t jcifs|java|samba|c] [-o outfile] [-Dmacro=defn] <filename>\n", argv[0]);
		return EXIT_FAILURE;
	}

	errno = 0;

	if (hashmap_init(&macros, 0, hash_text, cmp_text, NULL, NULL) == -1) {
		MMSG("");
		return EXIT_FAILURE;
	}

	args = argv;
	args++; argc--;

	sp = symtabpath;
#ifdef _DATADIR
	sp += sprintf(sp, "%s%c", _DATADIR, SEP);
#endif
	sprintf(sp, "symtab%s.txt", type);

	while (argc) {
		if (strcmp(*args, "-v") == 0) {
			verbose++;
		} else if (strcmp(*args, "-d") == 0) {
			verbose += 2;
		} else if (strcmp(*args, "-s") == 0) {
			args++; argc--;
			if (!argc) {
				MMSG("-s requires a symtab path");
				goto usage;
			}
			strncpy(symtabpath, *args, PATH_MAX);
		} else if (strcmp(*args, "-t") == 0) {
			args++; argc--;
			if (!argc) {
				MMSG("-t requires a type");
				goto usage;
			}

			sp = symtabpath;
#ifdef _DATADIR
			sp += sprintf(sp, "%s%c", _DATADIR, SEP);
#endif
			type = *args;
			sprintf(sp, "symtab%s.txt", type);
		} else if (strncmp(*args, "-D", 2) == 0) {
			char *p, *macro, *defn = NULL;

			p = macro = (*args) + 2;

			for ( ;; p++) {
				if (*p == '\0') {
					if (p == macro) {
						macro = NULL;
					} else if (defn == NULL) {
						defn = "1";
					}
					break;
				} else if (defn == NULL && isspace(*p)) {
					MMSG("invalid macro: %s", *args);
					goto usage;
				} else if (*p == '=') {
					if (p == macro) {
						MMSG("invalid macro: %s", *args);
						goto usage;
					}
					*p = '\0';
					defn = p + 1;
				}
			}
			if (macro && hashmap_put(&macros, macro, defn) == -1) {
				MMSG("");
				return EXIT_FAILURE;
			}
		} else if (strcmp(*args, "-o") == 0) {
			args++; argc--;
			if (!argc) {
				MMSG("-o requires a filename");
				goto usage;
			}
			outname = *args;
		} else if (filename || **args == '-') {
			MMSG("Invalid argument: %s", *args);
			goto usage;
		} else {
			filename = *args;
		}
		args++; argc--;
	}
	if (!filename) {
		MMSG("A filename must be specified");
		goto usage;
	}

	if (run(argc, argv, filename, outname, type, symtabpath, &macros, verbose) == -1) {
		MMSG("");
		return EXIT_FAILURE;
	}

	return EXIT_SUCCESS;
}

