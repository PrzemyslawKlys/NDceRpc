package rpc.ndr;

public class SignedHyperHolder implements Holder {

    private Long value;

    private boolean embedded;

    public SignedHyperHolder() {
        setValue(null);
    }

    public SignedHyperHolder(long value) {
        setValue(new Long(value));
    }

    public SignedHyperHolder(Long value) {
        setValue(value);
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = (value != null) ? (Long) value : new Long(0l);
    }

    public long getSignedHyper() {
        return ((Long) getValue()).longValue();
    }

    public void setSignedHyper(long value) {
        setValue(new Long(value));
    }

    public int getAlignment() {
        return 8;
    }

    public boolean isEmbedded() {
        return embedded;
    }

    public void setEmbedded(boolean embedded) {
        this.embedded = embedded;
    }

    public void read(NetworkDataRepresentation ndr) {
        setValue(new Long(ndr.readSignedHyper()));
    }

    public void write(NetworkDataRepresentation ndr) {
        Long value = (Long) getValue();
        if (value == null) return;
        ndr.writeSignedHyper(value.longValue());
    }

    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException ex) {
            throw new IllegalStateException();
        }
    }

    public String toString() {
        return String.valueOf(getValue());
    }

}
