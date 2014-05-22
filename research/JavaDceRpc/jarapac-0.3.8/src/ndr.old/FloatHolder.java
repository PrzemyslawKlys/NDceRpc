package rpc.ndr;

public class FloatHolder implements Holder {

    private Float value;

    private boolean embedded;

    public FloatHolder() {
        setValue(null);
    }

    public FloatHolder(float value) {
        setValue(new Float(value));
    }

    public FloatHolder(Float value) {
        setValue(value);
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = (value != null) ? (Float) value : new Float(0f);
    }

    public float getFloat() {
        return ((Float) getValue()).floatValue();
    }

    public void setFloat(float value) {
        setValue(new Float(value));
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
        setValue(new Float(ndr.readFloat()));
    }

    public void write(NetworkDataRepresentation ndr) {
        Float value = (Float) getValue();
        if (value == null) return;
        ndr.writeFloat(value.floatValue());
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
