package rpc.ndr;

public abstract class Pointer implements Element {

    public long identifier;

    public Element referent;

    private boolean embedded;

    protected Pointer() {
        this(0l, null);
    }

    protected Pointer(Element referent) {
        this(0l, referent);
    }

    protected Pointer(long identifier, Element referent) {
        this.identifier = identifier;
        this.referent = referent;
    }

    public boolean isEmbedded() {
        return embedded;
    }

    public void setEmbedded(boolean embedded) {
        this.embedded = embedded;
    }

    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException ex) {
            throw new IllegalStateException();
        }
    }

}
