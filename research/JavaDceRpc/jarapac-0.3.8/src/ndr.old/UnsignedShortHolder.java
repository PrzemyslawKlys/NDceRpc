package rpc.ndr;

public class UnsignedShortHolder implements Holder {

    private Integer value;

    private boolean embedded;

    public UnsignedShortHolder() {
        setValue(null);
    }

    public UnsignedShortHolder(int value) {
        setValue(new Integer(value));
    }

    public UnsignedShortHolder(Integer value) {
        setValue(value);
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = (value != null) ? (Integer) value : new Integer(0);
    }

    public int getUnsignedShort() {
        return ((Integer) getValue()).intValue();
    }

    public void setUnsignedShort(int value) {
        setValue(new Integer(value));
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
        setValue(new Integer(ndr.readUnsignedShort()));
    }

    public void write(NetworkDataRepresentation ndr) {
        Integer value = (Integer) getValue();
        if (value == null) return;
        ndr.writeUnsignedShort(value.intValue());
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
