package rpc.ndr;

public class PointerHelper implements ArrayHelper {

    private final Pointer templateObject;

    public PointerHelper(Pointer templateObject) {
        this.templateObject = templateObject;
        if (templateObject == null) {
            throw new NullPointerException("Null template.");
        }
    }

    public Class getType() {
        return templateObject.referent.getClass();
    }

    public int getAlignment() {
        return templateObject.getAlignment();
    }

    public Pointer getTemplate() {
        return templateObject;
    }

    public void readArray(Object array, int offset, int length,
            NetworkDataRepresentation ndr) {
        if (array == null) return;
        Element[] elementArray = (Element[]) array;
        length += offset;
        for (int i = offset; i < length; i++) {
            Pointer pointer = (Pointer) templateObject.clone();
            pointer.setEmbedded(true);
            ndr.readElement(pointer);
            elementArray[i] = pointer.referent;
        }
    }

    public void writeArray(Object array, int offset, int length,
            NetworkDataRepresentation ndr) {
        if (array == null) return;
        Element[] elementArray = (Element[]) array;
        length += offset;
        for (int i = offset; i < length; i++) {
            Pointer pointer = (Pointer) templateObject.clone();
            pointer.referent = elementArray[i];
            pointer.identifier = ndr.getIdentifier(pointer.referent);
            ndr.writeElement(pointer);
        }
    }

}
