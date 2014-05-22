package ndr;

public class NdrLong extends NdrObject {

    public int value;

    public NdrLong(int value) {
        this.value = value;
    }

    public void encode(NetworkDataRepresentation ndr, NdrBuffer dst) throws NdrException {
        dst.enc_ndr_long(value);
    }
    public void decode(NetworkDataRepresentation ndr, NdrBuffer src) throws NdrException {
        value = src.dec_ndr_long();
    }
}

