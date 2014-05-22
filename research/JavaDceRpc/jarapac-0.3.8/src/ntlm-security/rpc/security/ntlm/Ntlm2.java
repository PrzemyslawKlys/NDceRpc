package rpc.security.ntlm;

import java.io.IOException;

import rpc.ProviderException;
import rpc.Security;

import ndr.*;

public class Ntlm2 implements Security {

    public Ntlm2(boolean server, int flags, byte[] sessionKey,
            byte[] exchangedKey, boolean datagram)
            throws IOException {
        throw new ProviderException("NTLM2 is not yet implemented.");
    }

    public int getVerifierLength() {
        return 16;
    }

    public int getAuthenticationService() {
        return NtlmAuthentication.AUTHENTICATION_SERVICE_NTLM;
    }

    public int getProtectionLevel() {
        return PROTECTION_LEVEL_NONE;
    }

    public void processIncoming(NetworkDataRepresentation ndr, int index,
            int length, int verifierIndex) throws IOException {
        throw new ProviderException("NTLM2 is not yet implemented.");
    }

    public void processOutgoing(NetworkDataRepresentation ndr, int index,
            int length, int verifierIndex) throws IOException {
        throw new ProviderException("NTLM2 is not yet implemented.");
    }

}
