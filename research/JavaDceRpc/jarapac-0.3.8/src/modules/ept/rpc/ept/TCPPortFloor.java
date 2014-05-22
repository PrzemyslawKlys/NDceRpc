package rpc.ept;

import java.io.IOException;

public class TCPPortFloor implements Floor {

    private static final byte TCP_PORT_IDENTIFIER = (byte) 0x07;

    private int port;

    public TCPPortFloor() {
        this(0);
    }

    public TCPPortFloor(int port) {
        setPort(port);
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public byte getProtocolIdentifier() {
        return TCP_PORT_IDENTIFIER;
    }

    public byte[] getLeftHand() {
        return new byte[] { getProtocolIdentifier() };
    }

    public byte[] getRightHand() {
        byte[] rightHand = new byte[2];
        int port = getPort();
        rightHand[0] = (byte) ((port >> 8) & 0xff);
        rightHand[1] = (byte) (port & 0xff);
        return rightHand;
    }

    public void decode(byte[] src, int lhsIndex, int lhsSize, int rhsIndex,
            int rhsSize) throws IOException {
        if (src[lhsIndex] != getProtocolIdentifier()) {
            throw new IOException("Invalid protocol identifier: " +
                    Integer.toHexString(src[lhsIndex]));
        }
        setPort(((src[rhsIndex] & 0xff) << 8) | (src[rhsIndex + 1] & 0xff));
    }

}
