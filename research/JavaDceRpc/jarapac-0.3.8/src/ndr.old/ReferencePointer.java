package rpc.ndr;

public class ReferencePointer extends Pointer {

    private static long referenceIdentifier = 0xffffffffl;

    public ReferencePointer() {
        super();
    }

    public ReferencePointer(Element referent) {
        super(referent);
    }

    public ReferencePointer(long identifier, Element referent) {
        super(identifier, referent);
    }

    public int getAlignment() {
        return isEmbedded() ? 4 : referent.getAlignment();
    }

    public void read(NetworkDataRepresentation ndr) {
        if (!isEmbedded()) {
            ndr.readElement(referent);
            return;
        }
        identifier = ndr.readUnsignedLong();
        ndr.registerPointer(this);
    }

    public void write(NetworkDataRepresentation ndr) {
        if (!isEmbedded()) {
            ndr.writeElement(referent);
            return;
        }
        identifier = ReferencePointer.referenceIdentifier--;
        ndr.registerPointer(this);
        ndr.writeUnsignedLong(identifier);
    }

}
