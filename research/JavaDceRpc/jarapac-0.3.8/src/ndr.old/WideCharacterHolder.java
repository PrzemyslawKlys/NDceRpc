package rpc.ndr;

public class WideCharacterHolder implements Holder {

    private Character value;

    private boolean embedded;

    public WideCharacterHolder() {
        setValue(null);
    }

    public WideCharacterHolder(char value) {
        setValue(new Character(value));
    }

    public WideCharacterHolder(Character value) {
        setValue(value);
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = (value != null) ? (Character) value :
                new Character((char) 0);
    }

    public char getWideCharacter() {
        return ((Character) getValue()).charValue();
    }

    public void setWideCharacter(char value) {
        setValue(new Character(value));
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
        setValue(new Character(ndr.readWideCharacter()));
    }

    public void write(NetworkDataRepresentation ndr) {
        Character value = (Character) getValue();
        if (value == null) return;
        ndr.writeWideCharacter(value.charValue());
    }

    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException ex) {
            throw new IllegalStateException();
        }
    }

}
