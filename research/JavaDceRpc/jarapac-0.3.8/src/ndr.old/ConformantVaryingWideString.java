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

package rpc.ndr;

public class ConformantVaryingWideString extends VaryingWideString
        implements Conformant {

    public ConformantVaryingWideString() {
        super();
    }

    public ConformantVaryingWideString(String string) {
        super(string);
    }

    public int getConformance() {
        return super.getConformance();
    }

    public void setConformance(int conformance) {
        if (conformance == getConformance()) return;
        string = new char[conformance - 1];
    }

    public void readConformance(NetworkDataRepresentation ndr) {
        setConformance((int) ndr.readUnsignedLong());
    }

    public void writeConformance(NetworkDataRepresentation ndr) {
        ndr.writeUnsignedLong(getConformance());
    }

}
