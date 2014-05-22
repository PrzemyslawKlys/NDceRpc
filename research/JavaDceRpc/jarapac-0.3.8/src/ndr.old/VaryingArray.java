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

public class VaryingArray extends FixedArray {

    private Variance[] variance;

    public VaryingArray(Object array, ArrayHelper helper) {
        super(array, helper);
    }

    public void setArray(Object array) {
        super.setArray(array);
        variance = null;
    }

    public Variance[] getVariance() {
        return (variance != null) ? variance : (variance = super.getVariance());
    }

    public void setVariance(Variance[] variance) {
        this.variance = variance;
    }

    public void read(NetworkDataRepresentation ndr) {
        int dimensions = getDimensions();
        Variance[] variance = new Variance[dimensions];
        for (int i = 0; i < dimensions; i++) {
            variance[i] = new Variance((int) ndr.readUnsignedLong(),
                    (int) ndr.readUnsignedLong());
        }
        setVariance(variance);
        super.read(ndr);
    }

    public void write(NetworkDataRepresentation ndr) {
        Variance[] variance = getVariance();
        int dimensions = variance.length;
        for (int i = 0; i < dimensions; i++) {
            ndr.writeUnsignedLong(variance[i].offset);
            ndr.writeUnsignedLong(variance[i].length);
        }
        super.write(ndr);
    }

}
