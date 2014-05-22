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

package rpc;

import java.io.IOException;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import rpc.core.PresentationContext;
import rpc.core.PresentationSyntax;
import rpc.core.UUID;

import ndr.*;

import rpc.pdu.FaultCoPdu;
import rpc.pdu.RequestCoPdu;
import rpc.pdu.ResponseCoPdu;
import rpc.pdu.ShutdownPdu;

public class ConnectionOrientedEndpoint implements Endpoint {

    public static final String CONNECTION_CONTEXT = "rpc.connectionContext";

    protected ConnectionContext context;

    private Transport transport;

    private PresentationSyntax syntax;

    private boolean bound;

    private int callId;

    public ConnectionOrientedEndpoint(Transport transport,
            PresentationSyntax syntax) {
        this.transport = transport;
        this.syntax = syntax;
    }

    public Transport getTransport() {
        return transport;
    }

    public PresentationSyntax getSyntax() {
        return syntax;
    }

    public void call(int semantics, UUID object, int opnum, NdrObject ndrobj) throws IOException {
        bind();
        RequestCoPdu request = new RequestCoPdu();
        request.setCallId(++callId);

        byte[] b = new byte[1024];
        NdrBuffer buffer = new NdrBuffer(b, 0);
        NetworkDataRepresentation ndr = new NetworkDataRepresentation();
        ndrobj.encode(ndr, buffer);
		byte[] stub = new byte[buffer.getLength()]; /* yuk */
		System.arraycopy(buffer.buf, 0, stub, 0, stub.length);
jcifs.util.Hexdump.hexdump(System.err, stub, 0, stub.length);

        request.setStub(stub);
        request.setAllocationHint(buffer.getLength());
        request.setOpnum(opnum);
        request.setObject(object);
        if ((semantics & MAYBE) != 0) {
            request.setFlag(ConnectionOrientedPdu.PFC_MAYBE, true);
        }
        send(request);
        if (request.getFlag(ConnectionOrientedPdu.PFC_MAYBE)) return;
        ConnectionOrientedPdu reply = receive();
        if (reply instanceof ResponseCoPdu) {
            ndr.setFormat(reply.getFormat());

            buffer = new NdrBuffer(((ResponseCoPdu) reply).getStub(), 0);
jcifs.util.Hexdump.hexdump(System.err, buffer.buf, 0, buffer.buf.length);
            ndrobj.decode(ndr, buffer);

        } else if (reply instanceof FaultCoPdu) {
            FaultCoPdu fault = (FaultCoPdu) reply;
            throw new FaultException("Received fault.", fault.getStatus(),
                    fault.getStub());
        } else if (reply instanceof ShutdownPdu) {
            throw new RpcException("Received shutdown request from server.");
        } else {
            throw new RpcException("Received unexpected PDU from server.");
        }
    }

    protected void rebind() throws IOException {
        bound = false;
        bind();
    }

    protected void bind() throws IOException {
        if (bound) return;
        if (context != null) {
            bound = true;
            try {
                ConnectionOrientedPdu pdu = context.alter(
                        new PresentationContext(0, getSyntax()));
                if (pdu != null) send(pdu);
                while (!context.isEstablished()) {
                    if ((pdu = context.accept(receive())) != null) send(pdu);
                }
            } catch (IOException ex) {
                bound = false;
                throw ex;
            } catch (RuntimeException ex) {
                bound = false;
                throw ex;
            } catch (Exception ex) {
                bound = false;
                throw new IOException(ex.getMessage());
            }
        } else {
            connect();
        }
    }

    protected void send(ConnectionOrientedPdu request) throws IOException {
        bind();
        context.getConnection().transmit(request, getTransport());
    }

    protected ConnectionOrientedPdu receive() throws IOException {
        return context.getConnection().receive(getTransport());
    }

    public void detach() throws IOException {
        bound = false;
        context = null;
        getTransport().close();
    }

    private void connect() throws IOException {
        bound = true;
        try {
            context = createContext();
            ConnectionOrientedPdu pdu = context.init(
                    new PresentationContext(0, getSyntax()),
                            getTransport().getProperties());
            if (pdu != null) send(pdu);
            while (!context.isEstablished()) {
                if ((pdu = context.accept(receive())) != null) send(pdu);
            }
        } catch (IOException ex) {
            try {
                detach();
            } catch (IOException ignore) { }
            throw ex;
        } catch (RuntimeException ex) {
            try {
                detach();
            } catch (IOException ignore) { }
            throw ex;
        } catch (Exception ex) {
            try {
                detach();
            } catch (IOException ignore) { }
            throw new IOException(ex.getMessage());
        }
    }

    protected ConnectionContext createContext() throws ProviderException {
        Properties properties = getTransport().getProperties();
        if (properties == null) return new BasicConnectionContext();
        String context = properties.getProperty(CONNECTION_CONTEXT);
        if (context == null) return new BasicConnectionContext();
        try {
            return (ConnectionContext) Class.forName(context).newInstance();
        } catch (Exception ex) {
            throw new ProviderException(ex.getMessage());
        }
    }

}
