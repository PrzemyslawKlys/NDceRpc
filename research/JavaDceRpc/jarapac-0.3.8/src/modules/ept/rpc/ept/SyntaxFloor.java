package rpc.ept;

import java.io.IOException;

import rpc.Buffer;

import rpc.core.PresentationSyntax;
import rpc.core.UUID;

import rpc.ndr.Format;
import rpc.ndr.NetworkDataRepresentation;

public class SyntaxFloor implements Floor {

    private static final byte SYNTAX_IDENTIFIER = (byte) 0x0d;

    private PresentationSyntax syntax;

    private int majorVersion;

    public SyntaxFloor() {
        this((PresentationSyntax) null);
    }

    public SyntaxFloor(String syntax) {
        this(new PresentationSyntax(syntax));
    }

    public SyntaxFloor(PresentationSyntax syntax) {
        setSyntax(syntax);
    }

    public PresentationSyntax getSyntax() {
        return syntax;
    }

    public void setSyntax(PresentationSyntax syntax) {
        this.syntax = syntax;
    }

    public void setSyntax(String syntax) {
        setSyntax(new PresentationSyntax(syntax));
    }

    public byte getProtocolIdentifier() {
        return SYNTAX_IDENTIFIER;
    }

    public byte[] getLeftHand() {
        byte[] leftHand = new byte[19];
        leftHand[0] = getProtocolIdentifier();
        Format format = new Format(Format.LITTLE_ENDIAN |
                Format.ASCII_CHARACTER | Format.IEEE_FLOATING_POINT);
        NetworkDataRepresentation ndr = new NetworkDataRepresentation(
                format, new Buffer(16));
        PresentationSyntax syntax = getSyntax();
        ndr.writeElement(syntax.getUuid());
        System.arraycopy(ndr.getBuffer().copy(), 0, leftHand, 1, 16);
        format.writeUnsignedShort(syntax.getMajorVersion(), leftHand, 17);
        return leftHand;
    }

    public byte[] getRightHand() {
        byte[] rightHand = new byte[2];
        Format format = new Format(Format.LITTLE_ENDIAN |
                Format.ASCII_CHARACTER | Format.IEEE_FLOATING_POINT);
        format.writeUnsignedShort(getSyntax().getMinorVersion(), rightHand, 0);
        return rightHand;
    }

    public void decode(byte[] src, int lhsIndex, int lhsSize, int rhsIndex,
            int rhsSize) throws IOException {
        if (src[lhsIndex] != getProtocolIdentifier()) {
            throw new IOException("Invalid protocol identifier: " +
                    Integer.toHexString(src[lhsIndex]));
        }
        try {
            byte[] uuidBytes = new byte[16];
            System.arraycopy(src, lhsIndex + 1, uuidBytes, 0, 16);
            Format format = new Format(Format.LITTLE_ENDIAN |
                    Format.ASCII_CHARACTER | Format.IEEE_FLOATING_POINT);
            UUID uuid = new UUID();
            new NetworkDataRepresentation(format,
                    new Buffer(uuidBytes)).readElement(uuid);
            int majorVersion = format.readUnsignedShort(src, lhsIndex + 17);
            int minorVersion = format.readUnsignedShort(src, rhsIndex);
            setSyntax(new PresentationSyntax(uuid, majorVersion, minorVersion));
        } catch (Exception ex) {
            throw new IOException("Decoding error: " + ex);
        }
    }

}
