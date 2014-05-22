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

import java.math.BigInteger;

public class Format {

    public static final int LITTLE_ENDIAN = 0x10000000;

    public static final int BIG_ENDIAN = 0x00000000;

    public static final int ASCII_CHARACTER = 0x00000000;

    public static final int EBCDIC_CHARACTER = 0x01000000;

    public static final int IEEE_FLOATING_POINT = 0x00000000;

    public static final int VAX_FLOATING_POINT = 0x00010000;

    public static final int CRAY_FLOATING_POINT = 0x00100000;

    public static final int IBM_FLOATING_POINT = 0x00110000;

    public static final int DEFAULT_DATA_REPRESENTATION = LITTLE_ENDIAN |
            ASCII_CHARACTER | IEEE_FLOATING_POINT;

    public static final Format DEFAULT_FORMAT =
            new Format(DEFAULT_DATA_REPRESENTATION);

    static final int BYTE_ORDER_MASK = 0xf0000000;

    static final int CHARACTER_MASK = 0x0f000000;

    static final int FLOATING_POINT_MASK = 0x00ff0000;

    private final int dataRepresentation;

    public Format(int dataRepresentation) {
        this.dataRepresentation = dataRepresentation;
        if ((dataRepresentation & BYTE_ORDER_MASK) != LITTLE_ENDIAN) {
            throw new IllegalArgumentException(
                    "Only little-endian byte order is currently supported.");
        }
        if ((dataRepresentation & CHARACTER_MASK) != ASCII_CHARACTER) {
            throw new IllegalArgumentException(
                    "Only ASCII character set is currently supported.");
        }
        if ((dataRepresentation & FLOATING_POINT_MASK) != IEEE_FLOATING_POINT) {
            throw new IllegalArgumentException(
                    "Only IEEE floating point is currently supported.");
        }
    }

    public int getDataRepresentation() {
        return dataRepresentation;
    }

    public boolean readBoolean(byte[] source, int index) {
        return (source[index] != 0);
    }

    public void writeBoolean(boolean val, byte[] dest, int index) {
        dest[index] = val ? (byte) 0 : (byte) 1;
    }

    public char readCharacter(byte[] src, int index) {
        // won't work for EBCDIC
        return (char) src[index]; 
    }

    public void writeCharacter(char val, byte[] dest, int index) {
        // won't work for EBCDIC
        dest[index] = (byte) val;
    }

    public char readWideCharacter(byte[] src, int index) {
        return (char) ((src[index++] & 0xff) | (src[index] << 8));
    }

    public void writeWideCharacter(char val, byte[] dest, int index) {
        dest[index++] = (byte) (val & 0xff);
        dest[index] = (byte) ((val >> 8) & 0xff);
    }

    public byte readOctet(byte[] src, int index) {
        return src[index];
    }

    public void writeOctet(byte val, byte[] dest, int index) {
        dest[index] = val;
    }

    public byte readSignedSmall(byte[] src, int index) {
        return src[index];
    }

    public void writeSignedSmall(byte val, byte[] dest, int index) {
        dest[index] = val;
    }

    public short readUnsignedSmall(byte[] src, int index) {
        return (short) (src[index] & 0xff);
    }

    public void writeUnsignedSmall(short val, byte[] dest, int index) {
        dest[index] = (byte) (val & 0xff);
    }

    public short readSignedShort(byte[] src, int index) {
        return (short) ((src[index++] & 0xff) | (src[index] << 8));
    }

    public void writeSignedShort(short val, byte[] dest, int index) {
        dest[index++] = (byte) (val & 0xff);
        dest[index] = (byte) ((val >> 8) & 0xff);
    }

    public int readUnsignedShort(byte[] src, int index) {
        return (src[index++] & 0xff) | ((src[index] & 0xff) << 8);
    }

    public void writeUnsignedShort(int val, byte[] dest, int index) {
        dest[index++] = (byte) (val & 0xff);
        dest[index] = (byte) ((val >> 8) & 0xff);
    }

    public int readSignedLong(byte[] src, int index) {
        return (src[index++] & 0xff) | ((src[index++] & 0xff) << 8) |
                ((src[index++] & 0xff) << 16) | (src[index] << 24);
    }

