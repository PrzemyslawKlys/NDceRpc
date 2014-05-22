package ndr;

public class NdrShort extends NdrObject {

    public int value;

    public NdrShort(int value) {
        this.value = value & 0xFF;
    }

    public void encode(NetworkDataRepresentation ndr, NdrBuffer dst) throws NdrException {
        dst.enc_ndr_short(value);
    }
    public void decode(NetworkDataRepresentation ndr, NdrBuffer src) throws NdrException {
        value = src.dec_ndr_short();
    }
}

