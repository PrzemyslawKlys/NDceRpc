package rpc.ndr;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

public class Union implements Element {

    public final Holder discriminant;

    private final Map selections = new Hashtable();

    private boolean embedded;

    public Union(Holder discriminant) {
        this.discriminant = discriminant;
        if (discriminant == null) {
            throw new NullPointerException("Null discriminant tag prohibited.");
        }
    }

    public void addSelection(Object discriminant, Element member) {
        member.setEmbedded(isEmbedded());
        selections.put(discriminant, member);
    }

    public Element getMember() {
        return (Element) selections.get(discriminant.getValue());
    }

    public int getAlignment() {
        return discriminant.getAlignment();
    }

    public int getMaxAlignment() {
        int maxAlign = discriminant.getAlignment();
        if (maxAlign == 8) return 8;
        Element member;
        int alignment;
        Iterator values = selections.values().iterator();
        while (values.hasNext()) {
            member = (Element) values.next();
            alignment = member.getAlignment();
            if (alignment == 8) return 8;
            if (alignment > maxAlign) maxAlign = alignment;
        }
        return maxAlign;
    }

    public boolean isEmbedded() {
        return embedded;
    }

    public void setEmbedded(boolean embedded) {
        if (embedded == this.embedded) return;
        this.embedded = embedded;
        Iterator values = selections.values().iterator();
        while (values.hasNext()) {
            ((Element) values.next()).setEmbedded(embedded);
        }
    }

    public void read(NetworkDataRepresentation ndr) {
        ndr.readElement(discriminant);
        ndr.readElement(getMember());
    }

    public void write(NetworkDataRepresentation ndr) {
        ndr.writeElement(discriminant);
        ndr.writeElement(getMember());
    }

    // maybe need to deep clone map?
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException ex) {
            throw new IllegalStateException();
        }
    }

}
