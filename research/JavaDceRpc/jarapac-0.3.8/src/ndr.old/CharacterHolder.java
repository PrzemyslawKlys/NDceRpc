package rpc.ndr;

public class CharacterHolder implements Holder {

    private Character value;

    private boolean embedded;

    public CharacterHolder() {
        setValue(null);
    }

    public CharacterHolder(char value) {
        setValue(new Character(value));
    }

    public CharacterHolder(Character value) {
        setValue(value);
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = (value != null) ? (Character) value :
                new Character((char) 0);
    }

    public char getCharacter() {
        return ((Character) getValue()).charValue();
    }

    public void setCharacter(char value) {
        setValue(new Character(value));
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
        setValue(new Character(ndr.readCharacter()));
    }

    public void write(NetworkDataRepresentation ndr) {
        Character value = (Character) getValue();
        if (value == null) return;
        ndr.writeCharacter(value.charValue());
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
