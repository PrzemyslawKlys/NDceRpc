package rpc.ndr;

public class UnsignedSmallHolder implements Holder {

    private Short value;

    private boolean embedded;

    public UnsignedSmallHolder() {
        setValue(null);
    }

    public UnsignedSmallHolder(short value) {
        setValue(new Short(value));
    }

    public UnsignedSmallHolder(Short value) {
        setValue(value);
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = (value != null) ? (Short) value : new Short((short) 0);
    }

    public short getUnsignedSmall() {
        return ((Short) getValue()).shortValue();
    }

    public void setUnsignedSmall(short value) {
        setValue(new Short(value));
    }

    public int getAlignment() {
        return 1;
    }

    public boolean isEmbedded() {
        return embedded;
    }

    public void setEmbedded(boolean embedded) {
        this.embedded = embedded;
    }

    public void read(NetworkDataRepresentation ndr) {
        setValue(new Short(ndr.readUnsignedSmall()));
    }

    public void write(NetworkDataRepresentation ndr) {
        Short value = (Short) getValue();
        if (value == null) return;
        ndr.writeUnsignedSmall(value.shortValue());
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
