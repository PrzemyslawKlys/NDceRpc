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

package jcifs.smb;

import java.io.IOException;

import java.net.UnknownHostException;

import java.util.Properties;

import jcifs.Config;

import jcifs.netbios.NbtAddress;

import ndr.NdrBuffer;

import rpc.ConnectionOrientedEndpoint;
import rpc.Endpoint;
import rpc.ProviderException;
import rpc.RpcException;
import rpc.Transport;

import rpc.core.PresentationSyntax;

public class RpcTransport implements Transport {

    public static final String PROTOCOL = "ncacn_np";

    private static final String LOCALHOST;

    private static final int RPC_PIPE_TYPE = ((SmbNamedPipe.PIPE_TYPE_RDWR |
            SmbNamedPipe.PIPE_TYPE_TRANSACT) & 0xff0000) | SmbFile.O_EXCL;

    private String address;

    private Properties properties;

    private SmbNamedPipe pipe;

    private int writeSize;

    private int readSize;

    private boolean attached;

    static {
        String localhost = null;
        try {
            localhost = NbtAddress.getLocalHost().getHostName();
        } catch (UnknownHostException ex) { }
        LOCALHOST = localhost;
    }

    public RpcTransport(String address, Properties properties)
            throws ProviderException {
        this.properties = properties;
        parse(address);
    }

    public String getProtocol() {
        return PROTOCOL;
    }

    public Properties getProperties() {
        return properties;
    }

    public Endpoint attach(PresentationSyntax syntax) throws IOException {
        if (attached) throw new RpcException("Transport already attached.");
        try {
            if (pipe == null) {
                pipe = new SmbNamedPipe(address, RPC_PIPE_TYPE);
            }
            pipe.open(RPC_PIPE_TYPE, SmbFile.ATTR_NORMAL, 0);
            writeSize = Math.min(pipe.tree.session.transport.snd_buf_size - 70,
                    pipe.tree.session.transport.server.maxBufferSize - 70);
            readSize = Math.min(pipe.tree.session.transport.rcv_buf_size - 70,
                    pipe.tree.session.transport.server.maxBufferSize - 70);
            attached = true;
            return new ConnectionOrientedEndpoint(this, syntax);
        } catch (IOException ex) {
            try {
                close();
            } catch (Exception ignore) { }
            throw ex;
        }
    }

    public void close() throws IOException {
        try {
            if (pipe != null) pipe.close();
        } finally {
            attached = false;
            pipe = null;
        }
    }

    public void send(NdrBuffer buffer) throws IOException {
        if (!attached) throw new RpcException("Transport not attached.");
        int length = buffer.getLength();
        byte[] transmitBuffer = buffer.getBuffer();
        int offset = 0;
        int count;
        SmbComWriteAndXResponse writeResp = new SmbComWriteAndXResponse();
        do {
            count = (length > writeSize) ? writeSize : length;
            pipe.send(new SmbComWriteAndX(pipe.fid, offset, length - count,
                    transmitBuffer, offset, count, null), writeResp);
            offset += writeResp.count;
            length -= writeResp.count;
        } while (length > 0);
    }

    public void receive(NdrBuffer buffer) throws IOException {
        if (!attached) throw new RpcException("Transport not attached.");
        byte[] receiveBuffer = buffer.getBuffer();
        SmbComReadAndXResponse readResp =
                new SmbComReadAndXResponse(receiveBuffer, 0);
        readResp.responseTimeout = 0;
        int size; 
        int count;
        int length = receiveBuffer.length;
        do {
            size = length > readSize ? readSize : length;
            try {
                pipe.send(new SmbComReadAndX(pipe.fid, readResp.off, size,
                        null), readResp);
            } catch (SmbException ex) {
                if (ex.getNtStatus() != SmbException.ERROR_MORE_DATA) throw ex;
            }
            if ((count = readResp.dataLength) <= 0) break;
            readResp.off += count;
            length -= count;
        } while (length > 0 && count == size);
        buffer.setIndex(readResp.off);
    }

    protected void parse(String address) throws ProviderException {
        if (address == null) {
            throw new ProviderException("Null address.");
        }
        if (!address.startsWith("ncacn_np:")) {
            throw new ProviderException("Not an ncacn_np address.");
        }
        address = address.substring(9);
        int index = address.indexOf('[');
        if (index == -1) {
            throw new ProviderException("No port specifier present.");
        }
        String server = address.substring(0, index);
        address = address.substring(index + 1);
        index = address.indexOf(']');
        if (index == -1) {
            throw new ProviderException("Port specifier not terminated.");
        }
        address = address.substring(0, index);
        while (address.startsWith("\\")) address = address.substring(1);
        if (!address.regionMatches(true, 0, "PIPE", 0, 4)) {
            throw new ProviderException("Not a named pipe address.");
        }
        address = address.substring(4);
        while (address.startsWith("\\")) address = address.substring(1);
        if ("".equals(address)) throw new ProviderException("Empty port.");
        while (server.startsWith("\\")) server = server.substring(1);
        if ("".equals(server)) server = LOCALHOST;
        Properties properties = getProperties();
        if (properties != null) {
            String userInfo = properties.getProperty(
                    "rpc.ncacn_np.username");
            if (userInfo == null) {
                userInfo = Config.getProperty("jcifs.smb.client.username");
            }
            if (userInfo != null) {
                String domain = properties.getProperty(
                        "rpc.ncacn_np.domain");
                if (domain == null) {
                    domain = Config.getProperty("jcifs.smb.client.domain");
                }
                if (domain != null) userInfo = domain + ';' + userInfo;
                String password = properties.getProperty(
                        "rpc.ncacn_np.password");
                if (password == null) {
                    password = Config.getProperty("jcifs.smb.client.password");
                }
                if (password != null) userInfo += ':' + password;
            }
            if (userInfo != null) server = userInfo + '@' + server;
        }
        this.address = "smb://" + server + "/IPC$/" + address;
    }

}
