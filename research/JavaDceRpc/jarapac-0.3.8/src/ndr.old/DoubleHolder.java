package rpc.ndr;

public class DoubleHolder implements Holder {

    private Double value;

    private boolean embedded;

    public DoubleHolder() {
        setValue(null);
    }

    public DoubleHolder(double value) {
        setValue(new Double(value));
    }

    public DoubleHolder(Double value) {
        setValue(value);
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = (value != null) ? (Double) value : new Double(0d);
    }

    public double getDouble() {
        return ((Double) getValue()).doubleValue();
    }

    public void setDouble(double value) {
        setValue(new Double(value));
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
        setValue(new Double(ndr.readDouble()));
    }

    public void write(NetworkDataRepresentation ndr) {
        Double value = (Double) getValue();
        if (value == null) return;
        ndr.writeDouble(value.doubleValue());
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
