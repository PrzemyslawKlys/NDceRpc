package rpc.ndr;

import java.math.BigInteger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import rpc.Buffer;

public class NetworkDataRepresentation {

    public static final String NDR_UUID =
            "8a885d04-1ceb-11c9-9fe8-08002b104860";

    public static final int NDR_MAJOR_VERSION = 2;

    public static final int NDR_MINOR_VERSION = 0;

    public static final String NDR_SYNTAX = NDR_UUID + ":" + NDR_MAJOR_VERSION +
            "." + NDR_MINOR_VERSION;

    protected Buffer buffer;

    protected Format format;

    private Map callState;

    private List localPointers;

    private int localReferenceCount;

    private long identifier;

    public NetworkDataRepresentation() {
        this(null, null);
    }

    public NetworkDataRepresentation(Format format) {
        this(format, null);
    }

    public NetworkDataRepresentation(Buffer buffer) {
        this(null, buffer);
    }

    public NetworkDataRepresentation(Format format, Buffer buffer) {
        setFormat(format);
        setBuffer(buffer);
    }

    protected Map getCallState() {
        return (callState != null) ? callState : (callState = new HashMap());
    }

    public long getIdentifier(Element referent) {
        if (referent == null) return 0;
        Iterator entries = getCallState().entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry entry = (Map.Entry) entries.next();
            // == compare to ensure referent is the same object
            if (referent == entry.getValue()) {
                return ((Long) entry.getKey()).longValue();
            }
        }
        return ++identifier;
    }

    public Element getReferent(long identifier) {
        if (identifier > this.identifier) this.identifier = identifier;
        return (Element) getCallState().get(new Long(identifier));
    }

    public void registerPointer(Pointer pointer) {
        getCallState().put(new Long(pointer.identifier), pointer.referent);
        if (localPointers != null) localPointers.add(pointer);
    }

    public Format getFormat() {
        return format;
    }

    public void setFormat(Format format) {
        this.format = (format != null) ? format : Format.DEFAULT_FORMAT;
    }

    public Format readFormat(boolean connectionless) {
        return Format.readFormat(buffer.getBuffer(),
                buffer.getIndex(connectionless ? 3 : 4), connectionless);
    }

    public void writeFormat(boolean connectionless) {
        int index = buffer.getIndex(connectionless ? 3 : 4);
        format.writeFormat(buffer.getBuffer(), index, connectionless);
    }

    public Buffer getBuffer() {
        return buffer;
    }

    public void setBuffer(Buffer buffer) {
        this.buffer = buffer;
    }

    public boolean readBoolean() {
        int index = buffer.getIndex(1);
        return format.readBoolean(buffer.getBuffer(), index);
    }

    public void writeBoolean(boolean val) {
        int index = buffer.getIndex(1);
        format.writeBoolean(val, buffer.getBuffer(), index);
    }

    public void readBooleanArray(boolean[] array, int offset, int length) {
        int index = buffer.getIndex(length);
        format.readBooleanArray(array, offset, length, buffer.getBuffer(),
                index);
    }

    public void writeBooleanArray(boolean[] array, int offset, int length) {
        int index = buffer.getIndex(length);
        format.writeBooleanArray(array, offset, length, buffer.getBuffer(),
                index);
    }

    public char readCharacter() {
        int index = buffer.getIndex(1);
        return format.readCharacter(buffer.getBuffer(), index);
    }

    public void writeCharacter(char val) {
        int index = buffer.getIndex(1);
        format.writeCharacter(val, buffer.getBuffer(), index);
    }

    public void readCharacterArray(char[] array, int offset, int length) {
        int index = buffer.getIndex(length);
        format.readCharacterArray(array, offset, length,
                buffer.getBuffer(), index);
    }

    public void writeCharacterArray(char[] array, int offset, int length) {
        int index = buffer.getIndex(length);
        format.writeCharacterArray(array, offset, length,
                buffer.getBuffer(), index);
    }

    public char readWideCharacter() {
        buffer.align(2);
        int index = buffer.getIndex(2);
        return format.readWideCharacter(buffer.getBuffer(), index);
    }

    public void writeWideCharacter(char val) {
        buffer.align(2, (byte) 0);
        int index = buffer.getIndex(2);
        format.writeWideCharacter(val, buffer.getBuffer(), index);
    }

    public void readWideCharacterArray(char[] array, int offset, int length) {
        buffer.align(2);
        int index = buffer.getIndex(length * 2);
        format.readWideCharacterArray(array, offset, length,
                buffer.getBuffer(), index);
    }

    public void writeWideCharacterArray(char[] array, int offset, int length) {
        buffer.align(2, (byte) 0);
        int index = buffer.getIndex(length * 2);
        format.writeWideCharacterArray(array, offset, length,
                buffer.getBuffer(), index);
    }

    public byte readOctet() {
        int index = buffer.getIndex(1);
        return format.readOctet(buffer.getBuffer(), index);
    }

    public void writeOctet(byte val) {
        int index = buffer.getIndex(1);
        format.writeOctet(val, buffer.getBuffer(), index);
    }

    public void readOctetArray(byte[] array, int offset, int length) {
        int index = buffer.getIndex(length);
        format.readOctetArray(array, offset, length, buffer.getBuffer(),
                index);
    }

    public void writeOctetArray(byte[] array, int offset, int length) {
        int index = buffer.getIndex(length);
        format.writeOctetArray(array, offset, length, buffer.getBuffer(),
                index);
    }

    public byte readSignedSmall() {
        int index = buffer.getIndex(1);
        return format.readSignedSmall(buffer.getBuffer(), index);
    }

    public void writeSignedSmall(byte val) {
        int index = buffer.getIndex(1);
        format.writeSignedSmall(val, buffer.getBuffer(), index);
    }

    public void readSignedSmallArray(byte[] array, int offset, int length) {
        int index = buffer.getIndex(length);
        format.readSignedSmallArray(array, offset, length,
                buffer.getBuffer(), index);
    }

    public void writeSignedSmallArray(byte[] array, int offset, int length) {
        int index = buffer.getIndex(length);
        format.writeSignedSmallArray(array, offset, length,
                buffer.getBuffer(), index);
    }

    public short readUnsignedSmall() {
        int index = buffer.getIndex(1);
        return format.readUnsignedSmall(buffer.getBuffer(), index);
    }

    public void writeUnsignedSmall(short val) {
        int index = buffer.getIndex(1);
        format.writeUnsignedSmall(val, buffer.getBuffer(), index);
    }

    public void readUnsignedSmallArray(short[] array, int offset, int length) {
        int index = buffer.getIndex(length);
        format.readUnsignedSmallArray(array, offset, length,
                buffer.getBuffer(), index);
    }

    public void writeUnsignedSmallArray(short[] array, int offset, int length) {
        int index = buffer.getIndex(length);
        format.writeUnsignedSmallArray(array, offset, length,
                buffer.getBuffer(), index);
    }

    public short readSignedShort() {
        buffer.align(2);
        int index = buffer.getIndex(2);
        return format.readSignedShort(buffer.getBuffer(), index);
    }

    public void writeSignedShort(short val) {
        buffer.align(2, (byte) 0);
        int index = buffer.getIndex(2);
        format.writeSignedShort(val, buffer.getBuffer(), index);
    }

    public void readSignedShortArray(short[] array, int offset, int length) {
        buffer.align(2);
        int index = buffer.getIndex(length * 2);
        format.readSignedShortArray(array, offset, length,
                buffer.getBuffer(), index);
    }

    public void writeSignedShortArray(short[] array, int offset, int length) {
        buffer.align(2, (byte) 0);
        int index = buffer.getIndex(length * 2);
        format.writeSignedShortArray(array, offset, length,
                buffer.getBuffer(), index);
    }

    public int readUnsignedShort() {
        buffer.align(2);
        int index = buffer.getIndex(2);
        return format.readUnsignedShort(buffer.getBuffer(), index);
    }

    public void writeUnsignedShort(int val) {
        buffer.align(2, (byte) 0);
        int index = buffer.getIndex(2);
        format.writeUnsignedShort(val, buffer.getBuffer(), index);
    }

    public void readUnsignedShortArray(int[] array, int offset, int length) {
        buffer.align(2);
        int index = buffer.getIndex(length * 2);
        format.readUnsignedShortArray(array, offset, length,
                buffer.getBuffer(), index);
    }

    public void writeUnsignedShortArray(int[] array, int offset, int length) {
        buffer.align(2, (byte) 0);
        int index = buffer.getIndex(length * 2);
        format.writeUnsignedShortArray(array, offset, length,
                buffer.getBuffer(), index);
    }

    public int readSignedLong() {
        buffer.align(4);
        int index = buffer.getIndex(4);
        return format.readSignedLong(buffer.getBuffer(), index);
    }

    public void writeSignedLong(int val) {
        buffer.align(4, (byte) 0);
        int index = buffer.getIndex(4);
        format.writeSignedLong(val, buffer.getBuffer(), index);
    }

    public void readSignedLongArray(int[] array, int offset, int length) {
        buffer.align(4);
        int index = buffer.getIndex(length * 4);
        format.readSignedLongArray(array, offset, length,
                buffer.getBuffer(), index);
    }

    public void writeSignedLongArray(int[] array, int offset, int length) {
        buffer.align(4, (byte) 0);
        int index = buffer.getIndex(length * 4);
        format.writeSignedLongArray(array, offset, length,
                buffer.getBuffer(), index);
    }

    public long readUnsignedLong() {
        buffer.align(4);
        int index = buffer.getIndex(4);
        return format.readUnsignedLong(buffer.getBuffer(), index);
    }

    public void writeUnsignedLong(long val) {
        buffer.align(4, (byte) 0);
        int index = buffer.getIndex(4);
        format.writeUnsignedLong(val, buffer.getBuffer(), index);
    }

    public void readUnsignedLongArray(long[] array, int offset, int length) {
        buffer.align(4);
        int index = buffer.getIndex(length * 4);
        format.readUnsignedLongArray(array, offset, length,
                buffer.getBuffer(), index);
    }

    public void writeUnsignedLongArray(long[] array, int offset, int length) {
        buffer.align(4, (byte) 0);
        int index = buffer.getIndex(length * 4);
        format.writeUnsignedLongArray(array, offset, length,
                buffer.getBuffer(), index);
    }

    public long readSignedHyper() {
        buffer.align(8);
        int index = buffer.getIndex(8);
        return format.readSignedHyper(buffer.getBuffer(), index);
    }

    public void writeSignedHyper(long val) {
        buffer.align(8, (byte) 0);
        int index = buffer.getIndex(8);
        format.writeSignedHyper(val, buffer.getBuffer(), index);
    }

    public void readSignedHyperArray(long[] array, int offset, int length) {
        buffer.align(8);
        int index = buffer.getIndex(length * 8);
        format.readSignedHyperArray(array, offset, length,
                buffer.getBuffer(), index);
    }

    public void writeSignedHyperArray(long[] array, int offset, int length) {
        buffer.align(8, (byte) 0);
        int index = buffer.getIndex(length * 8);
        format.writeSignedHyperArray(array, offset, length,
                buffer.getBuffer(), index);
    }

    public BigInteger readUnsignedHyper() {
        buffer.align(8);
        int index = buffer.getIndex(8);
        return format.readUnsignedHyper(buffer.getBuffer(), index);
    }

    public void writeUnsignedHyper(BigInteger val) {
        buffer.align(8, (byte) 0);
        int index = buffer.getIndex(8);
        format.writeUnsignedHyper(val, buffer.getBuffer(), index);
    }

    public void readUnsignedHyperArray(BigInteger[] array, int offset,
            int length) {
        buffer.align(8);
        int index = buffer.getIndex(length * 8);
        format.readUnsignedHyperArray(array, offset, length,
                buffer.getBuffer(), index);
    }

    public void writeUnsignedHyperArray(BigInteger[] array, int offset,
            int length) {
        buffer.align(8, (byte) 0);
        int index = buffer.getIndex(length * 8);
        format.writeUnsignedHyperArray(array, offset, length,
                buffer.getBuffer(), index);
    }

    public float readFloat() {
        buffer.align(4);
        int index = buffer.getIndex(4);
        return format.readFloat(buffer.getBuffer(), index);
    }

    public void writeFloat(float val) {
        buffer.align(4, (byte) 0);
        int index = buffer.getIndex(4);
        format.writeFloat(val, buffer.getBuffer(), index);
    }

    public void readFloatArray(float[] array, int offset, int length) {
        buffer.align(4);
        int index = buffer.getIndex(length * 4);
        format.readFloatArray(array, offset, length, buffer.getBuffer(),
                index);
    }

    public void writeFloatArray(float[] array, int offset, int length) {
        buffer.align(4, (byte) 0);
        int index = buffer.getIndex(length * 4);
        format.writeFloatArray(array, offset, length, buffer.getBuffer(),
                index);
    }

    public double readDouble() {
        buffer.align(8);
        int index = buffer.getIndex(8);
        return format.readDouble(buffer.getBuffer(), index);
    }

    public void writeDouble(double val) {
        buffer.align(8, (byte) 0);
        int index = buffer.getIndex(8);
        format.writeDouble(val, buffer.getBuffer(), index);
    }

    public void readDoubleArray(double[] array, int offset, int length) {
        buffer.align(8);
        int index = buffer.getIndex(length * 8);
        format.readDoubleArray(array, offset, length, buffer.getBuffer(),
                index);
    }

    public void writeDoubleArray(double[] array, int offset, int length) {
        buffer.align(8, (byte) 0);
        int index = buffer.getIndex(length * 8);
        format.writeDoubleArray(array, offset, length, buffer.getBuffer(),
                index);
    }

    public Element readElement(Element val) {
        if (val == null) return null;
        boolean embedded = val.isEmbedded();
        if (!embedded) {
            if (localPointers == null) localPointers = new ArrayList();
            ++localReferenceCount;
            if (val instanceof Conformant) {
                ((Conformant) val).readConformance(this);
            }
        }
        buffer.align(val.getAlignment());
        val.read(this);
        if (!embedded) {
            Iterator locals = localPointers.iterator();
            while (locals.hasNext()) {
                Element referent = ((Pointer) locals.next()).referent;
                if (referent == null) continue;
                if (referent instanceof Conformant) {
                    ((Conformant) referent).readConformance(this);
                }
                buffer.align(referent.getAlignment());
                referent.read(this);
            }
            if (--localReferenceCount <= 0) {
                localPointers = null;
                localReferenceCount = 0;
            }
        }
        return val;
    }

    public void writeElement(Element val) {
        if (val == null) return;
        boolean embedded = val.isEmbedded();
        if (!embedded) {
            if (localPointers == null) localPointers = new ArrayList();
            ++localReferenceCount;
            if (val instanceof Conformant) {
                ((Conformant) val).writeConformance(this);
            }
        }
        buffer.align(val.getAlignment(), (byte) 0);
        val.write(this);
        if (!embedded) {
            Iterator locals = localPointers.iterator();
            while (locals.hasNext()) {
                Element referent = ((Pointer) locals.next()).referent;
                if (referent == null) continue;
                if (referent instanceof Conformant) {
                    ((Conformant) referent).writeConformance(this);
                }
                buffer.align(referent.getAlignment(), (byte) 0);
                referent.write(this);
            }
            if (--localReferenceCount <= 0) {
                localPointers = null;
                localReferenceCount = 0;
            }
        }
    }

}
