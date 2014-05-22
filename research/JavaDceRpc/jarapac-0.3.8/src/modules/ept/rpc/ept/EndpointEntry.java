package rpc.ept;

import rpc.core.UUID;

import rpc.ndr.FullPointer;
import rpc.ndr.Pointer;
import rpc.ndr.Structure;
import rpc.ndr.UserDefinedType;
import rpc.ndr.VaryingString;

public class EndpointEntry extends UserDefinedType {

    private static final int OBJECT_INDEX = 0;

    private static final int TOWER_INDEX = 1;

    private static final int ANNOTATION_INDEX = 2;

    public EndpointEntry() {
        structure = new Structure();
        structure.add(new UUID());
        structure.add(new FullPointer(new ProtocolTower()));
        structure.add(new VaryingString());
    }

    public UUID getObject() {
        return (UUID) structure.get(OBJECT_INDEX);
    }

    public void setObject(UUID object) {
        structure.set(OBJECT_INDEX, object);
    }

    public ProtocolTower getTower() {
        return (ProtocolTower) ((Pointer) structure.get(TOWER_INDEX)).referent;
    }

    public void setTower(ProtocolTower tower) {
        ((Pointer) structure.get(TOWER_INDEX)).referent = tower;
    }

    public String getAnnotation() {
        return ((VaryingString) structure.get(ANNOTATION_INDEX)).getString();
    }

    public void setAnnotation(String annotation) {
        ((VaryingString) structure.get(ANNOTATION_INDEX)).setString(annotation);
    }

}
