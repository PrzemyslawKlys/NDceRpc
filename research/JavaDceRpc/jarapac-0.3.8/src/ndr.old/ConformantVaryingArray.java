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

import java.lang.reflect.Array;

public class ConformantVaryingArray extends VaryingArray implements Conformant {

    public ConformantVaryingArray(Object array, ArrayHelper helper) {
        super(array, helper);
    }

    public void readConformance(NetworkDataRepresentation ndr) {
        int[] conformance = getConformance();
        int dimensions = conformance.length;
        boolean redim = false;
        int length;
        for (int i = 0; i < dimensions; i++) {
            if ((length = (int) ndr.readUnsignedLong()) != conformance[i]) {
                conformance[i] = length;
                redim = true;
            }
        }
        if (redim) {
            setArray(Array.newInstance(getHelper().getType(), conformance));
        }
    }

    public void writeConformance(NetworkDataRepresentation ndr) {
        int[] conformance = getConformance();
        int dimensions = conformance.length;
        for (int i = 0; i < dimensions; i++) {
            ndr.writeUnsignedLong(conformance[i]);
        }
    }

}
