import java.io.*;
import java.util.Properties;
import java.util.Date;
import jcifs.util.Hexdump;
import ndr.*;
import rpc.*;

public class TestSrvsvc extends srvsvc {

	String servername;

	TestSrvsvc(String servername, Properties properties) {
		this.servername = servername;
		setAddress("ncacn_np:" + servername + "[\\PIPE\\srvsvc]");
		setProperties(properties);
	}

	public void doShareEnum() throws Exception {
		ShareInfoCtr1 info = new ShareInfoCtr1();
		ShareEnumAll req = new ShareEnumAll("\\\\" + servername, 1, info, -1, 0, 0);

		call(0, req);
		if( req.retval != 0 ) {
			throw new Exception( "0x" + Hexdump.toHexString( req.retval, 8 ));
		}

		for (int i = 0; i < info.count; i++) {
			ShareInfo1 i1 = info.array[i];
			System.out.println("info[" + i + "]: " + i1.netname + " " + Hexdump.toHexString( i1.type, 8 ) + " " + i1.remark);
		}
	}
	public void doServerGetInfo() throws Exception {
		ServerInfo100 info = new ServerInfo100();
		ServerGetInfo req = new ServerGetInfo("\\\\" + servername, 100, info);

		call(0, req);
		if( req.retval != 0 ) {
			throw new Exception( "0x" + Hexdump.toHexString( req.retval, 8 ));
		}

		System.out.println("platform_id: " + info.platform_id + " name: " + info.name);
	}
	public Date doRemoteTOD() throws Exception {
		TimeOfDayInfo info = new TimeOfDayInfo();
		RemoteTOD req = new RemoteTOD("\\\\" + servername, info);

		call(0, req);
		if( req.retval != 0 ) {
			throw new Exception( "0x" + Hexdump.toHexString( req.retval, 8 ));
		}

		Date d = new Date( info.elapsedt * 1000L );
		System.out.println( "Time: " + d );

		return d;
	}

	public void doAll() throws Exception {
		doShareEnum();
		doServerGetInfo();
		doRemoteTOD();
	}

	public static void main(String[] args) throws Exception {
		if( args.length < 1 ) {
			System.err.println( "usage: TestSrvsvc <servername> [<properties>]" );
			return;
		}

        Properties properties = null;
        if (args.length > 1) {
            properties = new Properties();
            properties.load(new FileInputStream(args[1]));
        }

		TestSrvsvc stub = new TestSrvsvc(args[0], properties);

		stub.doAll();
	}
}
