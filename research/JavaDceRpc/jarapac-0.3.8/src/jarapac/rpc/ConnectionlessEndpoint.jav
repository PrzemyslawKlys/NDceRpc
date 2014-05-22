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

import java.util.Properties;

import rpc.core.PresentationSyntax;
import rpc.core.UUID;

import ndr.*;

import rpc.pdu.FaultClPdu;
import rpc.pdu.RequestClPdu;
import rpc.pdu.RejectPdu;
import rpc.pdu.ResponseClPdu;

public class ConnectionlessEndpoint implements Endpoint {

    public static final String DATAGRAM_CONTEXT = "rpc.datagramContext";

    protected DatagramContext context;

    private Transport transport;

    private PresentationSyntax syntax;

    private int sequenceNumber;

    public ConnectionlessEndpoint(Transport transport,
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

    public void call(int semantics, UUID object, int opnum,
            InputParameters input, OutputParameters output) throws IOException {
        RequestClPdu request = new RequestClPdu();
        request.setSequenceNumber(sequenceNumber++);
        Buffer buffer = new Buffer(1024);
        NetworkDataRepresentation ndr = new NetworkDataRepresentation(buffer);
        input.write(ndr);
        request.setStub(buffer.copy());
        request.setBodyLength(buffer.getLength());
        request.setObject(object);
        request.setSyntax(getSyntax());
        request.setOpnum(opnum);
        if ((semantics & MAYBE) != 0) {
            request.setFlag1(ConnectionlessPdu.PFC1_MAYBE, true);
        }
        if ((semantics & IDEMPOTENT) != 0) {
            request.setFlag1(ConnectionlessPdu.PFC1_IDEMPOTENT, true);
        }
        if ((semantics & BROADCAST) != 0) {
            request.setFlag1(ConnectionlessPdu.PFC1_BROADCAST, true);
        }
        send(request);
        if (request.getFlag1(ConnectionlessPdu.PFC1_MAYBE)) return;
        ConnectionlessPdu reply = receive();
        // all other response PDUs handled by the context.
        if (reply instanceof ResponseClPdu) {
            ndr.setFormat(reply.getFormat());
            ndr.setBuffer(new Buffer(((ResponseClPdu) reply).getStub()));
            output.read(ndr);
        } else if (reply instanceof FaultClPdu) {
            FaultClPdu fault = (FaultClPdu) reply;
            throw new FaultException("Received fault.", fault.body.status);
        } else if (reply instanceof RejectPdu) {
            RejectPdu rejection = (RejectPdu) reply;
            throw new FaultException("Received rejection.",
                    rejection.body.status);
        } else {
            throw new RpcException("Received unexpected PDU from server.");
        }
    }

    protected void send(ConnectionlessPdu request) throws IOException {
        connect();
        context.transmit(request, getTransport());
    }

    protected ConnectionlessPdu receive() throws IOException {
        return context.receive(getTransport());
    }

    public void detach() throws IOException {
        context = null;
        getTransport().close();
    }

    private void connect() throws IOException {
        if (context != null) return;
        try {
            context = createContext();
            context.init(getTransport().getProperties());
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

    protected DatagramContext createContext() throws ProviderException {
        Properties properties = getTransport().getProperties();
        if (properties == null) return new BasicDatagramContext();
        String context = properties.getProperty(DATAGRAM_CONTEXT);
        if (context == null) return new BasicDatagramContext();
        try {
            return (DatagramContext) Class.forName(context).newInstance();
        } catch (Exception ex) {
            throw new ProviderException(ex.getMessage());
        }
    }

}
