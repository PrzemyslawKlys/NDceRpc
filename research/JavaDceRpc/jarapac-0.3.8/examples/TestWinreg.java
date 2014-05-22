import java.io.*;
import java.util.Properties;
import jcifs.util.Hexdump;
import ndr.*;
import rpc.*;

public class TestWinreg extends winreg {

	String servername;

	TestWinreg(String servername, Properties properties) {
		this.servername = servername;
		setAddress("ncacn_np:" + servername + "[\\PIPE\\winreg]");
		setProperties(properties);
	}
	rpc.policy_handle winregOpenHKLM() throws Exception {
		rpc.policy_handle handle = new rpc.policy_handle();
		handle.uuid = new rpc.uuid_t();
		RegOpenUnknown unknown = new RegOpenUnknown();

		RegOpenHKLM req = new RegOpenHKLM(unknown, 0x02000000, handle);
		call(0, req);
		if( req.retval != 0 ) {
			throw new Exception( "0x" + Hexdump.toHexString( req.retval, 8 ));
		}

		System.out.println(req.retval + ": " + handle.uuid.time_low);

		return handle;
	}
	rpc.policy_handle winregOpenKey(rpc.policy_handle handle, String name) throws Exception {
		rpc.policy_handle result = new rpc.policy_handle();
		result.uuid = new rpc.uuid_t();
		UnicodeString sub_key = new UnicodeString( name, true );

		RegOpenKey req = new RegOpenKey(handle, sub_key, 0, 0x00020019, result);
		call(0, req);
		if( req.retval != 0 ) {
			throw new Exception( "0x" + Hexdump.toHexString( req.retval, 8 ));
		}

		System.out.println(req.retval + ": " + result.uuid.time_low);

		return result;
	}
	void winregCloseKey(rpc.policy_handle handle) throws Exception {
		RegCloseKey req = new RegCloseKey(handle);
		call(0, req);
		if( req.retval != 0 ) {
			throw new Exception( "0x" + Hexdump.toHexString( req.retval, 8 ));
		}
	}

	public void winregQueryValue(rpc.policy_handle key, String name) throws Exception {
		NdrLong type = new NdrLong(0);
		NdrLong size = new NdrLong(1024);
		NdrLong length = new NdrLong(0);
		byte[] data = new byte[size.value];

		RegQueryValue req = new RegQueryValue(key,
				new UnicodeString( name, true ),
				type,
				data,
				size,
				length);
		call(0, req);
System.out.println( "type=" + type.value + ",length=" + length.value);
		if( req.retval != 0 ) {
			throw new Exception( "0x" + Hexdump.toHexString( req.retval, 8 ));
		}
	}

	public void winregQueryHKLM() throws Exception {
		rpc.policy_handle hklm = winregOpenHKLM();
		rpc.policy_handle key;

		key = winregOpenKey( hklm, "SOFTWARE\\Microsoft\\Internet Explorer" );
		winregQueryValue( key, "Version" );
		winregCloseKey( key );
		key = winregOpenKey( hklm, "SOFTWARE\\Microsoft\\Internet Explorer\\Document Caching" );
		winregQueryValue( key, "Number" );
		winregCloseKey( key );
		key = winregOpenKey( hklm, "SOFTWARE\\Microsoft\\Internet Explorer\\AboutURLs" );
		winregQueryValue( key, "Home" );
		winregCloseKey( key );

	}

	public static void main(String[] args) throws Exception {
		if( args.length < 1 ) {
			System.err.println( "usage: TestWinreg <servername> [<properties>]" );
			return;
		}

        Properties properties = null;
        if (args.length > 1) {
            properties = new Properties();
            properties.load(new FileInputStream(args[1]));
        }

		TestWinreg stub = new TestWinreg(args[0], properties);

		stub.winregQueryHKLM();
	}
}
