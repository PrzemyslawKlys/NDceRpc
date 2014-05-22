package rpc.ndr;

public class OctetHolder implements Holder {

    private Byte value;

    private boolean embedded;

    public OctetHolder() {
        setValue(null);
    }

    public OctetHolder(byte value) {
        setValue(new Byte(value));
    }

    public OctetHolder(Byte value) {
        setValue(value);
    }

    public Object getValue() {
        return value;
    }

    public byte getOctet() {
        return ((Byte) getValue()).byteValue();
    }

    public void setOctet(byte value) {
        setValue(new Byte(value));
    }

    public void setValue(Object value) {
        this.value = (value != null) ? (Byte) value : new Byte((byte) 0);
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
        setValue(new Byte(ndr.readOctet()));
    }

    public void write(NetworkDataRepresentation ndr) {
        Byte value = (Byte) getValue();
        if (value == null) return;
        ndr.writeOctet(value.byteValue());
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
