#include <stdlib.h>
#include <string.h>
#include "midlc.h"

int
preprocess(struct idl *idl, const char *filename, char *cppfilename)
{
	strcpy(cppfilename, filename);
	return 0;
}
