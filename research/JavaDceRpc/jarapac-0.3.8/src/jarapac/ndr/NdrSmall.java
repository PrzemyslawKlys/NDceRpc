package ndr;

public class NdrSmall extends NdrObject {

    public int value;

    public NdrSmall(int value) {
        this.value = value & 0xFF;
    }

    public void encode(NetworkDataRepresentation ndr, NdrBuffer dst) throws NdrException {
        dst.enc_ndr_small(value);
    }
    public void decode(NetworkDataRepresentation ndr, NdrBuffer src) throws NdrException {
        value = src.dec_ndr_small();
    }
}

