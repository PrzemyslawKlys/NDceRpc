import java.util.*;
import jcifs.util.Hexdump;

public class SID extends rpc.sid_t {

	public static String toTextual(rpc.sid_t sid) {
		String ret = "S-" + sid.revision + "-";

		if (sid.identifier_authority[0] != 0 || sid.identifier_authority[1] != 0) {
			ret += "0x";
			for (int i = 0; i < 6; i++)
				ret += Hexdump.toHexString(sid.identifier_authority[i], 2);
		} else {
			int shift = 0;
			long id = 0;
			for (int i = 5; i > 1; i--) {
				id += ((long) sid.identifier_authority[i]) << shift;
				shift += 8;
			}
			ret += id;
		}

		for (int i = 0; i < sid.sub_authority_count ; i++)
			ret += "-" + sid.sub_authority[i];

		return ret;
	}

	public static rpc.sid_t toSID(String textual) throws Exception {
		rpc.sid_t sid = new rpc.sid_t();
		StringTokenizer st = new StringTokenizer(textual, "-");
		if (st.countTokens() < 3 || !st.nextToken().equals("S"))
			// need S-N-M
			throw new Exception("bad format");

		sid.revision = Byte.parseByte(st.nextToken());
		String tmp = st.nextToken();
		long id = 0;
		if (tmp.startsWith("0x"))
			id = Long.parseLong(tmp.substring(2), 16);
		else
			id = Long.parseLong(tmp);

		sid.identifier_authority = new byte[6];
		for (int i = 5; id > 0;  i--) {
			sid.identifier_authority[i] = (byte) (id % 256);
			id >>= 8;
		}

		sid.sub_authority_count = (byte) st.countTokens();
		if (sid.sub_authority_count > 0) {
			sid.sub_authority = new int[sid.sub_authority_count];
			for (int i = 0; i < sid.sub_authority_count; i++)
				sid.sub_authority[i] = (int)(Long.parseLong(st.nextToken()) & 0xFFFFFFFFL);
		}
		return sid;
	}
}

