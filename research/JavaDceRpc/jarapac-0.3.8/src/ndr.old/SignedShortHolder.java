package rpc.ndr;

public class SignedShortHolder implements Holder {

    private Short value;

    private boolean embedded;

    public SignedShortHolder() {
        setValue(null);
    }

    public SignedShortHolder(short value) {
        setValue(new Short(value));
    }

    public SignedShortHolder(Short value) {
        setValue(value);
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = (value != null) ? (Short) value : new Short((short) 0);
    }

    public short getSignedShort() {
        return ((Short) getValue()).shortValue();
    }

    public void setSignedShort(short value) {
        setValue(new Short(value));
    }

    public int getAlignment() {
        return 2;
    }

    public boolean isEmbedded() {
        return embedded;
    }

    public void setEmbedded(boolean embedded) {
        this.embedded = embedded;
    }

    public void read(NetworkDataRepresentation ndr) {
        setValue(new Short(ndr.readSignedShort()));
    }

    public void write(NetworkDataRepresentation ndr) {
        Short value = (Short) getValue();
        if (value == null) return;
        ndr.writeSignedShort(value.shortValue());
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
