#ifndef MIDLC_H
#define MIDLC_H

#include <stdlib.h>
#include <stdio.h>
#include <mba/hashmap.h>
#include <mba/stack.h>
#include <mba/linkedlist.h>
#include <mba/allocator.h>
#include "tok.h"

#ifdef _WIN32
#define SEP '\\'
#define PATH_MAX 1024
#else
#define SEP '/'
#endif

#define isident(ch) (isalnum(ch) || (ch) == '_')

extern const char *FLAGS; /* "ITCSPEMUAOpfci+xx" */

#define FLAGS_INTERFACE  0x0001
#define FLAGS_TYPEDEFD   0x0002
#define FLAGS_CONST      0x0004
#define FLAGS_STRUCTURE  0x0008
#define FLAGS_PARAMETER  0x0010
#define FLAGS_ENUM       0x0020
#define FLAGS_MEMBER     0x0040
#define FLAGS_UNION      0x0080
#define FLAGS_ANONYMOUS  0x0100
#define FLAGS_OPERATION  0x0200
#define FLAGS_PRIMATIVE  0x0400
#define FLAGS_FIXED      0x0800
#define FLAGS_CONFORMANT 0x1000
#define FLAGS_IMPORT     0x2000
#define FLAGS_ARRAY      (FLAGS_FIXED | FLAGS_CONFORMANT)
#define FLAGS_IMPORTED   0x8000
#define FLAGS_EXPANDED   0x80000

#define IS_INTERFACE(o) (((o)->flags & FLAGS_INTERFACE) != 0)
#define IS_TYPEDEFD(o) (((o)->flags & FLAGS_TYPEDEFD) != 0)
#define IS_CONST(o) (((o)->flags & FLAGS_CONST) != 0)
#define IS_STRUCTURE(o) (((o)->flags & FLAGS_STRUCTURE) != 0)
#define IS_PARAMETER(o) (((o)->flags & FLAGS_PARAMETER) != 0)
#define IS_ENUM(o) (((o)->flags & FLAGS_ENUM) != 0)
#define IS_MEMBER(o) (((o)->flags & FLAGS_MEMBER) != 0)
#define IS_UNION(o) (((o)->flags & FLAGS_UNION) != 0)
#define IS_ANONYMOUS(o) (((o)->flags & FLAGS_ANONYMOUS) != 0)
#define IS_OPERATION(o) (((o)->flags & FLAGS_OPERATION) != 0)
#define IS_PRIMATIVE(o) (((o)->flags & FLAGS_PRIMATIVE) != 0)
#define IS_FIXED(o) (((o)->flags & FLAGS_FIXED) != 0)
#define IS_CONFORMANT(o) (((o)->flags & FLAGS_CONFORMANT) != 0)
#define IS_IMPORT(o) (((o)->flags & FLAGS_IMPORT) != 0)
#define IS_ARRAY(o) (((o)->flags & FLAGS_ARRAY) != 0)
#define IS_EMBEDDED_CONFORMANT(o) (((o)->flags & (FLAGS_FIXED | FLAGS_CONFORMANT)) == (FLAGS_FIXED | FLAGS_CONFORMANT))
#define IS_EXPANDED(o) (((o)->flags & FLAGS_EXPANDED) != 0)
#define IS_IMPORTED(o) (((o)->flags & FLAGS_IMPORTED) != 0)

struct sym;

struct idl {
	int argc;
	char **argv;
	TOKFILE *in;
	FILE *out;
	char *outname;
	struct hashmap *syms;
	struct hashmap *macros;
	struct hashmap *consts;
	struct hashmap *tmp;
	int verbose;
	const char *type;
	struct allocator *al;
	int symid;
	int ptr_default;
	int opnum;
	int for_loop_redecl; /* prevent error: `_i' previously declared here */
	char *interface;
};

int idl_process_file(struct idl *idl, const char *filename, struct sym *sym);
int preprocess(struct idl *idl, const char *filename, char *cppfilename);
int parse(struct idl *idl, TOKFILE *in, struct sym *sym);
const char *path_filename(const char *path);

struct sym *get_descriminant(struct sym *u);
int mkoutname(char *outname, const char *basename, const char *suffix);
void strreplace(char *s, int from, int to);
void sp(FILE *stream, int n);
int print_comment(struct idl *idl, const char *comment, struct sym *sym, int indent, int depth);
int print(struct idl *idl, int indent, const char *fmt, ...);
char *dupstr(const char *s, struct allocator *al);
int sprint_sym(char *buf, struct sym *sym, int indent, int spacer);

int emit_imported_protos(struct idl *idl, struct sym *sym);
const char *convexpr(const char *expr, const char *prefix);
int emit_mem(struct idl *idl, struct sym *sym, int indent);
int emit_stub_jcifs(struct idl *idl, struct sym *sym);
int emit_stub_java(struct idl *idl, struct sym *sym);
int emit_hdr_c(struct idl *idl, struct sym *sym);
int emit_svr_stub_c(struct idl *idl, struct sym *sym);
int emit_marsh_proto(struct idl *idl, struct sym *sym, int enc, int nl);
int emit_params_marsh_proto(struct idl *idl, struct sym *sym, int enc, int out);
int emit_stub_samba(struct idl *idl, struct sym *sym);
int emit_struct(struct idl *idl, struct sym *sym, int indent);
int emit_encoder_fragment(struct idl *idl, struct sym *sym, int mode, int indent);
int emit_encoder(struct idl *idl, struct sym *sym, int indent);
int emit_params_encoder(struct idl *idl, struct sym *sym, int out, int indent);
int emit_decoder_fragment(struct idl *idl, struct sym *sym, int mode, int indent);
int emit_decoder(struct idl *idl, struct sym *sym, int indent);
int emit_params_decoder(struct idl *idl, struct sym *sym, int out, int indent);

#endif /* MIDLC_H */
