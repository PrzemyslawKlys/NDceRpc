import java.io.FileInputStream;

import java.util.Properties;

import rpc.StubFactory;

import rpc.core.ContextHandle;
import rpc.core.UUID;

import rpc.ept.ConnectionOrientedFloor;
import rpc.ept.EndpointMapper;
import rpc.ept.Floor;
import rpc.ept.IPAddressFloor;
import rpc.ept.ProtocolTower;
import rpc.ept.SyntaxFloor;
import rpc.ept.TCPPortFloor;

import rpc.ndr.NetworkDataRepresentation;
import rpc.ndr.UnsignedLongHolder;

public class Mapper {

    public static void main(String[] args) throws Exception {
        String address = args[0];
        Properties properties = null;
        if (args.length > 1) {
            properties = new Properties();
            properties.load(new FileInputStream(args[1]));
        }
        EndpointMapper mapper = (EndpointMapper)
                StubFactory.newInstance().createStub(address, properties,
                        EndpointMapper.class);
        ProtocolTower tower = new ProtocolTower();
        tower.setFloors(new Floor[] {
            new SyntaxFloor("e1af8308-5d1f-11c9-91a4-08002b14a0fa:3.0"),
            new SyntaxFloor(NetworkDataRepresentation.NDR_SYNTAX),
            new ConnectionOrientedFloor(),
            new TCPPortFloor(),
            new IPAddressFloor()
        });
        tower.setTowerLength(tower.getTower().length);
        ContextHandle handle = new ContextHandle();
        UnsignedLongHolder status = new UnsignedLongHolder();
        ProtocolTower[] matchedTowers = new ProtocolTower[4];
        UnsignedLongHolder matchedTowerCount = new UnsignedLongHolder();
        mapper.ept_map(new UUID(UUID.NIL_UUID), tower, handle,
                matchedTowers.length, matchedTowerCount, matchedTowers, status);
    }

}
