package rpc;

import java.io.IOException;

import java.util.Iterator;
import java.util.NoSuchElementException;

import rpc.core.AuthenticationVerifier;

import ndr.*;

import rpc.pdu.AlterContextPdu;
import rpc.pdu.AlterContextResponsePdu;
import rpc.pdu.Auth3Pdu;
import rpc.pdu.BindPdu;
import rpc.pdu.BindAcknowledgePdu;
import rpc.pdu.BindNoAcknowledgePdu;
import rpc.pdu.CancelCoPdu;
import rpc.pdu.FaultCoPdu;
import rpc.pdu.OrphanedPdu;
import rpc.pdu.RequestCoPdu;
import rpc.pdu.ResponseCoPdu;
import rpc.pdu.ShutdownPdu;

public class DefaultConnection implements Connection {

    protected NetworkDataRepresentation ndr;

    protected NdrBuffer transmitBuffer;

    protected NdrBuffer receiveBuffer;

    protected Security security;

    protected int contextId;

    public DefaultConnection() {
        this(ConnectionOrientedPdu.MUST_RECEIVE_FRAGMENT_SIZE,
                ConnectionOrientedPdu.MUST_RECEIVE_FRAGMENT_SIZE);
    }

    public DefaultConnection(int transmitLength, int receiveLength) {
        ndr = new NetworkDataRepresentation();
        transmitBuffer = new NdrBuffer(new byte[transmitLength], 0);
        receiveBuffer = new NdrBuffer(new byte[receiveLength], 0);
    }

    public void transmit(ConnectionOrientedPdu pdu, Transport transport)
            throws IOException {
        if (!(pdu instanceof Fragmentable)) {
            transmitFragment(pdu, transport);
            return;
        }
        Iterator fragments =
                ((Fragmentable) pdu).fragment(transmitBuffer.getCapacity());
        while (fragments.hasNext()) {
            transmitFragment((ConnectionOrientedPdu) fragments.next(),
                    transport);
        }
    }

    public ConnectionOrientedPdu receive(final Transport transport)
            throws IOException {
        final ConnectionOrientedPdu fragment = receiveFragment(transport);
        if (!(fragment instanceof Fragmentable) ||
                fragment.getFlag(ConnectionOrientedPdu.PFC_LAST_FRAG)) {
            return fragment;
        }
        return (ConnectionOrientedPdu) ((Fragmentable) fragment).assemble(
                new Iterator() {
            ConnectionOrientedPdu currentFragment = fragment;
            public boolean hasNext() {
                return (currentFragment != null);
            }
            public Object next() {
                if (currentFragment == null) {
                    throw new NoSuchElementException();
                }
                try {
                    return currentFragment;
                } finally {
                    if (currentFragment.getFlag(
                            ConnectionOrientedPdu.PFC_LAST_FRAG)) {
                        currentFragment = null;
                    } else {
                        try {
                            currentFragment = receiveFragment(transport);
                        } catch (Exception ex) {
                            throw new IllegalStateException();
                        }
                    }
                }
            }
            public void remove() {
                throw new UnsupportedOperationException();
            }
        });
    }

    protected void transmitFragment(ConnectionOrientedPdu fragment,
            Transport transport) throws IOException {
        transmitBuffer.reset();

        fragment.encode(ndr, transmitBuffer);

        processOutgoing();
        transport.send(transmitBuffer);
    }

    protected ConnectionOrientedPdu receiveFragment(Transport transport)
            throws IOException {
        transport.receive(receiveBuffer);
        processIncoming();
        receiveBuffer.setIndex(ConnectionOrientedPdu.TYPE_OFFSET);
        int type = receiveBuffer.dec_ndr_small();
        ConnectionOrientedPdu pdu = null;
        switch (type) {
        case AlterContextPdu.ALTER_CONTEXT_TYPE:
            pdu = new AlterContextPdu();
            break;
        case AlterContextResponsePdu.ALTER_CONTEXT_RESPONSE_TYPE:
            pdu = new AlterContextResponsePdu();
            break;
        case Auth3Pdu.AUTH3_TYPE:
            pdu = new Auth3Pdu();
            break;
        case BindPdu.BIND_TYPE:
            pdu = new BindPdu();
            break;
        case BindAcknowledgePdu.BIND_ACKNOWLEDGE_TYPE:
            pdu = new BindAcknowledgePdu();
            break;
        case BindNoAcknowledgePdu.BIND_NO_ACKNOWLEDGE_TYPE:
            pdu = new BindNoAcknowledgePdu();
            break;
        case CancelCoPdu.CANCEL_TYPE:
            pdu = new CancelCoPdu();
            break;
        case FaultCoPdu.FAULT_TYPE:
            pdu = new FaultCoPdu();
            break;
        case OrphanedPdu.ORPHANED_TYPE:
            pdu = new OrphanedPdu();
            break;
        case RequestCoPdu.REQUEST_TYPE:
            pdu = new RequestCoPdu();
            break;
        case ResponseCoPdu.RESPONSE_TYPE:
            pdu = new ResponseCoPdu();
            break;
        case ShutdownPdu.SHUTDOWN_TYPE:
            pdu = new ShutdownPdu();
            break;
        default:
            throw new IOException("Unknown PDU type: 0x" +
                    Integer.toHexString(type));
        }
        receiveBuffer.setIndex(0);
        pdu.decode(ndr, receiveBuffer);
        return pdu;
    }

