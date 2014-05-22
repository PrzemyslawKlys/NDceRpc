#include <stdlib.h>
#include <errno.h>
#include <string.h>

#include <encdec.h>
#include <mba/msgno.h>

#include "rpc.h"
#include "lsarpc.h"

int
unicode_string_create(unicode_string *ustr, const char *str)
{
	char buf[1024], *dst = buf;

	if (enc_mbscpy(str, &dst, "UCS-2LE") == -1) {
		AMSG("");
		return -1;
	}

	ustr->maximum_length = dst - buf;
	ustr->length = ustr->maximum_length - 2;
	ustr->buffer = malloc(ustr->maximum_length);
	memcpy(ustr->buffer, buf, ustr->maximum_length);

	return 0;
}

int
LsarClose(void *_context, policy_handle *handle)
{
	(void)_context;
	(void)handle;
	return 0;
}

int
LsarLookupSids(void *_context,
            policy_handle *handle,
            lsa_SidArray *sids,
            lsa_RefDomainList *domains,
            lsa_TransNameArray *names,
            uint16_t level,
            uint32_t *count)
{
	uint32_t i;

	printf("num_sids: %d level: %d\n", sids->num_sids, level);
	for (i = 0; i < sids->num_sids; i++) {
		uint8_t j;
		sid_t *sid = sids->sids[i].sid;

		printf("sid %d: count: %d", i, sid->sub_authority_count);
		for (j = 0; j < sid->sub_authority_count; j++) {
			printf(" %d", sid->sub_authority[j]);
		}
		printf("\n");
	}

	(void)_context;
	(void)handle;
	(void)domains;
	(void)names;
	(void)count;
	return 0;
}

int
LsarOpenPolicy(void *_context,
            char_t * system_name,
            LsaObjectAttributes *object_attributes,
            uint32_t desired_access,
            policy_handle *policy_handle)
{
	int context_mode = object_attributes->security_quality_of_service->context_mode;
	MSG("context: %p system_name: %s context_mode: %d desired_access: 0x%08x", _context, system_name, context_mode, desired_access);
	memcpy(policy_handle, "ThisIsThePolicyHandle", 20);
	return 0;
}

int
LsarQueryInformationPolicy(void *_context,
            policy_handle *handle,
            uint16_t level,
            LsaPolicyInfo *info)
{
	LsaDomainInfo *dinfo = (LsaDomainInfo *)info;

	dinfo->name.length = 10;
	if (unicode_string_create(&dinfo->name, "AMRS") == -1) {
		MSG("");
		return -1;
	}

	dinfo->sid = malloc(sizeof(sid_t) + 12);
	dinfo->sid->revision = 1;
	dinfo->sid->sub_authority_count = 4;
	memcpy(dinfo->sid->identifier_authority, "\0\0\0\0\0\5", 6);
	dinfo->sid->sub_authority[0] = 21;
	dinfo->sid->sub_authority[1] = 0x11111111;
	dinfo->sid->sub_authority[2] = 0x22222222;
	dinfo->sid->sub_authority[3] = 0x33333333;

	MSG("context: %p handle: %p level: %d info: %p", _context, handle, level, dinfo);

	return 0;
}
