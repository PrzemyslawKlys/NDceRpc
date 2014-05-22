package rpc.ept;

import java.rmi.RemoteException;

import rpc.RpcException;
import rpc.Endpoint;
import rpc.Stub;

import rpc.core.ContextHandle;
import rpc.core.InterfaceIdentifier;
import rpc.core.UUID;

import rpc.ndr.ConformantArray;
import rpc.ndr.ConformantVaryingArray;
import rpc.ndr.ElementHelper;
import rpc.ndr.FullPointer;
import rpc.ndr.InputParameters;
import rpc.ndr.OutputParameters;
import rpc.ndr.PointerHelper;
import rpc.ndr.UnsignedLongHolder;

public class EndpointMapper_Stub extends Stub implements EndpointMapper {

    public void ept_insert(long num_ents, EndpointEntry[] entries,
            boolean replace, UnsignedLongHolder status) throws RemoteException {
        InputParameters input = new InputParameters();
        input.add(new UnsignedLongHolder(num_ents));
        input.add(new ConformantArray(entries,
                new ElementHelper(new EndpointEntry())));
        input.add(new UnsignedLongHolder(replace ? 1 : 0));
        OutputParameters output = new OutputParameters();
        output.add(status);
        try {
            call(0, 0, input, output);
        } catch (RpcException ex) {
            throw new RemoteException("RPC error.", ex);
        } catch (Exception ex) {
            throw new RemoteException("Unknown error.", ex);
        }
    }

    public void ept_delete(long num_ents, EndpointEntry[] entries,
            UnsignedLongHolder status) throws RemoteException {
        InputParameters input = new InputParameters();
        input.add(new UnsignedLongHolder(num_ents));
        input.add(new ConformantArray(entries,
                new ElementHelper(new EndpointEntry())));
        OutputParameters output = new OutputParameters();
        output.add(status);
        try {
            call(0, 1, input, output);
        } catch (RpcException ex) {
            throw new RemoteException("RPC error.", ex);
        } catch (Exception ex) {
            throw new RemoteException("Unknown error.", ex);
        }
    }

    public void ept_lookup(long inquiry_type, UUID object,
            InterfaceIdentifier interface_id, long vers_option,
            ContextHandle entry_handle, long max_ents,
            UnsignedLongHolder num_ents, EndpointEntry[] entries,
            UnsignedLongHolder status) throws RemoteException {
        InputParameters input = new InputParameters();
        input.add(new UnsignedLongHolder(inquiry_type));
        input.add(new FullPointer(object));
        input.add(new FullPointer(interface_id));
        input.add(new UnsignedLongHolder(vers_option));
        input.add(entry_handle);
        input.add(new UnsignedLongHolder(max_ents));
        OutputParameters output = new OutputParameters();
        output.add(entry_handle);
        output.add(num_ents);
        output.add(new ConformantVaryingArray(entries,
                new ElementHelper(new EndpointEntry())));
        output.add(status);
        try {
            call(Endpoint.IDEMPOTENT, 2, input, output);
        } catch (RpcException ex) {
            throw new RemoteException("RPC error.", ex);
        } catch (Exception ex) {
            throw new RemoteException("Unknown error.", ex);
        }
    }

    public void ept_map(UUID object, ProtocolTower map_tower,
            ContextHandle entry_handle, long max_towers,
                    UnsignedLongHolder num_towers, ProtocolTower[] towers,
                            UnsignedLongHolder status) throws RemoteException {
        InputParameters input = new InputParameters();
        input.add(new FullPointer(object));
        input.add(new FullPointer(map_tower));
        input.add(entry_handle);
        input.add(new UnsignedLongHolder(max_towers));
        OutputParameters output = new OutputParameters();
        output.add(entry_handle);
        output.add(num_towers);
        output.add(new ConformantVaryingArray(towers,
                new PointerHelper(new FullPointer(new ProtocolTower()))));
        output.add(status);
        try {
            call(Endpoint.IDEMPOTENT, 3, input, output);
        } catch (RpcException ex) {
            throw new RemoteException("RPC error.", ex);
        } catch (Exception ex) {
            throw new RemoteException("Unknown error.", ex);
        }
    }

    public void ept_lookup_handle_free(ContextHandle entry_handle,
            UnsignedLongHolder status) throws RemoteException {
        InputParameters input = new InputParameters();
        input.add(entry_handle);
        OutputParameters output = new OutputParameters();
        output.add(entry_handle);
        output.add(status);
        try {
            call(0, 4, input, output);
        } catch (RpcException ex) {
            throw new RemoteException("RPC error.", ex);
        } catch (Exception ex) {
            throw new RemoteException("Unknown error.", ex);
        }
    }

    public void ept_inq_object(UUID ept_object, UnsignedLongHolder status)
            throws RemoteException {
        InputParameters input = new InputParameters();
        OutputParameters output = new OutputParameters();
        output.add(ept_object);
        output.add(status);
        try {
            call(Endpoint.IDEMPOTENT, 5, input, output);
        } catch (RpcException ex) {
            throw new RemoteException("RPC error.", ex);
        } catch (Exception ex) {
            throw new RemoteException("Unknown error.", ex);
        }
    }

    public void ept_mgmt_delete(boolean object_speced, UUID object,
            ProtocolTower tower, UnsignedLongHolder status)
                    throws RemoteException {
        InputParameters input = new InputParameters();
        input.add(new UnsignedLongHolder(object_speced ? 1 : 0));
        input.add(new FullPointer(object));
        input.add(new FullPointer(tower));
        OutputParameters output = new OutputParameters();
        output.add(status);
        try {
            call(0, 6, input, output);
        } catch (RpcException ex) {
            throw new RemoteException("RPC error.", ex);
        } catch (Exception ex) {
            throw new RemoteException("Unknown error.", ex);
        }
    }

    protected String getSyntax() {
        return "e1af8308-5d1f-11c9-91a4-08002b14a0fa:3.0";
    }

}
