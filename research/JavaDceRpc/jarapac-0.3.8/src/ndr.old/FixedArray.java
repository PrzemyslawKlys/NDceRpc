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

public class FixedArray implements Element {

    private Object array;

    private ArrayHelper helper;

    private int dimensions;

    private boolean embedded;

    public FixedArray(Object array, ArrayHelper helper) {
        setArray(array);
        setHelper(helper);
    }

    public Object getArray() {
        return array;
    }

    public void setArray(Object array) {
        if (!(array.getClass().isArray())) {
            throw new IllegalArgumentException("Not an array.");
        }
        this.array = array;
        dimensions = 0;
    }

    public ArrayHelper getHelper() {
        return helper;
    }

    public void setHelper(ArrayHelper helper) {
        if (helper == null) throw new NullPointerException("Null helper.");
        this.helper = helper;
    }

    public int getAlignment() {
        return getHelper().getAlignment();
    }

    public boolean isEmbedded() {
        return embedded;
    }

    public void setEmbedded(boolean embedded) {
        this.embedded = embedded;
    }

    public void read(NetworkDataRepresentation ndr) {
        readArray(getArray(), 0, getVariance(), ndr);
    }

    public void write(NetworkDataRepresentation ndr) {
        writeArray(getArray(), 0, getVariance(), ndr);
    }

    public int getDimensions() {
        if (dimensions != 0) return dimensions;
        Object array = getArray();
        Class arrayClass = array.getClass();
        int dimensions;
        for (dimensions = 0; arrayClass.isArray(); dimensions++) {
            arrayClass = arrayClass.getComponentType();
        }
        return (this.dimensions = dimensions);
    }

    protected int[] getConformance() {
        int dimensions = getDimensions();
        int[] conformance = new int[dimensions];
        Object array = getArray();
        for (int i = 0; i < dimensions; i++) {
            if ((conformance[i] = Array.getLength(array)) == 0) break;
            array = Array.get(array, 0);
        }
        return conformance;
    }

    protected Variance[] getVariance() {
        int[] conformance = getConformance();
        int dimensions = conformance.length;
        Variance[] variance = new Variance[dimensions];
        for (int i = 0; i < dimensions; i++) {
            variance[i] = new Variance(0, conformance[i]);
        }
        return variance;
    }

    private void readArray(Object array, int dimension, Variance[] variance,
            NetworkDataRepresentation ndr) {
        int offset = variance[dimension].offset;
        int length = variance[dimension].length;
        if (array.getClass().getComponentType().isArray()) {
            length += offset;
            dimension++;
            for (int i = offset; i < length; i++) {
                readArray(Array.get(array, i), dimension, variance, ndr);
            }
        } else {
            getHelper().readArray(array, offset, length, ndr);
        }
    }

    private void writeArray(Object array, int dimension, Variance[] variance,
            NetworkDataRepresentation ndr) {
        int offset = variance[dimension].offset;
        int length = variance[dimension].length;
        if (array.getClass().getComponentType().isArray()) {
            length += offset;
            dimension++;
            for (int i = offset; i < length; i++) {
                writeArray(Array.get(array, i), dimension, variance, ndr);
            }
        } else {
            getHelper().writeArray(array, offset, length, ndr);
        }
    }

    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException ex) {
            throw new IllegalStateException();
        }
    }

}
