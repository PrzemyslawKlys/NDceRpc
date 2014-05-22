public class UnicodeString extends rpc.unicode_string {

	boolean zterm;

	public UnicodeString(boolean zterm) {
		this.zterm = zterm;
	}
	public UnicodeString(rpc.unicode_string rus, boolean zterm) {
		this.length = rus.length;
		this.maximum_length = rus.maximum_length;
		this.buffer = rus.buffer;
		this.zterm = zterm;
	}

	public UnicodeString(String str, boolean zterm) {
		this.zterm = zterm;

		int len = str.length();
		int zt = zterm ? 1 : 0;

		length = maximum_length = (short)((len + zt) * 2);
		buffer = new short[len + zt];

		int i;
		for (i = 0; i < len; i++) {
			buffer[i] = (short)str.charAt(i);
		}
		if (zterm) {
			buffer[i] = (short)0;
		}
	}

	public String toString() {
		int len = length / 2 - (zterm ? 1 : 0);
		char[] ca = new char[len];
		for (int i = 0; i < len; i++) {
			ca[i] = (char)buffer[i];
		}
		return new String(ca, 0, len);
	}
}
