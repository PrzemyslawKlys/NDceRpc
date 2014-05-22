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

import java.math.BigInteger;

public interface ArrayHelper {

    public static final ArrayHelper BOOLEAN_HELPER = new ArrayHelper() {

        public Class getType() {
            return Boolean.TYPE;
        }

        public int getAlignment() {
            return 1;
        }

        public void readArray(Object array, int offset, int length,
                NetworkDataRepresentation ndr) {
            if (array == null) return;
            ndr.readBooleanArray((boolean[]) array, offset, length);
        }

        public void writeArray(Object array, int offset, int length,
                NetworkDataRepresentation ndr) {
            if (array == null) return;
            ndr.writeBooleanArray((boolean[]) array, offset, length);
        }

    };

    public static final ArrayHelper CHARACTER_HELPER = new ArrayHelper() {

        public Class getType() {
            return Character.TYPE;
        }

        public int getAlignment() {
            return 1;
        }

        public void readArray(Object array, int offset, int length,
                NetworkDataRepresentation ndr) {
            if (array == null) return;
            ndr.readCharacterArray((char[]) array, offset, length);
        }

        public void writeArray(Object array, int offset, int length,
                NetworkDataRepresentation ndr) {
            if (array == null) return;
            ndr.writeCharacterArray((char[]) array, offset, length);
        }

    };

    public static final ArrayHelper WIDE_CHARACTER_HELPER = new ArrayHelper() {

        public Class getType() {
            return Character.TYPE;
        }

        public int getAlignment() {
            return 2;
        }

        public void readArray(Object array, int offset, int length,
                NetworkDataRepresentation ndr) {
            if (array == null) return;
            ndr.readWideCharacterArray((char[]) array, offset, length);
        }

        public void writeArray(Object array, int offset, int length,
                NetworkDataRepresentation ndr) {
            if (array == null) return;
            ndr.writeWideCharacterArray((char[]) array, offset, length);
        }

    };

    public static final ArrayHelper OCTET_HELPER = new ArrayHelper() {

        public Class getType() {
            return Byte.TYPE;
        }

        public int getAlignment() {
            return 1;
        }

        public void readArray(Object array, int offset, int length,
                NetworkDataRepresentation ndr) {
            if (array == null) return;
            ndr.readOctetArray((byte[]) array, offset, length);
        }

        public void writeArray(Object array, int offset, int length,
                NetworkDataRepresentation ndr) {
            if (array == null) return;
            ndr.writeOctetArray((byte[]) array, offset, length);
        }

    };

    public static final ArrayHelper SIGNED_SMALL_HELPER = new ArrayHelper() {

        public Class getType() {
            return Byte.TYPE;
        }

        public int getAlignment() {
            return 1;
        }

        public void readArray(Object array, int offset, int length,
                NetworkDataRepresentation ndr) {
            if (array == null) return;
            ndr.readSignedSmallArray((byte[]) array, offset, length);
        }

        public void writeArray(Object array, int offset, int length,
                NetworkDataRepresentation ndr) {
            if (array == null) return;
            ndr.writeSignedSmallArray((byte[]) array, offset, length);
        }

    };

    public static final ArrayHelper UNSIGNED_SMALL_HELPER = new ArrayHelper() {

        public Class getType() {
            return Short.TYPE;
        }

        public int getAlignment() {
            return 1;
        }

        public void readArray(Object array, int offset, int length,
                NetworkDataRepresentation ndr) {
            if (array == null) return;
            ndr.readUnsignedSmallArray((short[]) array, offset, length);
        }

        public void writeArray(Object array, int offset, int length,
                NetworkDataRepresentation ndr) {
            if (array == null) return;
            ndr.writeUnsignedSmallArray((short[]) array, offset, length);
        }

    };

    public static final ArrayHelper SIGNED_SHORT_HELPER = new ArrayHelper() {

        public Class getType() {
            return Short.TYPE;
        }

        public int getAlignment() {
            return 2;
        }

        public void readArray(Object array, int offset, int length,
                NetworkDataRepresentation ndr) {
            if (array == null) return;
            ndr.readSignedShortArray((short[]) array, offset, length);
        }

        public void writeArray(Object array, int offset, int length,
                NetworkDataRepresentation ndr) {
            if (array == null) return;
            ndr.writeSignedShortArray((short[]) array, offset, length);
        }

    };

