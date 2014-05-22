/* Jarapac DCE/RPC Framework
 * Copyright (C) 2003  Eric Glass
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package rpc.security.ntlm;

import java.io.IOException;

import java.util.Properties;
import java.util.Random;

import jcifs.Config;

import jcifs.smb.NtlmPasswordAuthentication;

import jcifs.ntlmssp.NtlmFlags;
import jcifs.ntlmssp.Type1Message;
import jcifs.ntlmssp.Type2Message;
import jcifs.ntlmssp.Type3Message;

import jcifs.util.HMACT64;
import jcifs.util.MD4;

import rpc.ProviderException;
import rpc.Security;

public class NtlmAuthentication {

    public static final int AUTHENTICATION_SERVICE_NTLM = 10;

    private static final int LM_COMPATIBILITY =
            Config.getInt("jcifs.smb.lmCompatibility", 0);

    private static final boolean UNICODE_SUPPORTED =
            Config.getBoolean("jcifs.smb.client.useUnicode", true);

    private static final String OEM_ENCODING =
            Config.getProperty("jcifs.smb.client.codepage",
                    Config.getProperty("jcifs.encoding",
                            System.getProperty("file.encoding")));

    private static final int BASIC_FLAGS = NtlmFlags.NTLMSSP_NEGOTIATE_NTLM |
            NtlmFlags.NTLMSSP_NEGOTIATE_OEM |
                    NtlmFlags.NTLMSSP_NEGOTIATE_ALWAYS_SIGN |
                            (UNICODE_SUPPORTED ?
                                    NtlmFlags.NTLMSSP_NEGOTIATE_UNICODE : 0);

    private static final byte[] LM_CONSTANT = new byte[] {
        (byte) 0x4b, (byte) 0x47, (byte) 0x53, (byte) 0x21,
        (byte) 0x40, (byte) 0x23, (byte) 0x24, (byte) 0x25
    };

    private static final Random RANDOM = new Random();

    protected Properties properties;

    private NtlmPasswordAuthentication credentials;

    private AuthenticationSource authenticationSource;

    private boolean lanManagerKey;

    private boolean seal;

    private boolean sign;

    private boolean keyExchange;

    private int keyLength = 40;

    public NtlmAuthentication(Properties properties) {
        this.properties = properties;
        String domain = null;
        String user = null;
        String password = null;
        if (properties != null) {
            lanManagerKey = Boolean.valueOf(properties.getProperty(
                    "rpc.ntlm.lanManagerKey")).booleanValue();
            seal = Boolean.valueOf(properties.getProperty(
                    "rpc.ntlm.seal")).booleanValue();
            sign = seal ? true : Boolean.valueOf(properties.getProperty(
                    "rpc.ntlm.sign")).booleanValue();
            keyExchange = Boolean.valueOf(properties.getProperty(
                    "rpc.ntlm.keyExchange")).booleanValue();
            String keyLength = properties.getProperty("rpc.ntlm.keyLength");
            if (keyLength != null) {
                try {
                    this.keyLength = Integer.parseInt(keyLength);
                } catch (NumberFormatException ex) {
                    throw new IllegalArgumentException("Invalid key length: " +
                            keyLength);
                }
            }
            domain = properties.getProperty("rpc.ntlm.domain");
            user = properties.getProperty(Security.USERNAME);
            password = properties.getProperty(Security.PASSWORD);
        }
        credentials = new NtlmPasswordAuthentication(domain, user, password);
    }

    public Security createSecurity(boolean server, Type2Message type2,
            Type3Message type3) throws IOException {
        boolean datagram = type2.getFlag(
                NtlmFlags.NTLMSSP_NEGOTIATE_DATAGRAM_STYLE);
        int flags = datagram ? type3.getFlags() : type2.getFlags();
        if ((flags & (NtlmFlags.NTLMSSP_NEGOTIATE_SIGN |
                NtlmFlags.NTLMSSP_NEGOTIATE_SEAL)) == 0) {
            return null;
        }
        byte[] sessionKey = server ? getAuthenticationSource().authenticate(
                properties, type2, type3) : getSessionKey(type2, type3);
        if (sessionKey == null) return null;
        byte[] exchangedKey =
                ((flags & NtlmFlags.NTLMSSP_NEGOTIATE_KEY_EXCH) != 0) ?
                        type3.getSessionKey() : null;
        return type3.getFlag(NtlmFlags.NTLMSSP_NEGOTIATE_NTLM2) ? (Security)
                new Ntlm2(server, flags, sessionKey, exchangedKey, datagram) :
                (Security) new Ntlm1(flags, sessionKey, exchangedKey, datagram);
    }

    protected AuthenticationSource getAuthenticationSource() {
        if (authenticationSource != null) return authenticationSource;
        String sourceClass = (properties != null) ?
                properties.getProperty("rpc.ntlm.authenticationSource") : null;
        if (sourceClass == null) {
            return (authenticationSource =
                    AuthenticationSource.getDefaultInstance());
        }
        try {
            return (authenticationSource = (AuthenticationSource)
                    Class.forName(sourceClass).newInstance());
        } catch (Exception ex) {
            throw new IllegalArgumentException(
                    "Invalid authentication source: " + ex);
        }
    }

    private int getDefaultFlags() {
        int flags = BASIC_FLAGS;
        if (lanManagerKey) flags |= NtlmFlags.NTLMSSP_NEGOTIATE_LM_KEY;
        if (sign) flags |= NtlmFlags.NTLMSSP_NEGOTIATE_SIGN;
        if (seal) flags |= NtlmFlags.NTLMSSP_NEGOTIATE_SEAL;
        if (keyExchange) flags |= NtlmFlags.NTLMSSP_NEGOTIATE_KEY_EXCH;
        if (keyLength >= 56) flags |= NtlmFlags.NTLMSSP_NEGOTIATE_56;
        if (keyLength >= 128) flags |= NtlmFlags.NTLMSSP_NEGOTIATE_128;
        return flags;
    }

    private int adjustFlags(int flags) {
        if (UNICODE_SUPPORTED &&
                ((flags & NtlmFlags.NTLMSSP_NEGOTIATE_UNICODE) != 0)) {
            flags &= ~NtlmFlags.NTLMSSP_NEGOTIATE_OEM;
            flags |= NtlmFlags.NTLMSSP_NEGOTIATE_UNICODE;
        } else {
            flags &= ~NtlmFlags.NTLMSSP_NEGOTIATE_UNICODE;
            flags |= NtlmFlags.NTLMSSP_NEGOTIATE_OEM;
        }
        if (!lanManagerKey) flags &= ~NtlmFlags.NTLMSSP_NEGOTIATE_LM_KEY;
        if (!(sign || seal)) flags &= ~NtlmFlags.NTLMSSP_NEGOTIATE_SIGN;
        if (!seal) flags &= ~NtlmFlags.NTLMSSP_NEGOTIATE_SEAL;
        if (!keyExchange) flags &= ~NtlmFlags.NTLMSSP_NEGOTIATE_KEY_EXCH;
        if (keyLength < 128) flags &= ~NtlmFlags.NTLMSSP_NEGOTIATE_128;
        if (keyLength < 56) flags &= ~NtlmFlags.NTLMSSP_NEGOTIATE_56;
        // NTLM2 sign & seal not currently supported.
        flags &= ~NtlmFlags.NTLMSSP_NEGOTIATE_NTLM2;
        return flags;
    }

    public Type1Message createType1() throws IOException {
        int flags = getDefaultFlags();
        return new Type1Message(flags, Type1Message.getDefaultDomain(),
                Type1Message.getDefaultWorkstation());
    }

    public Type2Message createType2(Type1Message type1) throws IOException {
        int flags;
        if (type1 == null) {
            flags = getDefaultFlags();
        } else {
            flags = adjustFlags(type1.getFlags());
        }
        return new Type2Message(flags,
                getAuthenticationSource().createChallenge(properties, type1),
                        Type2Message.getDefaultDomain());
    }

    public Type3Message createType3(Type2Message type2) throws IOException {
        int flags = type2.getFlags();
        if ((flags & NtlmFlags.NTLMSSP_NEGOTIATE_DATAGRAM_STYLE) != 0) {
            flags = adjustFlags(flags);
        }
        byte[] challenge = type2.getChallenge();
        byte[] lmResponse = NtlmPasswordAuthentication.getPreNTLMResponse(
                credentials.getPassword(), challenge);
        byte[] ntResponse = NtlmPasswordAuthentication.getNTLMResponse(
                credentials.getPassword(), challenge);
        Type3Message type3 = new Type3Message(flags, lmResponse, ntResponse,
                credentials.getDomain(), credentials.getUsername(),
                        Type3Message.getDefaultWorkstation());
        if ((flags & NtlmFlags.NTLMSSP_NEGOTIATE_KEY_EXCH) != 0) {
            byte[] exchangedKey = new byte[16];
            RANDOM.nextBytes(exchangedKey);
            new ArcFour(getSessionKey(type2, type3), false).process(
                    exchangedKey, 0, 16, exchangedKey, 0);
            type3.setSessionKey(exchangedKey);
        }
        return type3;
    }

    private byte[] getSessionKey(Type2Message type2, Type3Message type3)
            throws IOException {
        int flags = type2.getFlag(NtlmFlags.NTLMSSP_NEGOTIATE_DATAGRAM_STYLE) ?
                type3.getFlags() : type2.getFlags();
        if (LM_COMPATIBILITY < 3) {
            return ((flags & NtlmFlags.NTLMSSP_NEGOTIATE_LM_KEY) != 0) ?
                    v1LmSessionKey(flags, type2, type3) :
                            v1UserSessionKey(flags, type2, type3);
        } else {
            return ((flags & NtlmFlags.NTLMSSP_NEGOTIATE_LM_KEY) != 0) ?
                    v2LmSessionKey(flags, type2, type3) :
                            v2UserSessionKey(flags, type2, type3);
        }
    }

    private byte[] v1LmSessionKey(int flags, Type2Message type2,
            Type3Message type3) throws IOException {
        if (LM_COMPATIBILITY == 2) {
            throw new ProviderException("LanManager session keys are " +
                    "incompatible with LmCompatibility = 2.");
        }
        byte[] response = type3.getLMResponse();
        if (response == null) {
            throw new ProviderException("LanManager session key " +
                    "requires the LM Response.");
        }
        String password = credentials.getPassword();
        if (password == null) password = "";
        byte[] lmHash = lmHash(password);
        for (int i = 8; i < 14; i++) lmHash[i] = (byte) 0xbd;
        byte[] sessionKey = new byte[16];
        new DES(DES.createKey(lmHash, 0)).process(response, 0, 8,
                sessionKey, 0);
        new DES(DES.createKey(lmHash, 7)).process(response, 0, 8,
                sessionKey, 8);
        return sessionKey;
    }

    private byte[] v1UserSessionKey(int flags, Type2Message type2,
            Type3Message type3) throws IOException {
        String password = credentials.getPassword();
        if (password == null) password = "";
        if (type3.getNTResponse() != null) {
            MD4 md4 = new MD4();
            byte[] key = md4.digest(md4.digest(password.getBytes(
                    "UnicodeLittleUnmarked")));
            if ((flags & NtlmFlags.NTLMSSP_NEGOTIATE_NTLM2) == 0) return key;
            HMACT64 hmac = new HMACT64(key);
            hmac.update(type2.getChallenge());
            hmac.update(type3.getLMResponse(), 0, 8);
            return hmac.digest();
        } else {
            byte[] lmHash = lmHash(password);
            for (int i = 8; i < 16; i++) lmHash[i] = (byte) 0;
            return lmHash;
        }
    }

    private byte[] v2LmSessionKey(int flags, Type2Message type2,
            Type3Message type3) throws IOException {
        throw new ProviderException("The algorithm for NTLMv2 " +
                "LanManager session keys is currently unknown.");
    }

    private byte[] v2UserSessionKey(int flags, Type2Message type2,
            Type3Message type3) throws IOException {
        byte[] response = type3.getNTResponse();
        if (response == null) return new byte[16]; // LMv2 key...? maybe.
        MD4 md4 = new MD4();
        String password = credentials.getPassword();
        if (password == null) password = "";
        HMACT64 hmac = new HMACT64(md4.digest(password.getBytes(
                "UnicodeLittleUnmarked")));
        hmac.update(type3.getUser().toUpperCase().getBytes(
                "UnicodeLittleUnmarked"));
        String domain = type3.getDomain();
        if (domain == null) domain = "";
        hmac = new HMACT64(hmac.digest(domain.toUpperCase().getBytes(
                "UnicodeLittleUnmarked")));
        hmac.update(type2.getChallenge());
        hmac.update(response, 16, response.length - 16);
        return hmac.digest(hmac.digest());
    }

    private static byte[] lmHash(String password) throws IOException {
        byte[] pwd = password.toUpperCase().getBytes(OEM_ENCODING);
        int length = pwd.length;
        if (length > 14) length = 14;
        byte[] hash = new byte[14];
        System.arraycopy(pwd, 0, hash, 0, length);
        pwd = hash;
        hash = new byte[16];
        new DES(DES.createKey(pwd, 0)).process(LM_CONSTANT, 0, 8, hash, 0);
        new DES(DES.createKey(pwd, 7)).process(LM_CONSTANT, 0, 8, hash, 8);
        return hash;
    }

}
