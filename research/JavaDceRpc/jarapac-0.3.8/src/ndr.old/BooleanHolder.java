package rpc.ndr;

public class BooleanHolder implements Holder {

    private Boolean value;

    private boolean embedded;

    public BooleanHolder() {
        setValue(null);
    }

    public BooleanHolder(boolean value) {
        setValue(new Boolean(value));
    }

    public BooleanHolder(Boolean value) {
        setValue(value);
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = (value != null) ? (Boolean) value : Boolean.FALSE;
    }

    public boolean getBoolean() {
        return ((Boolean) getValue()).booleanValue();
    }

    public void setBoolean(boolean value) {
        setValue(new Boolean(value));
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
        setValue(new Boolean(ndr.readBoolean()));
    }

    public void write(NetworkDataRepresentation ndr) {
        Boolean value = (Boolean) getValue();
        if (value == null) return;
        ndr.writeBoolean(value.booleanValue());
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
