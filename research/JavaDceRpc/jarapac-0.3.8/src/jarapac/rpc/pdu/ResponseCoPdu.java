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

package rpc.pdu;

import java.io.IOException;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

import rpc.ConnectionOrientedPdu;
import rpc.Fragmentable;

import ndr.*;

public class ResponseCoPdu extends ConnectionOrientedPdu
        implements Fragmentable {

    public static final int RESPONSE_TYPE = 0x02;

    private byte[] stub;

    private int allocationHint = 0;

    private int contextId = 0;

    private int cancelCount = 0;

    public int getType() {
        return RESPONSE_TYPE;
    }

    public byte[] getStub() {
        return stub;
    }

    public void setStub(byte[] stub) {
        this.stub = stub;
    }

    public int getAllocationHint() {
        return allocationHint;
    }

    public void setAllocationHint(int allocationHint) {
        this.allocationHint = allocationHint;
    }

    public int getContextId() {
        return contextId;
    }

    public void setContextId(int contextId) {
        this.contextId = contextId;
    }

    public int getCancelCount() {
        return cancelCount;
    }

    public void setCancelCount(int cancelCount) {
        this.cancelCount = cancelCount;
    }

    protected void readPdu(NetworkDataRepresentation ndr) {
        readHeader(ndr);
        readBody(ndr);
        readStub(ndr);
    }

    protected void writePdu(NetworkDataRepresentation ndr) {
        writeHeader(ndr);
        writeBody(ndr);
        writeStub(ndr);
    }

    protected void readBody(NetworkDataRepresentation ndr) {
        setAllocationHint(ndr.readUnsignedLong());
        setContextId(ndr.readUnsignedShort());
        setCancelCount(ndr.readUnsignedSmall());
    }

    protected void writeBody(NetworkDataRepresentation ndr) {
        ndr.writeUnsignedLong(getAllocationHint());
        ndr.writeUnsignedShort(getContextId());
        ndr.writeUnsignedSmall((short) getCancelCount());
    }

    protected void readStub(NetworkDataRepresentation ndr) {
        ndr.getBuffer().align(8);
        byte[] stub = null;
        int length = getFragmentLength() - ndr.getBuffer().getIndex();
        if (length > 0) {
            stub = new byte[length];
			ndr.readOctetArray(stub, 0, length);
        }
        setStub(stub);
    }

    protected void writeStub(NetworkDataRepresentation ndr) {
        ndr.getBuffer().align(8, (byte) 0);
        byte[] stub = getStub();
		if (stub != null) ndr.writeOctetArray(stub, 0, stub.length);
    }

    public Iterator fragment(int size) {
        byte[] stub = getStub();
        if (stub == null) {
            return Arrays.asList(new ResponseCoPdu[] { this }).iterator();
        }
        int stubSize = size - 24;
        if (stub.length <= stubSize) {
            return Arrays.asList(new ResponseCoPdu[] { this }).iterator();
        }
        return new FragmentIterator(stubSize);
    }

    public Fragmentable assemble(Iterator fragments) throws IOException {
        if (!fragments.hasNext()) {
            throw new IOException("No fragments available.");
        }
        try {
            ResponseCoPdu pdu = (ResponseCoPdu) fragments.next();
            byte[] stub = pdu.getStub();
            if (stub == null) stub = new byte[0];
            while (fragments.hasNext()) {
                ResponseCoPdu fragment = (ResponseCoPdu) fragments.next();
                byte[] fragmentStub = fragment.getStub();
                if (fragmentStub != null && fragmentStub.length > 0) {
                    byte[] tmp = new byte[stub.length + fragmentStub.length];
                    System.arraycopy(stub, 0, tmp, 0, stub.length);
                    System.arraycopy(fragmentStub, 0, tmp, stub.length,
                            fragmentStub.length);
                    stub = tmp;
                }
            }
            int length = stub.length;
            if (length > 0) {
                pdu.setStub(stub);
                pdu.setAllocationHint(length);
            } else {
                pdu.setStub(null);
                pdu.setAllocationHint(0);
            }
            pdu.setFlag(PFC_FIRST_FRAG, true);
            pdu.setFlag(PFC_LAST_FRAG, true);
            return pdu;
        } catch (Exception ex) {
            throw new IOException("Unable to assemble PDU fragments.");
        }
    }

    public Object clone() {
        try {
            return super.clone();
        } catch (Exception ex) {
            throw new IllegalStateException();
        }
    }

    private class FragmentIterator implements Iterator {

        private int stubSize;

        private int index = 0;

        public FragmentIterator(int stubSize) {
            this.stubSize = stubSize;
        }

        public boolean hasNext() {
            return index < stub.length;
        }

        public Object next() {
            if (index >= stub.length) throw new NoSuchElementException();
            ResponseCoPdu fragment = (ResponseCoPdu) ResponseCoPdu.this.clone();
            int allocation = stub.length - index;
            fragment.setAllocationHint(allocation);
            if (stubSize < allocation) allocation = stubSize;
            byte[] fragmentStub = new byte[allocation];
            System.arraycopy(stub, index, fragmentStub, 0, allocation);
            fragment.setStub(fragmentStub);
            int flags = getFlags() & ~(PFC_FIRST_FRAG | PFC_LAST_FRAG);
            if (index == 0) flags |= PFC_FIRST_FRAG;
            index += allocation;
            if (index >= stub.length) flags |= PFC_LAST_FRAG;
            fragment.setFlags(flags);
            return fragment;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

    }

}