    protected void processIncoming() throws IOException {
        ndr.getBuffer().setIndex(ConnectionOrientedPdu.TYPE_OFFSET);
        switch (ndr.readUnsignedSmall()) {
        case BindAcknowledgePdu.BIND_ACKNOWLEDGE_TYPE:
        case AlterContextResponsePdu.ALTER_CONTEXT_RESPONSE_TYPE:
        case BindPdu.BIND_TYPE:
        case AlterContextPdu.ALTER_CONTEXT_TYPE:
        case Auth3Pdu.AUTH3_TYPE:
/* FIXME
            incomingRebind(detachAuthentication());
*/
            break;
        case FaultCoPdu.FAULT_TYPE:
        case ResponseCoPdu.RESPONSE_TYPE:
        case RequestCoPdu.REQUEST_TYPE:
        case CancelCoPdu.CANCEL_TYPE:
        case OrphanedPdu.ORPHANED_TYPE:
//            if (security != null) verifyAndUnseal();
            break;
        case BindNoAcknowledgePdu.BIND_NO_ACKNOWLEDGE_TYPE:
        case ShutdownPdu.SHUTDOWN_TYPE:
            return;
        default:
            throw new RpcException("Invalid incoming PDU type.");
        }
    }

    protected void processOutgoing() throws IOException {
        ndr.getBuffer().setIndex(ConnectionOrientedPdu.TYPE_OFFSET);
        switch (ndr.readUnsignedSmall()) {
        case BindPdu.BIND_TYPE:
        case AlterContextPdu.ALTER_CONTEXT_TYPE:
        case Auth3Pdu.AUTH3_TYPE:
        case BindAcknowledgePdu.BIND_ACKNOWLEDGE_TYPE:
        case AlterContextResponsePdu.ALTER_CONTEXT_RESPONSE_TYPE:
/* FIXME
            AuthenticationVerifier verifier = outgoingRebind();
            if (verifier != null) attachAuthentication(verifier);
*/
            break;
        case RequestCoPdu.REQUEST_TYPE:
        case CancelCoPdu.CANCEL_TYPE:
        case OrphanedPdu.ORPHANED_TYPE:
        case FaultCoPdu.FAULT_TYPE:
        case ResponseCoPdu.RESPONSE_TYPE:
//            if (security != null) signAndSeal();
            break;
        case BindNoAcknowledgePdu.BIND_NO_ACKNOWLEDGE_TYPE:
        case ShutdownPdu.SHUTDOWN_TYPE:
            return;
        default:
            throw new RpcException("Invalid outgoing PDU type.");
        }
    }

    protected void setSecurity(Security security) {
        this.security = security;
    }

    private void attachAuthentication(AuthenticationVerifier verifier)
                throws IOException {
        try {
            NdrBuffer buffer = ndr.getBuffer();
            int length = buffer.getLength();
            buffer.setIndex(length);
            verifier.encode(ndr, buffer);
            length = buffer.getLength();
            buffer.setIndex(ConnectionOrientedPdu.FRAG_LENGTH_OFFSET);
            ndr.writeUnsignedShort(length);
            ndr.writeUnsignedShort(verifier.body.length);
        } catch (Exception ex) {
            throw new IOException("Error attaching authentication to PDU: " +
                    ex.getMessage());
        }
    }

