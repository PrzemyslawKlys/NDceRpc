#ifndef TOK_H
#define TOK_H

#define TOKMAX 512

typedef struct {
	FILE *in;
	int state;
	int line;
} TOKFILE;

TOKFILE *tok_fopen(const char *filename);
int tok_fclose(TOKFILE *tf);
int tokget(TOKFILE *in, char *dst, char *dlim);
int tokcat(TOKFILE *in, char *dst, char *dlim);

#endif /* TOK_H */
