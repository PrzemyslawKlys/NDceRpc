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

public class ConformantArray extends FixedArray implements Conformant {

    public ConformantArray(Object array, ArrayHelper helper) {
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
        ArrayHelper helper = getHelper();
        if (redim) {
            setArray(Array.newInstance(helper.getType(), conformance));
        }
        if (ConformantVaryingString.class.isAssignableFrom(helper.getType())) {
            ConformantVaryingString string  = (ConformantVaryingString)
                    ((ElementHelper) helper).getTemplate();
            string.setConformance((int) ndr.readUnsignedLong());
        }
    }

    public void writeConformance(NetworkDataRepresentation ndr) {
        int[] conformance = getConformance();
        int dimensions = conformance.length;
        for (int i = 0; i < dimensions; i++) {
            ndr.writeUnsignedLong(conformance[i]);
        }
        ArrayHelper helper = getHelper();
        if (ConformantVaryingString.class.isAssignableFrom(helper.getType())) {
            ConformantVaryingString string = (ConformantVaryingString)
                    ((ElementHelper) helper).getTemplate();
            ndr.writeUnsignedLong(string.getConformance());
        }
    }

}
