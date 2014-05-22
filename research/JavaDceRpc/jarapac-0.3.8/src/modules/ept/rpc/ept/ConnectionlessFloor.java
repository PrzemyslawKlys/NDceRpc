package rpc.ept;

import java.io.IOException;

import rpc.ndr.Format;

public class ConnectionlessFloor implements Floor {

    private static final byte CONNECTIONLESS_IDENTIFIER = (byte) 0x0a;

    private int minorVersion;

    public ConnectionlessFloor() {
        this(0);
    }

    public ConnectionlessFloor(int minorVersion) {
        setMinorVersion(minorVersion);
    }

    public int getMinorVersion() {
        return minorVersion;
    }

    public void setMinorVersion(int minorVersion) {
        this.minorVersion = minorVersion;
    }

    public byte getProtocolIdentifier() {
        return CONNECTIONLESS_IDENTIFIER;
    }

    public byte[] getLeftHand() {
        return new byte[] { getProtocolIdentifier() };
    }

    public byte[] getRightHand() {
        byte[] rightHand = new byte[2];
        Format format = new Format(Format.LITTLE_ENDIAN |
                Format.ASCII_CHARACTER | Format.IEEE_FLOATING_POINT);
        format.writeUnsignedShort(getMinorVersion(), rightHand, 0);
        return rightHand;
    }

    public void decode(byte[] src, int lhsIndex, int lhsSize, int rhsIndex,
            int rhsSize) throws IOException {
        if (src[lhsIndex] != getProtocolIdentifier()) {
            throw new IOException("Invalid protocol identifier: " +
                    Integer.toHexString(src[lhsIndex]));
        }
        try {
            Format format = new Format(Format.LITTLE_ENDIAN |
                    Format.ASCII_CHARACTER | Format.IEEE_FLOATING_POINT);
            setMinorVersion(format.readUnsignedShort(src, rhsIndex));
        } catch (Exception ex) {
            throw new IOException("Decoding error: " + ex);
        }
    }

}
