import ndr.*;

public class Debug {

	static void hex(NdrBuffer buf) {
		int size = buf.deferred.index - buf.index;
		if ((buf.index + size) > buf.buf.length) {
			size = buf.buf.length - buf.index;
			System.out.println("short by " + ((buf.deferred.index - buf.index) - size));
		}
		System.out.println("index: " + buf.index + " size: " + size);
		jcifs.util.Hexdump.hexdump(System.out, buf.buf, buf.index, size);
	}
}