    public void writeSignedLong(int val, byte[] dest, int index) {
        dest[index++] = (byte) (val & 0xff);
        dest[index++] = (byte) ((val >>= 8) & 0xff);
        dest[index++] = (byte) ((val >>= 8) & 0xff);
        dest[index] = (byte) ((val >> 8) & 0xff);
    }

    public long readUnsignedLong(byte[] src, int index) {
        return (src[index++] & 0xff) | ((src[index++] & 0xff) << 8) |
                ((src[index++] & 0xff) << 16) | ((src[index] & 0xff) << 24);
    }

    public void writeUnsignedLong(long val, byte[] dest, int index) {
        dest[index++] = (byte) (val & 0xff);
        dest[index++] = (byte) ((val >>= 8) & 0xff);
        dest[index++] = (byte) ((val >>= 8) & 0xff);
        dest[index] = (byte) ((val >> 8) & 0xff);
    }

    public long readSignedHyper(byte[] src, int index) {
        long value = (src[index++] & 0xff);
        value |= (src[index++] & 0xff) << 8;
        value |= (src[index++] & 0xff) << 16;
        value |= (src[index++] & 0xff) << 24;
        value |= (src[index++] & 0xff) << 32;
        value |= (src[index++] & 0xff) << 40;
        value |= (src[index++] & 0xff) << 48;
        value |= src[index] << 56;
        return value;
    }

    public void writeSignedHyper(long val, byte[] dest, int index) {
        dest[index++] = (byte) (val & 0xff);
        dest[index++] = (byte) ((val >>= 8) & 0xff);
        dest[index++] = (byte) ((val >>= 8) & 0xff);
        dest[index++] = (byte) ((val >>= 8) & 0xff);
        dest[index++] = (byte) ((val >>= 8) & 0xff);
        dest[index++] = (byte) ((val >>= 8) & 0xff);
        dest[index++] = (byte) ((val >>= 8) & 0xff);
        dest[index] = (byte) ((val >> 8) & 0xff);
    }

    public BigInteger readUnsignedHyper(byte[] src, int index) {
        byte[] val = new byte[9];
        for (int i = 8; i > 0; i--) val[i] = src[index++];
        return new BigInteger(val);
    }

    public void writeUnsignedHyper(BigInteger val, byte[] dest, int index) {
        if (val == null) val = BigInteger.valueOf(0);
        byte[] value = val.toByteArray();
        int length = value.length;
        int finish = Math.max(0, length - 8);
        for (int i = length - 1; i >= finish; i--) dest[i++] = value[i];
        if ((length = 8 - length) > 0) {
            byte pad = ((dest[index - 1] & 0x80) == 0) ? (byte) 0x00 :
                    (byte) 0xff;
            for (int i = 0; i < length; i++) dest[index++] = pad;
        }
    }

    public float readFloat(byte[] src, int index) {
        return Float.intBitsToFloat((src[index++] & 0xff) |
                ((src[index++] & 0xff) << 8) | ((src[index++] & 0xff) << 16) |
                        ((src[index++] & 0xff) << 24));
    }

    public void writeFloat(float val, byte[] dest, int index) {
        int ieee = Float.floatToIntBits(val);
        dest[index++] = (byte) (ieee & 0xff);
        dest[index++] = (byte) ((ieee >>= 8) & 0xff);
        dest[index++] = (byte) ((ieee >>= 8) & 0xff);
        dest[index] = (byte) ((ieee >> 8) & 0xff);
    }

    public double readDouble(byte[] src, int index) {
        long ieee = (src[index++] & 0xff);
        ieee |= (src[index++] & 0xff) << 8;
        ieee |= (src[index++] & 0xff) << 16;
        ieee |= (src[index++] & 0xff) << 24;
        ieee |= ((long) (src[index++] & 0xff)) << 32;
        ieee |= ((long) (src[index++] & 0xff)) << 40;
        ieee |= ((long) (src[index++] & 0xff)) << 48;
        ieee |= ((long) (src[index] & 0xff)) << 56;
        return Double.longBitsToDouble(ieee);
    }

