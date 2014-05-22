package rpc.ndr;

public class FullPointer extends Pointer {

    public FullPointer() {
        super();
    }

    public FullPointer(Element referent) {
        super(referent);
    }

    public FullPointer(long identifier, Element referent) {
        super(identifier, referent);
    }

    public int getAlignment() {
        return 4;
    }

    public void read(NetworkDataRepresentation ndr) {
        identifier = ndr.readUnsignedLong();
        if (identifier == 0) {
            referent = null;
            return;
        }
        Element current = ndr.getReferent(identifier);
        if (current == null) ndr.registerPointer(this);
    }

    public void write(NetworkDataRepresentation ndr) {
        long identifier = ndr.getIdentifier(referent);
        ndr.writeUnsignedLong(identifier);
        // return if null or already encountered.
        if (identifier == 0l || identifier <= this.identifier) return;
        this.identifier = identifier;
        ndr.registerPointer(this);
    }

}
