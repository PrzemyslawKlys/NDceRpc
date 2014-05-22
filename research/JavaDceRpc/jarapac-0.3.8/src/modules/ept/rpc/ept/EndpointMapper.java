package rpc.ept;

import java.rmi.Remote;
import java.rmi.RemoteException;

import rpc.core.ContextHandle;
import rpc.core.InterfaceIdentifier;
import rpc.core.UUID;

import rpc.ndr.UnsignedLongHolder;

public interface EndpointMapper extends Remote {

    public void ept_insert(long num_ents, EndpointEntry[] entries,
            boolean replace, UnsignedLongHolder status) throws RemoteException;

    public void ept_delete(long num_ents, EndpointEntry[] entries,
            UnsignedLongHolder status) throws RemoteException;

    public void ept_lookup(long inquiry_type, UUID object,
            InterfaceIdentifier interface_id, long vers_option,
                    ContextHandle entry_handle, long max_ents,
                            UnsignedLongHolder num_ents,
                                    EndpointEntry[] entries,
                                            UnsignedLongHolder status)
                                                    throws RemoteException;

    public void ept_map(UUID object, ProtocolTower map_tower,
            ContextHandle entry_handle, long max_towers,
                    UnsignedLongHolder num_towers, ProtocolTower[] towers,
                            UnsignedLongHolder status) throws RemoteException;

    public void ept_lookup_handle_free(ContextHandle entry_handle,
            UnsignedLongHolder status) throws RemoteException;

    public void ept_inq_object(UUID object, UnsignedLongHolder status)
            throws RemoteException;

    public void ept_mgmt_delete(boolean object_speced, UUID object,
            ProtocolTower tower, UnsignedLongHolder status)
                    throws RemoteException;

}
