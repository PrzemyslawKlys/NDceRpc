package rpc.ndr;

public class UnsignedLongHolder implements Holder {

    private Long value;

    private boolean embedded;

    public UnsignedLongHolder() {
        setValue(null);
    }

    public UnsignedLongHolder(long value) {
        setValue(new Long(value));
    }

    public UnsignedLongHolder(Long value) {
        setValue(value);
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = (value != null) ? (Long) value : new Long(0l);
    }

    public long getUnsignedLong() {
        return ((Long) getValue()).longValue();
    }

    public void setUnsignedLong(long value) {
        setValue(new Long(value));
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
        setValue(new Long(ndr.readUnsignedLong()));
    }

    public void write(NetworkDataRepresentation ndr) {
        Long value = (Long) getValue();
        if (value == null) return;
        ndr.writeUnsignedLong(value.longValue());
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
