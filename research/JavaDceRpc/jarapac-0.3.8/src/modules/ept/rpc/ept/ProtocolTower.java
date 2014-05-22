package rpc.ept;

import java.io.IOException;

import rpc.ndr.ArrayHelper;
import rpc.ndr.Conformant;
import rpc.ndr.ConformantArray;
import rpc.ndr.ConformantStructure;
import rpc.ndr.Format;
import rpc.ndr.NetworkDataRepresentation;
import rpc.ndr.UnsignedLongHolder;
import rpc.ndr.UserDefinedType;

public class ProtocolTower extends UserDefinedType implements Conformant {

    private static final int TOWERLENGTH_INDEX = 0;

    private static final int TOWER_INDEX = 1;

    public ProtocolTower() {
        structure = new ConformantStructure();
        structure.add(new UnsignedLongHolder());
        structure.add(new ConformantArray(new byte[0],
                ArrayHelper.OCTET_HELPER));
    }

    public long getTowerLength() {
        return ((UnsignedLongHolder)
                structure.get(TOWERLENGTH_INDEX)).getUnsignedLong();
    }

    public void setTowerLength(long towerLength) {
        ((UnsignedLongHolder)
                structure.get(TOWERLENGTH_INDEX)).setUnsignedLong(towerLength);
    }

    public byte[] getTower() {
        return (byte[]) ((ConformantArray)
                structure.get(TOWER_INDEX)).getArray();
    }

    public void setTower(byte[] tower) {
        ((ConformantArray) structure.get(TOWER_INDEX)).setArray(tower);
    }

    public Floor[] getFloors() {
        byte[] tower = getTower();
        Format format = new Format(Format.LITTLE_ENDIAN |
                Format.ASCII_CHARACTER | Format.IEEE_FLOATING_POINT);
        int index = 0;
        int floorCount = format.readUnsignedShort(tower, index);
        index += 2;
        Floor[] floors = new Floor[floorCount];
        for (int i = 0; i < floorCount; i++) {
            int lhsSize = format.readUnsignedShort(tower, index);
            index += 2;
            int lhsIndex = index;
            index += lhsSize;
            int rhsSize = format.readUnsignedShort(tower, index);
            index += 2;
            int rhsIndex = index;
            index += rhsSize;
            byte protocolIdentifier = tower[lhsIndex];
            switch (protocolIdentifier) {
            case 0x0d:
                floors[i] = new SyntaxFloor();
                break;
            case 0x07:
                floors[i] = new TCPPortFloor();
                break;
            case 0x08:
                floors[i] = new UDPPortFloor();
                break;
            case 0x09:
                floors[i] = new IPAddressFloor();
                break;
            case 0x0a:
                floors[i] = new ConnectionlessFloor();
                break;
            case 0x0b:
                floors[i] = new ConnectionOrientedFloor();
                break;
                /*
            case 0x10:
                floors[i] = new NamedPipesFloor();
                break;
            case 0x22:
                floors[i] = new NetBiosFloor();
                break;
                */
            default:
                throw new IllegalArgumentException("Unrecognized floor type: " +
                        Integer.toHexString(protocolIdentifier));
            }
            try {
                floors[i].decode(tower, lhsIndex, lhsSize, rhsIndex, rhsSize);
            } catch (IOException ex) {
                throw new IllegalArgumentException("Error decoding floor: " +
                        ex);
            }
        }
        return floors;
    }

    public void setFloors(Floor[] floors) {
        int floorCount = floors.length;
        int towerSize = 2 + (floorCount * 4);
        for (int i = 0; i < floorCount; i++) {
            towerSize += floors[i].getLeftHand().length +
                    floors[i].getRightHand().length;
        }
        byte[] tower = new byte[towerSize];
        Format format = new Format(Format.LITTLE_ENDIAN |
                Format.ASCII_CHARACTER | Format.IEEE_FLOATING_POINT);
        int index = 0;
        format.writeUnsignedShort(floorCount, tower, index);
        index += 2;
        for (int i = 0; i < floorCount; i++) {
            byte[] hand = floors[i].getLeftHand();
            int handSize = hand.length;
            format.writeUnsignedShort(handSize, tower, index);
            index += 2;
            System.arraycopy(hand, 0, tower, index, handSize);
            index += handSize;
            hand = floors[i].getRightHand();
            handSize = hand.length;
            format.writeUnsignedShort(handSize, tower, index);
            index += 2;
            System.arraycopy(hand, 0, tower, index, handSize);
            index += handSize;
        }
        setTower(tower);
    }

    public void readConformance(NetworkDataRepresentation ndr) {
        ((ConformantStructure) structure).readConformance(ndr);
    }

    public void writeConformance(NetworkDataRepresentation ndr) {
        ((ConformantStructure) structure).writeConformance(ndr);
    }

}