    public static final ArrayHelper UNSIGNED_SHORT_HELPER = new ArrayHelper() {

        public Class getType() {
            return Integer.TYPE;
        }

        public int getAlignment() {
            return 2;
        }

        public void readArray(Object array, int offset, int length,
                NetworkDataRepresentation ndr) {
            if (array == null) return;
            ndr.readUnsignedShortArray((int[]) array, offset, length);
        }

        public void writeArray(Object array, int offset, int length,
                NetworkDataRepresentation ndr) {
            if (array == null) return;
            ndr.writeUnsignedShortArray((int[]) array, offset, length);
        }

    };

    public static final ArrayHelper SIGNED_LONG_HELPER = new ArrayHelper() {

        public Class getType() {
            return Integer.TYPE;
        }

        public int getAlignment() {
            return 4;
        }

        public void readArray(Object array, int offset, int length,
                NetworkDataRepresentation ndr) {
            if (array == null) return;
            ndr.readSignedLongArray((int[]) array, offset, length);
        }

        public void writeArray(Object array, int offset, int length,
                NetworkDataRepresentation ndr) {
            if (array == null) return;
            ndr.writeSignedLongArray((int[]) array, offset, length);
        }

    };

    public static final ArrayHelper UNSIGNED_LONG_HELPER = new ArrayHelper() {

        public Class getType() {
            return Long.TYPE;
        }

        public int getAlignment() {
            return 4;
        }

        public void readArray(Object array, int offset, int length,
                NetworkDataRepresentation ndr) {
            if (array == null) return;
            ndr.readUnsignedLongArray((long[]) array, offset, length);
        }

        public void writeArray(Object array, int offset, int length,
                NetworkDataRepresentation ndr) {
            if (array == null) return;
            ndr.writeUnsignedLongArray((long[]) array, offset, length);
        }

    };

    public static final ArrayHelper SIGNED_HYPER_HELPER = new ArrayHelper() {

        public Class getType() {
            return Long.TYPE;
        }

        public int getAlignment() {
            return 8;
        }

        public void readArray(Object array, int offset, int length,
                NetworkDataRepresentation ndr) {
            if (array == null) return;
            ndr.readSignedHyperArray((long[]) array, offset, length);
        }

        public void writeArray(Object array, int offset, int length,
                NetworkDataRepresentation ndr) {
            if (array == null) return;
            ndr.writeSignedHyperArray((long[]) array, offset, length);
        }

    };

    public static final ArrayHelper UNSIGNED_HYPER_HELPER = new ArrayHelper() {

        public Class getType() {
            return BigInteger.class;
        }

        public int getAlignment() {
            return 8;
        }

        public void readArray(Object array, int offset, int length,
                NetworkDataRepresentation ndr) {
            if (array == null) return;
            ndr.readUnsignedHyperArray((BigInteger[]) array, offset, length);
        }

        public void writeArray(Object array, int offset, int length,
                NetworkDataRepresentation ndr) {
            if (array == null) return;
            ndr.writeUnsignedHyperArray((BigInteger[]) array, offset, length);
        }

    };

    public static final ArrayHelper FLOAT_HELPER = new ArrayHelper() {

        public Class getType() {
            return Float.TYPE;
        }

        public int getAlignment() {
            return 4;
        }

        public void readArray(Object array, int offset, int length,
                NetworkDataRepresentation ndr) {
            if (array == null) return;
            ndr.readFloatArray((float[]) array, offset, length);
        }

        public void writeArray(Object array, int offset, int length,
                NetworkDataRepresentation ndr) {
            if (array == null) return;
            ndr.writeFloatArray((float[]) array, offset, length);
        }

    };

    public static final ArrayHelper DOUBLE_HELPER = new ArrayHelper() {

        public Class getType() {
            return Double.TYPE;
        }

        public int getAlignment() {
            return 8;
        }

        public void readArray(Object array, int offset, int length,
                NetworkDataRepresentation ndr) {
            if (array == null) return;
            ndr.readDoubleArray((double[]) array, offset, length);
        }

        public void writeArray(Object array, int offset, int length,
                NetworkDataRepresentation ndr) {
            if (array == null) return;
            ndr.writeDoubleArray((double[]) array, offset, length);
        }

    };

    public Class getType();

    public int getAlignment();

    public void readArray(Object array, int offset, int length,
            NetworkDataRepresentation ndr);

    public void writeArray(Object array, int offset, int length,
            NetworkDataRepresentation ndr);

}
