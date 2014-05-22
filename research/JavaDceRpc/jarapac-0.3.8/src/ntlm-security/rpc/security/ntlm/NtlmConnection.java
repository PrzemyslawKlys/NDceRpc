package rpc.security.ntlm;

import java.io.IOException;

import java.util.Properties;

import jcifs.ntlmssp.NtlmFlags;
import jcifs.ntlmssp.NtlmMessage;
import jcifs.ntlmssp.Type1Message;
import jcifs.ntlmssp.Type2Message;
import jcifs.ntlmssp.Type3Message;

import rpc.DefaultConnection;
import rpc.Security;

import ndr.*;

import rpc.core.AuthenticationVerifier;

public class NtlmConnection extends DefaultConnection {

    private static int contextSerial;

    private NtlmAuthentication authentication;

    protected Properties properties;

    private NtlmMessage ntlm;

    public NtlmConnection(Properties properties) {
        this.authentication = new NtlmAuthentication(properties);
        this.properties = properties;
    }

    public void setTransmitLength(int transmitLength) {
        transmitBuffer = new NdrBuffer(new byte[transmitLength], 0);
    }

    public void setReceiveLength(int receiveLength) {
        receiveBuffer = new NdrBuffer(new byte[receiveLength], 0);
    }

    protected void incomingRebind(AuthenticationVerifier verifier)
            throws IOException {
        switch (verifier.body[8]) {
        case 1:
            // server gets negotiate from client
            setSecurity(null);
            contextId = verifier.contextId;
            ntlm = new Type1Message(verifier.body);
            break;
        case 2:
            // client gets challenge from server
            ntlm = new Type2Message(verifier.body);
            break;
        case 3:
            // server gets authenticate from client
            Type2Message type2 = (Type2Message) ntlm; 
            setSecurity(authentication.createSecurity(true, type2,
                    (Type3Message) (ntlm = new Type3Message(verifier.body))));
            break;
        default:
            throw new IOException("Invalid NTLM message type.");
        }
    }

    protected AuthenticationVerifier outgoingRebind() throws IOException {
        if (ntlm == null || ntlm instanceof Type3Message) {
            // client sends negotiate to server
            setSecurity(null);
            synchronized (NtlmConnection.class) {
                contextId = ++contextSerial;
            }
            ntlm = authentication.createType1();
        } else if (ntlm instanceof Type1Message) {
            // server sends challenge to client
            ntlm = authentication.createType2((Type1Message) ntlm);
        } else if (ntlm instanceof Type2Message) {
            // client sends authenticate to server
            Type2Message type2 = (Type2Message) ntlm;
            setSecurity(authentication.createSecurity(false, type2,
                    (Type3Message) (ntlm = authentication.createType3(type2))));
        } else {
            throw new IOException("Unrecognized NTLM message.");
        }
        int protectionLevel = ntlm.getFlag(NtlmFlags.NTLMSSP_NEGOTIATE_SEAL) ?
            Security.PROTECTION_LEVEL_PRIVACY :
                ntlm.getFlag(NtlmFlags.NTLMSSP_NEGOTIATE_SIGN) ?
                    Security.PROTECTION_LEVEL_INTEGRITY :
                        Security.PROTECTION_LEVEL_CONNECT;
        return new AuthenticationVerifier(
                NtlmAuthentication.AUTHENTICATION_SERVICE_NTLM, protectionLevel,
                        contextId, ntlm.toByteArray());
    }

}
