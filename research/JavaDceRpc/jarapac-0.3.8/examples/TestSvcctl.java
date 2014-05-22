import java.io.*;
import java.util.Properties;
import java.util.Date;
import jcifs.util.Hexdump;
import jcifs.util.Encdec;
import ndr.*;
import rpc.*;

public class TestSvcctl extends svcctl {

	static final int BUFSIZ = 16384;

	String servername;

	TestSvcctl(String servername, Properties properties) {
		this.servername = servername;
		setAddress("ncacn_np:" + servername + "[\\PIPE\\svcctl]");
		setProperties(properties);
	}
	rpc.policy_handle svcctlOpenSCManager() throws Exception {
		rpc.policy_handle handle = new rpc.policy_handle();
		handle.uuid = new rpc.uuid_t();

		OpenSCManager req = new OpenSCManager( "\\\\" + servername,
				null, SC_MANAGER_ALL_ACCESS, handle);
		call(0, req);
		if( req.retval != 0 ) {
			throw new Exception( "0x" + Hexdump.toHexString( req.retval, 8 ));
		}

		return handle;
	}

    public static class ServiceStatus extends NdrObject {

		public String service_name;
		public String display_name;
        public int service_type;
        public int current_state;
        public int controls_accepted;
        public int win32_exit_code;
        public int service_specific_exit_code;
        public int check_point;
        public int wait_hint;

        public int decode( byte[] src, int si, int rel_start, char[] buf ) throws IOException {
			int start = si, rel;

			rel = Encdec.dec_uint32le( src, si ); si += 4;
			service_name = Encdec.dec_ucs2le( src, rel_start + rel, src.length, buf );
			rel = Encdec.dec_uint32le( src, si ); si += 4;
			display_name = Encdec.dec_ucs2le( src, rel_start + rel, src.length, buf );

            service_type = Encdec.dec_uint32le( src, si ); si += 4;
            current_state = Encdec.dec_uint32le( src, si ); si += 4;
            controls_accepted = Encdec.dec_uint32le( src, si ); si += 4;
            win32_exit_code = Encdec.dec_uint32le( src, si ); si += 4;
            service_specific_exit_code = Encdec.dec_uint32le( src, si ); si += 4;
            check_point = Encdec.dec_uint32le( src, si ); si += 4;
            wait_hint = Encdec.dec_uint32le( src, si ); si += 4;

			return si - start;
        }
    }

	void svcctlEnumServicesStatus(rpc.policy_handle scm_handle) throws Exception {
		byte[] service = new byte[BUFSIZ];
		EnumServicesStatus req = new EnumServicesStatus(scm_handle,
				0x30, 0x3, BUFSIZ, service, 0, 0, 0);
		call(0, req);
		if( req.retval != 0 ) {
			throw new Exception( "0x" + Hexdump.toHexString( req.retval, 8 ));
		}

		System.out.println( "bytes_needed=" + req.bytes_needed + ",services_returned=" + req.services_returned + ",resume_handle=" + req.resume_handle );

		ServiceStatus status = new ServiceStatus();
		char[] buf = new char[BUFSIZ / 2];
		int off = 0;

		for( int i = 0; i < req.services_returned; i++ ) {
			off += status.decode( req.service, off, 0, buf );

			System.out.println( "service_name=" + status.service_name + ",display_name=" + status.display_name + ",service_type=0x" + Hexdump.toHexString( status.service_type, 4 ) + ",current_state=" + status.current_state + ",controls_accepted=" + status.controls_accepted + ",win32_exit_code=0x" + Hexdump.toHexString( status.win32_exit_code, 8 ) + ",service_specific_exit_code=" + status.service_specific_exit_code + ",check_point=" + status.check_point + ",wait_hint=" + status.wait_hint );
		}
	}
	rpc.policy_handle svcctlOpenService(rpc.policy_handle scm_handle, String name) throws Exception {
		rpc.policy_handle handle = new rpc.policy_handle();
		handle.uuid = new rpc.uuid_t();

		OpenService req = new OpenService( scm_handle, name, SC_MANAGER_ALL_ACCESS, handle);
		call(0, req);
		if( req.retval != 0 ) {
			throw new Exception( "0x" + Hexdump.toHexString( req.retval, 8 ));
		}

		return handle;
	}
	void svcctlStartService( rpc.policy_handle handle, String[] args ) throws Exception {
		StartService req = new StartService( handle, args.length, args );
		call(0, req);
		if( req.retval != 0 ) {
			throw new Exception( "0x" + Hexdump.toHexString( req.retval, 8 ));
		}
	}
	void svcctlControlService( rpc.policy_handle handle, int control ) throws Exception {
		service_status status = new service_status();
		ControlService req = new ControlService( handle, control, status );
		call(0, req);
		if( req.retval != 0 ) {
			throw new Exception( "0x" + Hexdump.toHexString( req.retval, 8 ));
		}
	}
	void svcctlCloseServiceHandle(rpc.policy_handle handle) throws Exception {
		CloseServiceHandle req = new CloseServiceHandle(handle);
		call(0, req);
		if( req.retval != 0 ) {
			throw new Exception( "0x" + Hexdump.toHexString( req.retval, 8 ));
		}
	}
	public void doAll() throws Exception {
		rpc.policy_handle scm_handle = svcctlOpenSCManager();
		svcctlEnumServicesStatus(scm_handle);
		rpc.policy_handle handle = svcctlOpenService( scm_handle, "NetDDEdsdm" );
		String[] args = { "one", "two", "three" };
		svcctlStartService( handle, args );
System.out.println( "Network DDE DSDM service started." );
Thread.sleep( 1000 );
		svcctlControlService( handle, SERVICE_CONTROL_STOP );
System.out.println( "Network DDE DSDM service stopped." );
		svcctlCloseServiceHandle( handle );
		svcctlCloseServiceHandle( scm_handle );
	}
	public static void main(String[] args) throws Exception {
		if( args.length < 1 ) {
			System.err.println( "usage: TestSvcctl <servername> [<properties>]" );
			return;
		}

        Properties properties = null;
        if (args.length > 1) {
            properties = new Properties();
            properties.load(new FileInputStream(args[1]));
        }

		TestSvcctl stub = new TestSvcctl(args[0], properties);

		stub.doAll();
	}
}
