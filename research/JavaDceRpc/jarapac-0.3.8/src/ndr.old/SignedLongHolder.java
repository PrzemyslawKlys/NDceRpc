package rpc.ndr;

public class SignedLongHolder implements Holder {

    private Integer value;

    private boolean embedded;

    public SignedLongHolder() {
        setValue(null);
    }

    public SignedLongHolder(int value) {
        setValue(new Integer(value));
    }

    public SignedLongHolder(Integer value) {
        setValue(value);
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = (value != null) ? (Integer) value : new Integer(0);
    }

    public int getSignedLong() {
        return ((Integer) getValue()).intValue();
    }

    public void setSignedLong(int value) {
        setValue(new Integer(value));
    }

    public int getAlignment() {
        return 4;
    }

    public boolean isEmbedded() {
        return embedded;
    }

    public void setEmbedded(boolean embedded) {
        this.embedded = embedded;
    }

    public void read(NetworkDataRepresentation ndr) {
        setValue(new Integer(ndr.readSignedLong()));
    }

    public void write(NetworkDataRepresentation ndr) {
        Integer value = (Integer) getValue();
        if (value == null) return;
        ndr.writeSignedLong(value.intValue());
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
