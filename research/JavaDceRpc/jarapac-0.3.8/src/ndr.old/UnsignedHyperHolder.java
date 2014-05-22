package rpc.ndr;

import java.math.BigInteger;

public class UnsignedHyperHolder implements Holder {

    private BigInteger value;

    private boolean embedded;

    public UnsignedHyperHolder() {
        setValue(null);
    }

    public UnsignedHyperHolder(BigInteger value, int type) {
        setValue(value);
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = (value != null) ? (BigInteger) value :
                new BigInteger("0");
    }

    public BigInteger getUnsignedHyper() {
        return (BigInteger) getValue();
    }

    public void setUnsignedHyper(BigInteger value) {
        setValue(value);
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
        setValue(ndr.readUnsignedHyper());
    }

    public void write(NetworkDataRepresentation ndr) {
        BigInteger value = (BigInteger) getValue();
        if (value == null) return;
        ndr.writeUnsignedHyper(value);
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
