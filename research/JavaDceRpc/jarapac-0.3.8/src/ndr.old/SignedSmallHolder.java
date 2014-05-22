package rpc.ndr;

public class SignedSmallHolder implements Holder {

    private Byte value;

    private boolean embedded;

    public SignedSmallHolder() {
        setValue(null);
    }

    public SignedSmallHolder(byte value) {
        setValue(new Byte(value));
    }

    public SignedSmallHolder(Byte value) {
        setValue(value);
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = (value != null) ? (Byte) value : new Byte((byte) 0);
    }

    public byte getSignedSmall() {
        return ((Byte) getValue()).byteValue();
    }

    public void setSignedSmall(byte value) {
        setValue(new Byte(value));
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
        setValue(new Byte(ndr.readSignedSmall()));
    }

    public void write(NetworkDataRepresentation ndr) {
        Byte value = (Byte) getValue();
        if (value == null) return;
        ndr.writeSignedSmall(value.byteValue());
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
