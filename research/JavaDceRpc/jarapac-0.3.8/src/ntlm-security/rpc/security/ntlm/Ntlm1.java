package rpc.security.ntlm;

import java.io.IOException;

import java.util.zip.CRC32;

import jcifs.ntlmssp.NtlmFlags;

import rpc.IntegrityException;
import rpc.Security;

import ndr.*;

public class Ntlm1 implements NtlmFlags, Security {

    private static final int NTLM1_VERIFIER_LENGTH = 16;

    private ArcFour cipher;

    private int protectionLevel;

    private int sequence;

    public Ntlm1(int flags, byte[] sessionKey, byte[] exchangedKey,
            boolean datagram) throws IOException {
        if (exchangedKey != null) {
            new ArcFour(sessionKey, datagram).process(exchangedKey, 0, 16,
                    (sessionKey = new byte[16]), 0);
        }
        protectionLevel = ((flags & NTLMSSP_NEGOTIATE_SEAL) != 0) ?
                PROTECTION_LEVEL_PRIVACY : PROTECTION_LEVEL_INTEGRITY;
        // key weakening for lanmanager session key
        if ((flags & NTLMSSP_NEGOTIATE_LM_KEY) != 0) {
            byte[] key = new byte[8];
            System.arraycopy(sessionKey, 0, key, 0, 8);
            if ((flags & NTLMSSP_NEGOTIATE_56) != 0) {
                key[7] = (byte) 0xa0;
            } else {
                key[5] = (byte) 0xe5;
                key[6] = (byte) 0x38;
                key[7] = (byte) 0xb0;
            }
            sessionKey = key;
        }
        cipher = new ArcFour(sessionKey, datagram);
    }

    public int getVerifierLength() {
        return NTLM1_VERIFIER_LENGTH;
    }

    public int getAuthenticationService() {
        return NtlmAuthentication.AUTHENTICATION_SERVICE_NTLM;
    }

    public int getProtectionLevel() {
        return protectionLevel;
    }

    public void processIncoming(NetworkDataRepresentation ndr, int index,
            int length, int verifierIndex) throws IOException {
        try {
            NdrBuffer buffer = ndr.getBuffer();
            byte[] data = buffer.getBuffer();
            if (getProtectionLevel() == PROTECTION_LEVEL_PRIVACY) {
                cipher.process(data, index, length, data, index);
            }
            CRC32 crc32 = new CRC32();
            crc32.update(data, index, length);
            int crc = (int) crc32.getValue();
            cipher.process(data, verifierIndex + 4, 12, data,
                    verifierIndex + 4);
            buffer.setIndex(verifierIndex + 8);
            if ((int) ndr.readUnsignedLong() != crc) {
                throw new IntegrityException("CRC check failed to verify.");
            }
            if (ndr.readUnsignedLong() != sequence++) {
                throw new IntegrityException("Message out of sequence.");
            }
        } catch (IOException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new IntegrityException("General error: " + ex.getMessage());
        }
    }

    public void processOutgoing(NetworkDataRepresentation ndr, int index,
            int length, int verifierIndex) throws IOException {
        try {
            NdrBuffer buffer = ndr.getBuffer();
            byte[] data = buffer.getBuffer();
            CRC32 crc32 = new CRC32();
            crc32.update(data, index, length);
            long crc = crc32.getValue();
            if (getProtectionLevel() == PROTECTION_LEVEL_PRIVACY) {
                cipher.process(data, index, length, data, index);
            }
            buffer.setIndex(verifierIndex);
            ndr.writeUnsignedLong(1);
            index = buffer.getIndex();
            ndr.writeUnsignedLong(0);
            ndr.writeUnsignedLong((int)(crc & 0xFFFFFFFF));
            ndr.writeUnsignedLong(sequence++);
            cipher.process(data, index, 12, data, index);
        } catch (Exception ex) {
            throw new IntegrityException("General error: " + ex.getMessage());
        }
    }

}