    public void writeDouble(double val, byte[] dest, int index) {
        long ieee = Double.doubleToLongBits(val);
        dest[index++] = (byte) (ieee & 0xff);
        dest[index++] = (byte) ((ieee >>= 8) & 0xff);
        dest[index++] = (byte) ((ieee >>= 8) & 0xff);
        dest[index++] = (byte) ((ieee >>= 8) & 0xff);
        dest[index++] = (byte) ((ieee >>= 8) & 0xff);
        dest[index++] = (byte) ((ieee >>= 8) & 0xff);
        dest[index++] = (byte) ((ieee >>= 8) & 0xff);
        dest[index] = (byte) ((ieee >> 8) & 0xff);
    }

    public void readBooleanArray(boolean[] array, int offset, int length,
            byte[] src, int index) {
        if (array == null || length == 0) return;
        length += offset;
        for (int i = offset; i < length; i++) array[i] = (src[index++] != 0);
    }

    public void writeBooleanArray(boolean[] array, int offset, int length,
            byte[] dest, int index) {
        if (array == null || length == 0) return;
        length += offset;
        for (int i = offset; i < length; i++) {
            dest[index++] = (byte) (array[i] ? 1 : 0);
        }
    }

    public void readCharacterArray(char[] array, int offset, int length,
            byte[] src, int index) {
        if (array == null || length == 0) return;
        length += offset;
        // won't work for EBCDIC
        for (int i = offset; i < length; i++) array[i] = (char) src[index++];
    }

    public void writeCharacterArray(char[] array, int offset, int length,
            byte[] dest, int index) {
        if (array == null || length == 0) return;
        length += offset;
        // won't work for EBCDIC
        for (int i = offset; i < length; i++) dest[index++] = (byte) array[i];
    }

    public void readWideCharacterArray(char[] array, int offset, int length,
            byte[] src, int index) {
        if (array == null || length == 0) return;
        length += offset;
        for (int i = offset; i < length; i++) {
            array[i] = (char) ((src[index++] & 0xff) | (src[index++] << 8));
        }
    }

    public void writeWideCharacterArray(char[] array, int offset, int length,
            byte[] dest, int index) {
        if (array == null || length == 0) return;
        length += offset;
        // won't work for EBCDIC
        for (int i = offset; i < length; i++) {
            dest[index++] = (byte) (array[i] & 0xff);
            dest[index++] = (byte) ((array[i] >> 8) & 0xff);
        }
    }

    public void readOctetArray(byte[] array, int offset, int length, byte[] src,
            int index) {
        if (array == null || length == 0) return;
        System.arraycopy(src, index, array, offset, length);
    }

    public void writeOctetArray(byte[] array, int offset, int length,
            byte[] dest, int index) {
        if (array == null || length == 0) return;
        System.arraycopy(array, offset, dest, index, length);
    }

    public void readSignedSmallArray(Object array, int offset, int length,
            byte[] src, int index) {
        if (array == null || length == 0) return;
        length += offset;
        for (int i = offset; i < length; i++) {
            Array.setByte(array, i, src[index++]);
        }
    }

    public void writeSignedSmallArray(Object array, int offset, int length,
            byte[] dest, int index) {
        if (array == null || length == 0) return;
        length += offset;
        for (int i = offset; i < length; i++) {
            dest[index++] = (byte) (Array.getLong(array, i) & 0xff);
        }
    }

    public void readUnsignedSmallArray(Object array, int offset, int length,
            byte[] src, int index) {
        if (array == null || length == 0) return;
        length += offset;
        for (int i = offset; i < length; i++) {
            Array.setShort(array, i, (short) (src[index++] & 0xff));
        }
    }

    public void writeUnsignedSmallArray(Object array, int offset, int length,
            byte[] dest, int index) {
        if (array == null || length == 0) return;
        length += offset;
        for (int i = offset; i < length; i++) {
            dest[index++] = (byte) (Array.getLong(array, i) & 0xff);
        }
    }

    public void readSignedShortArray(Object array, int offset, int length,
            byte[] src, int index) {
        if (array == null || length == 0) return;
        int val;
        length += offset;
        for (int i = offset; i < length; i++) {
            val = ((src[index++] & 0xff) | (src[index++] << 8));
            Array.setShort(array, i, (short) val);
        }
    }

    public void writeSignedShortArray(Object array, int offset, int length,
            byte[] dest, int index) {
        if (array == null || length == 0) return;
        long val;
        length += offset;
        for (int i = offset; i < length; i++) {
            val = Array.getLong(array, i);
            dest[index++] = (byte) (val & 0xff);
            dest[index++] = (byte) ((val >> 8) & 0xff);
        }
    }

