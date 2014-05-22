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

package rpc.ncadg_ip_udp;

import java.io.IOException;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import java.util.Properties;

import rpc.Buffer;
import rpc.ConnectionlessEndpoint;
import rpc.Endpoint;
import rpc.ProviderException;
import rpc.RpcException;
import rpc.Transport;

import rpc.core.PresentationSyntax;

public class RpcTransport implements Transport {

    public static final String PROTOCOL = "ncadg_ip_udp";

    private static final String LOCALHOST;

    private Properties properties;

    private String host;

    private int port;

    private DatagramSocket socket;

    private boolean attached;

    static {
        String localhost = null;
        try {
            localhost = InetAddress.getLocalHost().getHostName();
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
            socket = new DatagramSocket();
            attached = true;
            return new ConnectionlessEndpoint(this, syntax);
        } catch (IOException ex) {
            try {
                close();
            } catch (Exception ignore) { }
            throw ex;
        }
    }

    public void close() throws IOException {
        try {
            if (socket != null) socket.close();
        } finally {
            attached = false;
            socket = null;
        }
    }

    public void send(Buffer buffer) throws IOException {
        if (!attached) throw new RpcException("Transport not attached.");
        socket.send(new DatagramPacket(buffer.getBuffer(), 0,
                buffer.getLength(), InetAddress.getByName(host), port));
    }

    public void receive(Buffer buffer) throws IOException {
        if (!attached) throw new RpcException("Transport not attached.");
        DatagramPacket packet = new DatagramPacket(buffer.getBuffer(), 0,
                buffer.getCapacity());
        socket.receive(packet);
        buffer.setLength(packet.getLength());
    }

    protected void parse(String address) throws ProviderException {
        if (address == null) {
            throw new ProviderException("Null address.");
        }
        if (!address.startsWith("ncadg_ip_udp:")) {
            throw new ProviderException("Not an ncadg_ip_udp address.");
        }
        address = address.substring(13);
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
        if ("".equals(server)) server = LOCALHOST;
        try {
            port = Integer.parseInt(address);
        } catch (Exception ex) {
            throw new ProviderException("Invalid port specifier.");
        }
        host = server;
    }

}
