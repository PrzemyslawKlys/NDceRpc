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

import ndr.*;

public class FragmentAcknowledgeBody extends NdrObject {

    public int version;

    public int windowSize;

    public int maxTsdu;

    public int maxFragment;

    public int serialNumber;

    public int[] selectiveAcknowledgeMasks;

    public FragmentAcknowledgeBody() {
        this(0, 0, 0, 0, 0, null);
    }

    public FragmentAcknowledgeBody(int version, int windowSize, int maxTsdu,
            int maxFragment, int serialNumber,
                    int[] selectiveAcknowledgeMasks) {
        this.version = version;
        this.windowSize = windowSize;
        this.maxTsdu = maxTsdu;
        this.maxFragment = maxFragment;
        this.serialNumber = serialNumber;
        this.selectiveAcknowledgeMasks = selectiveAcknowledgeMasks;
    }

    public void read(NetworkDataRepresentation ndr) {
        version = ndr.readUnsignedSmall();
        windowSize = ndr.readUnsignedShort();
        maxTsdu = ndr.readUnsignedLong();
        maxFragment = ndr.readUnsignedLong();
        serialNumber = ndr.readUnsignedShort();
        int count = ndr.readUnsignedShort();
        if (count > 0) {
            selectiveAcknowledgeMasks = new int[count];
			for (int i = 0; i < count; i++) {
				selectiveAcknowledgeMasks[i] = ndr.readUnsignedLong();
			}
        } else {
            selectiveAcknowledgeMasks = null;
        }
    }

    public void write(NetworkDataRepresentation ndr) {
        ndr.writeUnsignedSmall((short) version);
        ndr.writeUnsignedShort(windowSize);
        ndr.writeUnsignedLong(maxTsdu);
        ndr.writeUnsignedLong(maxFragment);
        ndr.writeUnsignedShort(serialNumber);
        int length = (selectiveAcknowledgeMasks != null) ?
                selectiveAcknowledgeMasks.length : 0;
        ndr.writeUnsignedShort(length);
        if (length > 0) {
			for (int i = 0; i < length; i++) {
	 			ndr.writeUnsignedLong(selectiveAcknowledgeMasks[i]);
			}
        }
    }

}