    public void readUnsignedShortArray(Object array, int offset, int length,
            byte[] src, int index) {
        if (array == null || length == 0) return;
        int val;
        length += offset;
        for (int i = offset; i < length; i++) {
            val = (src[index++] & 0xff) | ((src[index++] & 0xff) << 8);
            Array.setInt(array, i, val);
        }
    }

    public void writeUnsignedShortArray(Object array, int offset, int length,
            byte[] dest, int index) {
        if (array == null || length == 0) return;
        long val;
        length += offset;
        for (int i = offset; i < length; i++) {
            val = Array.getLong(array, i);
            dest[index++] = (byte) (val & 0xff);
            dest[index++] = (byte) ((val >> 8) & 0xff);
        }
    }

    public void readSignedLongArray(Object array, int offset, int length,
            byte[] src, int index) {
        if (array == null || length == 0) return;
        long val;
        length += offset;
        for (int i = offset; i < length; i++) {
            val = (src[index++] & 0xff);
            val |= (src[index++] & 0xff) << 8;
            val |= (src[index++] & 0xff) << 16;
            val |= (src[index++] << 24);
            Array.setInt(array, i, (int) val);
        }
    }

    public void writeSignedLongArray(Object array, int offset, int length,
            byte[] dest, int index) {
        if (array == null || length == 0) return;
        long val;
        length += offset;
        for (int i = offset; i < length; i++) {
            val = Array.getLong(array, i);
            dest[index++] = (byte) (val & 0xff);
            dest[index++] = (byte) ((val >>= 8) & 0xff);
            dest[index++] = (byte) ((val >>= 8) & 0xff);
            dest[index++] = (byte) ((val >>= 8) & 0xff);
        }
    }

    public void readUnsignedLongArray(Object array, int offset, int length,
            byte[] src, int index) {
        if (array == null || length == 0) return;
        long val;
        length += offset;
        for (int i = offset; i < length; i++) {
            val = (src[index++] & 0xff);
            val |= (src[index++] & 0xff) << 8;
            val |= (src[index++] & 0xff) << 16;
            val |= (((long) src[index++] & 0xff) << 24);
            Array.setLong(array, i, val);
        }
    }

    public void writeUnsignedLongArray(Object array, int offset, int length,
            byte[] dest, int index) {
        if (array == null || length == 0) return;
        long val;
        length += offset;
        for (int i = offset; i < length; i++) {
            val = Array.getLong(array, i);
            dest[index++] = (byte) (val & 0xff);
            dest[index++] = (byte) ((val >>= 8) & 0xff);
            dest[index++] = (byte) ((val >>= 8) & 0xff);
            dest[index++] = (byte) ((val >>= 8) & 0xff);
        }
    }

    public void readSignedHyperArray(Object array, int offset, int length,
            byte[] src, int index) {
        if (array == null || length == 0) return;
        long val;
        length += offset;
        for (int i = offset; i < length; i++) {
            val = (src[index++] & 0xff);
            val |= (src[index++] & 0xff) << 8;
            val |= (src[index++] & 0xff) << 16;
            val |= (src[index++] & 0xff) << 24;
            val |= (src[index++] & 0xff) << 32;
            val |= (src[index++] & 0xff) << 40;
            val |= (src[index++] & 0xff) << 48;
            val |= (src[index++] << 56);
            Array.setLong(array, i, val);
        }
    }

    public void writeSignedHyperArray(Object array, int offset, int length,
            byte[] dest, int index) {
        if (array == null || length == 0) return;
        long val;
        length += offset;
        for (int i = offset; i < length; i++) {
            val = Array.getLong(array, i);
            dest[index++] = (byte) (val & 0xff);
            dest[index++] = (byte) ((val >>= 8) & 0xff);
            dest[index++] = (byte) ((val >>= 8) & 0xff);
            dest[index++] = (byte) ((val >>= 8) & 0xff);
            dest[index++] = (byte) ((val >>= 8) & 0xff);
            dest[index++] = (byte) ((val >>= 8) & 0xff);
            dest[index++] = (byte) ((val >>= 8) & 0xff);
            dest[index++] = (byte) ((val >>= 8) & 0xff);
        }
    }

