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

import rpc.ConnectionlessPdu;

import ndr.*;

public class NoCallPdu extends ConnectionlessPdu {

    public static final int NO_CALL_TYPE = 0x05;

    private FragmentAcknowledgeBody body;

    public int getType() {
        return NO_CALL_TYPE;
    }

    public FragmentAcknowledgeBody getBody() {
        return body;
    }

    public void setBody(FragmentAcknowledgeBody body) {
        this.body = body;
    }

    protected void readBody(NetworkDataRepresentation ndr) {
        FragmentAcknowledgeBody body = new FragmentAcknowledgeBody();
        body.read(ndr);
        setBody(body);
    }

    protected void writeBody(NetworkDataRepresentation ndr) {
        FragmentAcknowledgeBody body = getBody();
        if (body != null) body.write(ndr);
    }

}
