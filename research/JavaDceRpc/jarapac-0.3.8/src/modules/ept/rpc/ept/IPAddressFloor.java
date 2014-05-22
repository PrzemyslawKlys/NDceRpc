package rpc.ept;

import java.io.IOException;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class IPAddressFloor implements Floor {

    private static final byte IP_ADDRESS_IDENTIFIER = (byte) 0x09;

    private InetAddress address;

    public IPAddressFloor() {
        this((InetAddress) null);
    }

    public IPAddressFloor(String address) throws UnknownHostException {
        setAddress(address);
    }

    public IPAddressFloor(InetAddress address) {
        setAddress(address);
    }

    public InetAddress getAddress() {
        return address;
    }

    public void setAddress(InetAddress address) {
        this.address = address;
    }

    public void setAddress(String address) throws UnknownHostException {
        setAddress((address != null) ? InetAddress.getByName(address) :
                (InetAddress) null);
    }

    public byte getProtocolIdentifier() {
        return IP_ADDRESS_IDENTIFIER;
    }

    public byte[] getLeftHand() {
        return new byte[] { getProtocolIdentifier() };
    }

    public byte[] getRightHand() {
        InetAddress address = getAddress();
        return (address != null) ? address.getAddress() : new byte[4];
    }

    public void decode(byte[] src, int lhsIndex, int lhsSize, int rhsIndex,
            int rhsSize) throws IOException {
        if (src[lhsIndex] != getProtocolIdentifier()) {
            throw new IOException("Invalid protocol identifier: " +
                    Integer.toHexString(src[lhsIndex]));
        }
        StringBuffer buffer = new StringBuffer();
        buffer.append(src[rhsIndex++]);
        buffer.append('.');
        buffer.append(src[rhsIndex++]);
        buffer.append('.');
        buffer.append(src[rhsIndex++]);
        buffer.append('.');
        buffer.append(src[rhsIndex]);
        setAddress(buffer.toString());
    }

}
