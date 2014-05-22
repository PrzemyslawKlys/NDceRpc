#include <stdlib.h>
#include <string.h>
#include <stdio.h>
#include <errno.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <unistd.h>
#include <mba/msgno.h>
#include "midlc.h"

int
file_copy(const char *from, const char *to)
{
	FILE *in, *out;
	int ch;

	if ((in = fopen(from, "rb")) == NULL) {
		PMNF(errno, ": %s", from);	
		return -1;
	}
	if ((out = fopen(to, "wb")) == NULL) {
		PMNF(errno, ": %s", to);	
		return -1;
	}

	while ((ch = fgetc(in)) != EOF) {
		fputc(ch, out);
	}

	fclose(in);
	fclose(out);

	return 0;
}

int
preprocess(struct idl *idl, const char *filename, char *cppfilename)
{
	const char *basn = path_filename(filename);
	pid_t pid;
	int status;

	snprintf(cppfilename, 512, "/tmp/%s", basn);

	if ((pid = fork()) == -1) {
		PMNO(errno);
		return -1;
	} else if (pid == 0) {
		char *args[512];
		int i, ai = 0;

		args[ai++] = "cpp";
		for (i = 1; i < idl->argc && ai < 511; i++) {
			if (strncmp(idl->argv[i], "-D", 2) == 0 ||
				strncmp(idl->argv[i], "-I", 2) == 0) {
				args[ai++] = idl->argv[i];
			}
		}

		args[ai++] = (char *)filename;
		args[ai++] = cppfilename;
		args[ai++] = NULL;

		execv("/usr/bin/cpp", args);
		/* should never reach here */
		PMNO(errno);
		return -1;
	}
	waitpid(pid, &status, 0);

	if (WIFEXITED(status) && (status = WEXITSTATUS(status))) {
		PMSG("non-zero exit status: %d", status);
		return -1;
	}

	return 0;
}

/* for win32 ...
	IN = filename              src\foo.idl
	TMPD = GetTempPath();      c:\temp
	BASN = basename(filename); foo.idl
	TMPN = "TMPD\BASN.c"       c:\temp\foo.idl.c 
	CopyFile(IN, TMPN, 1)
	CreateProcess("cl /P /TcBASN.c", cd("TMPD")
	wait
	f = fopen("TMPN")         c:\temp\foo.idl.c
	delete("TMPN")            c:\temp\foo.idl.c 
	delete("TMPD\BASN.i")     c:\temp\foo.idl.i
	return f

int
main(int argc, char *argv[])
{
	if (preprocess(argv[1], argc, argv) == -1) {
		MSG("");
		return EXIT_FAILURE;
	}

	return EXIT_SUCCESS;
}
*/
