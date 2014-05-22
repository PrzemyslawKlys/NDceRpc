#include <stdlib.h>
#include <stdio.h>
#include <ctype.h>
#include <errno.h>
#include <mba/text.h>
#include <mba/msgno.h>
#include "tok.h"

TOKFILE *
tok_fopen(const char *filename)
{
	TOKFILE *tf;

	if ((tf = calloc(1, sizeof *tf)) == NULL) {
		PMNO(errno);
		return NULL;
	}
	if ((tf->in = fopen(filename, "r")) == NULL) {
		PMNF(errno, ": %s", filename);
		free(tf);
		return NULL;
	}
	tf->line = 1;

	return tf;
}
int
tok_fclose(TOKFILE *tf)
{
	if (tf->in) {
		fclose(tf->in);
	}
	free(tf);
	return 0;
}

static int
istokchar(int ch)
{
	switch (ch) {
		case '[': case ']': case '(': case ')':
		case ',': case '{': case '}': case ';':
		case '*': case '&': case '.': case ':':
		case '"':
			return 1;
	}
	return 0;
}

int
tokget(TOKFILE *in, char *dst, char *dlim)
{
	char *start = dst;
	int ch;

	if (in->state == -1) {
		*dst = '\0';
		return 0;
	}
	while (dst < dlim) {
		ch = fgetc(in->in);

		if (ch == '#') {
			while ((ch = fgetc(in->in)) != '\n') {
				if (ch == EOF) {
					*dst = '\0';
					return 0;
				}
			}
		} else if (ch == '\n') {
			in->line++;
		}

		switch (in->state) {
			case 0:
				if (ch == EOF) {
					in->state = -1;
					*dst = '\0';
					return 0;
				} else if (isspace(ch)) {
					break;
				}
				in->state = 1;
			case 1:
				if (ch == EOF) {
					in->state = -1;
					*dst = '\0';
					return 0;
				} else if (istokchar(ch)) {
					*dst++ = ch;
					*dst = '\0';
					in->state = 0; 
					return 1;
				}
				in->state = 2;
			case 2:
				if (isspace(ch) || istokchar(ch) || ch == EOF) {
					if (ch == EOF) {
						in->state = -1; 
					} else if (ch == '*' && (dst - start) == 1 && *start == '/') {
						in->state = 3; /* comment */
						dst = start;
						break;
					} else {
						ungetc(ch, in->in);
						in->state = 0; 
					}
					*dst = '\0';
					return dst - start;
				}
				*dst++ = ch;
				break;
			case 3:
				if (ch == '*') {
					in->state = 4;
				}
				break;
			case 4:
				if (ch != '*') {
					in->state = ch != '/' ? 3 : 0;
				}
				break;
			default:
				errno = EINVAL;
				PMNF(errno, ": invalid TOKFILE state: %d", in->state);
				in->state = -1; 
				return -1;
		}
	}
	if (dst == dlim) {
		errno = ERANGE;
		PMNO(errno);
		return -1;
	}

	return 0;
}
int
tokcat(TOKFILE *in, char *dst, char *dlim)
{
	int n;

	n = str_length(dst, dlim - 1);
	dst[n++] = ' ';
	if ((n = tokget(in, dst + n, dlim)) == -1) {
		AMSG("");
		return -1;
	}

	return n;
}