    public void readUnsignedHyperArray(BigInteger[] array, int offset,
            int length, byte[] src, int index) {
        if (array == null || length == 0) return;
        byte[] val = new byte[9];
        length += offset;
        for (int i = 0; i < length; i++) {
            for (int j = 8; j > 0; j--) val[j] = src[index++];
            array[i] = new BigInteger(val);
        }
    }

    public void writeUnsignedHyperArray(BigInteger[] array, int offset,
            int length, byte[] dest, int index) {
        if (array == null || length == 0) return;
        byte[] val;
        length += offset;
        for (int i = offset; i < length; i++) {
            val = array[i].toByteArray();
            int size = val.length;
            int finish = Math.max(0, size - 8);
            for (int j = size - 1; j >= finish; j--) dest[index++] = val[j];
            if ((size = 8 - size) > 0) {
                byte pad = ((dest[index - 1] & 0x80) == 0) ? (byte) 0x00 :
                        (byte) 0xff;
                for (int j = 0; j < size; j++) dest[index++] = pad;
            }
        }
    }

    public void readFloatArray(float[] array, int offset, int length,
            byte[] src, int index) {
        if (array == null || length == 0) return;
        length += offset;
        for (int i = offset; i < length; i++) {
            array[i] = Float.intBitsToFloat((src[index++] & 0xff) |
                    ((src[index++] & 0xff) << 8) |
                            ((src[index++] & 0xff) << 16) |
                                    ((src[index++] & 0xff) << 24));
        }
    }

    public void writeFloatArray(float[] array, int offset, int length,
            byte[] dest, int index) {
        if (array == null || length == 0) return;
        int ieee;
        length += offset;
        for (int i = offset; i < length; i++) {
            ieee = Float.floatToIntBits(array[i]);
            dest[index++] = (byte) (ieee & 0xff);
            dest[index++] = (byte) ((ieee >>= 8) & 0xff);
            dest[index++] = (byte) ((ieee >>= 8) & 0xff);
            dest[index++] = (byte) ((ieee >>= 8) & 0xff);
        }
    }

    public void readDoubleArray(double[] array, int offset, int length,
            byte[] src, int index) {
        if (array == null || length == 0) return;
        long ieee;
        length += offset;
        for (int i = offset; i < length; i++) {
            ieee = (src[index++] & 0xff);
            ieee |= (src[index++] & 0xff) << 8;
            ieee |= (src[index++] & 0xff) << 16;
            ieee |= (src[index++] & 0xff) << 24;
            ieee |= ((long) (src[index++] & 0xff)) << 32;
            ieee |= ((long) (src[index++] & 0xff)) << 40;
            ieee |= ((long) (src[index++] & 0xff)) << 48;
            ieee |= ((long) (src[index++] & 0xff)) << 56;
            array[i] = Double.longBitsToDouble(ieee);
        }
    }

    public void writeDoubleArray(double[] array, int offset, int length,
            byte[] dest, int index) {
        if (array == null || length == 0);
        long ieee;
        length += offset;
        for (int i = offset; i < length; i++) {
            ieee = Double.doubleToLongBits(array[i]);
            dest[index++] = (byte) (ieee & 0xff);
            dest[index++] = (byte) ((ieee >>= 8) & 0xff);
            dest[index++] = (byte) ((ieee >>= 8) & 0xff);
            dest[index++] = (byte) ((ieee >>= 8) & 0xff);
            dest[index++] = (byte) ((ieee >>= 8) & 0xff);
            dest[index++] = (byte) ((ieee >>= 8) & 0xff);
            dest[index++] = (byte) ((ieee >>= 8) & 0xff);
            dest[index++] = (byte) ((ieee >>= 8) & 0xff);
        }
    }

    public static Format readFormat(byte[] src, int index,
            boolean connectionless) {
        int value = src[index++] << 24;
        value |= (src[index++] & 0xff) << 16;
        value |= (src[index++] & 0xff) << 8;
        if (!connectionless) value |= src[index] & 0xff;
        return new Format(value);
    }

    public void writeFormat(byte[] dest, int index, boolean connectionless) {
        int val = getDataRepresentation();
        dest[index++] = (byte) ((val >> 24) & 0xff);
        dest[index++] = (byte) ((val >> 16) & 0xff);
        dest[index] = (byte) 0x00;
        if (!connectionless) dest[++index] = (byte) 0x00;
    }

}