    private AuthenticationVerifier detachAuthentication() throws IOException {
        try {
            NdrBuffer buffer = ndr.getBuffer();
            buffer.setIndex(ConnectionOrientedPdu.AUTH_LENGTH_OFFSET);
            int length = ndr.readUnsignedShort(); // auth body size
            int index = buffer.getLength() - length - 8; // 8 = auth header size
            buffer.setIndex(index);
            AuthenticationVerifier verifier =
                    new AuthenticationVerifier(length);
            verifier.decode(ndr, buffer);
            buffer.setIndex(index + 2); // auth padding
            length = index - ndr.readUnsignedSmall();
            buffer.setIndex(ConnectionOrientedPdu.FRAG_LENGTH_OFFSET);
            ndr.writeUnsignedShort(length);
            ndr.writeUnsignedShort(0);
            buffer.setIndex(length);
            return verifier;
        } catch (Exception ex) {
            throw new IOException("Error stripping authentication from PDU: " +
                    ex);
        }
    }

/* FIXME
    private void signAndSeal() throws IOException {
        int protectionLevel = security.getProtectionLevel();
        if (protectionLevel < Security.PROTECTION_LEVEL_INTEGRITY) return;
        int verifierLength = security.getVerifierLength();
        AuthenticationVerifier verifier = new AuthenticationVerifier(
                security.getAuthenticationService(), protectionLevel, contextId,
                        verifierLength);
        Buffer buffer = ndr.getBuffer();
        int length = buffer.getLength();
        buffer.setIndex(length);
        verifier.write(ndr);
        length = buffer.getLength();
        buffer.setIndex(ConnectionOrientedPdu.FRAG_LENGTH_OFFSET);
        ndr.writeUnsignedShort(length);
        ndr.writeUnsignedShort(verifierLength);
        int verifierIndex = length - verifierLength;
        length -= verifierLength + 8; // less verifier + header
        int index = ConnectionOrientedPdu.HEADER_LENGTH;
        buffer.setIndex(ConnectionOrientedPdu.TYPE_OFFSET);
        switch (ndr.readUnsignedSmall()) {
        case RequestCoPdu.REQUEST_TYPE:
            index += 8;
            buffer.setIndex(ConnectionOrientedPdu.FLAGS_OFFSET);
            if ((ndr.readUnsignedSmall() &
                    ConnectionOrientedPdu.PFC_OBJECT_UUID) != 0) {
                index += 16;
            }
            break;
        case FaultCoPdu.FAULT_TYPE:
            index += 16;
            break;
        case ResponseCoPdu.RESPONSE_TYPE:
            index += 8;
            break;
        case CancelCoPdu.CANCEL_TYPE:
        case OrphanedPdu.ORPHANED_TYPE:
            index = length;
            break;
        default:
            throw new IntegrityException("Not an authenticated PDU type.");
        }
        length -= index;
        security.processOutgoing(ndr, index, length, verifierIndex);
    }

    private void verifyAndUnseal() throws IOException {
        Buffer buffer = ndr.getBuffer();
        buffer.setIndex(ConnectionOrientedPdu.AUTH_LENGTH_OFFSET);
        int verifierLength = ndr.readUnsignedShort();
        if (verifierLength <= 0) return;
        int verifierIndex = buffer.getLength() - verifierLength;
        int length = verifierIndex - 8;
        int index = ConnectionOrientedPdu.HEADER_LENGTH;
        buffer.setIndex(ConnectionOrientedPdu.TYPE_OFFSET);
        switch (ndr.readUnsignedSmall()) {
        case RequestCoPdu.REQUEST_TYPE:
            index += 8;
            buffer.setIndex(ConnectionOrientedPdu.FLAGS_OFFSET);
            if ((ndr.readUnsignedSmall() &
                    ConnectionOrientedPdu.PFC_OBJECT_UUID) != 0) {
                index += 16;
            }
            break;
        case FaultCoPdu.FAULT_TYPE:
            index += 16;
            break;
        case ResponseCoPdu.RESPONSE_TYPE:
            index += 8;
            break;
        case CancelCoPdu.CANCEL_TYPE:
        case OrphanedPdu.ORPHANED_TYPE:
            index = length;
            break;
        default:
            throw new IntegrityException("Not an authenticated PDU type.");
        }
        length -= index;
        security.processIncoming(ndr, index, length, verifierIndex);
        buffer.setIndex(verifierIndex - 6); // auth padding field
        length = verifierIndex - ndr.readUnsignedSmall() - 8;
        buffer.setIndex(ConnectionOrientedPdu.FRAG_LENGTH_OFFSET);
        // "doctor" the PDU by removing the auth and padding
        ndr.writeUnsignedShort(length);
        ndr.writeUnsignedShort(0);
        buffer.setLength(length);
    }
*/

    protected void incomingRebind(AuthenticationVerifier verifier)
            throws IOException { }

    protected AuthenticationVerifier outgoingRebind() throws IOException {
        return null;
    }

}
