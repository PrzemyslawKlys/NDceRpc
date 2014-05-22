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

import rpc.ConnectionlessPdu;
import rpc.Fragmentable;

import ndr.*;

public class RequestClPdu extends ConnectionlessPdu implements Fragmentable {

    public static final int REQUEST_TYPE = 0x00;

    private byte[] stub;

    public int getType() {
        return REQUEST_TYPE;
    }

    public byte[] getStub() {
        return stub;
    }

    public void setStub(byte[] stub) {
        this.stub = stub;
    }

    protected void readBody(NetworkDataRepresentation ndr) {
        byte[] stub = null;
        int length = getBodyLength();
        if (length > 0) {
            stub = new byte[length];
			ndr.readOctetArray(stub, 0, length);
        }
        setStub(stub);
    }

    protected void writeBody(NetworkDataRepresentation ndr) {
        byte[] stub = getStub();
		if (stub != null) ndr.writeOctetArray(stub, 0, stub.length);
    }

    public Iterator fragment(int size) {
        byte[] stub = getStub();
        if (stub == null) {
            return Arrays.asList(new RequestClPdu[] { this }).iterator();
        }
        int stubSize = size - HEADER_LENGTH;
        if (stub.length <= stubSize) {
            return Arrays.asList(new RequestClPdu[] { this }).iterator();
        }
        return new FragmentIterator(stubSize);
    }

    public Fragmentable assemble(Iterator fragments) throws IOException {
        if (!fragments.hasNext()) {
            throw new IOException("No fragments available.");
        }
        try {
            RequestClPdu pdu = (RequestClPdu) fragments.next();
            byte[] stub = pdu.getStub();
            if (stub == null) stub = new byte[0];
            while (fragments.hasNext()) {
                RequestClPdu fragment = (RequestClPdu) fragments.next();
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
            } else {
                pdu.setStub(null);
            }
            pdu.setFragmentNumber(0);
            pdu.setFlag1(PFC1_FRAG, false);
            pdu.setFlag1(PFC1_LAST_FRAG, false);
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

        private int fragmentNumber = 0;

        public FragmentIterator(int stubSize) {
            this.stubSize = stubSize;
        }

        public boolean hasNext() {
            return index < stub.length;
        }

        public Object next() {
            if (index >= stub.length) throw new NoSuchElementException();
            RequestClPdu fragment = (RequestClPdu) RequestClPdu.this.clone();
            int allocation = stub.length - index;
            if (stubSize < allocation) allocation = stubSize;
            byte[] fragmentStub = new byte[allocation];
            System.arraycopy(stub, index, fragmentStub, 0, allocation);
            fragment.setStub(fragmentStub);
            int flags1 = getFlags1() & ~PFC1_LAST_FRAG;
            flags1 |= PFC1_FRAG;
            index += allocation;
            if (index >= stub.length) flags1 |= PFC1_LAST_FRAG;
            fragment.setFlags1(flags1);
            fragment.setFragmentNumber(fragmentNumber++);
            return fragment;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

    }

}
