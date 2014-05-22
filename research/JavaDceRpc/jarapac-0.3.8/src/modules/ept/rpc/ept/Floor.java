package rpc.ept;

import java.io.IOException;

public interface Floor {

    public byte getProtocolIdentifier();

    public byte[] getLeftHand();

    public byte[] getRightHand();

    public void decode(byte[] src, int lhsIndex, int lhsSize, int rhsIndex,
            int rhsSize) throws IOException;

}
