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

package rpc.security.ntlm.jcifs;

import java.io.IOException;

import java.net.URL;

import java.util.Enumeration;
import java.util.Properties;

import jcifs.Config;
import jcifs.UniAddress;

import jcifs.netbios.NbtAddress;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbSession;

import jcifs.ntlmssp.Type1Message;
import jcifs.ntlmssp.Type2Message;
import jcifs.ntlmssp.Type3Message;

public class AuthenticationSource
        extends rpc.security.ntlm.AuthenticationSource {

    public byte[] createChallenge(Properties properties, Type1Message type1)
            throws IOException {
        // once netlogon is in, we will generate our own challenge.
        return SmbSession.getChallenge(getDomainController(properties));
    }

    public byte[] authenticate(Properties properties, Type2Message type2,
            Type3Message type3) throws IOException {
        UniAddress dc = getDomainController(properties);
        byte[] lmResponse = type3.getLMResponse();
        if (lmResponse == null) lmResponse = new byte[0];
        byte[] ntResponse = type3.getNTResponse();
        if (ntResponse == null) ntResponse = new byte[0];
        SmbSession.logon(dc,
                new NtlmPasswordAuthentication(type3.getDomain(),
                        type3.getUser(),
                        SmbSession.getChallenge(getDomainController(properties)),
                        lmResponse, ntResponse));
        // once netlogon is in, we will be able to get the session key.
        return null;
    }

    private UniAddress getDomainController(Properties properties)
            throws IOException {
        String domain = null;
        String domainController = null;
        String loadBalance = null;
        boolean balance = false;
        if (properties != null) {
            domain = properties.getProperty("rpc.ntlm.domain");
            if (domain == null) {
                domain = properties.getProperty("jcifs.smb.client.domain");
            }
            domainController = properties.getProperty(
                    "jcifs.http.domainController");
            loadBalance = properties.getProperty("jcifs.http.loadBalance");
        }
        if (domain == null) {
            domain = Config.getProperty("jcifs.smb.client.domain");
        }
        if (domainController == null) {
            domainController = Config.getProperty(
                    "jcifs.http.domainController");
        }
        if (domainController == null) {
            domainController = domain;
            if (loadBalance == null) {
                loadBalance = Config.getProperty("jcifs.http.loadBalance");
            }
            balance = Boolean.valueOf(loadBalance).booleanValue();
        }
        return balance ? new UniAddress(NbtAddress.getByName(
                domainController, 0x1c, null)) : UniAddress.getByName(
                        domainController, true);
    }

}
