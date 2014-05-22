import java.io.*;
import java.util.Properties;
import jcifs.util.Hexdump;
import ndr.*;
import rpc.*;

public class TestSamr extends samr {

	String servername;

	TestSamr(String servername, Properties properties) {
		this.servername = servername;
		setAddress("ncacn_np:" + servername + "[\\PIPE\\samr]");
		setProperties(properties);
	}

	rpc.policy_handle samrConnect2() throws Exception {
		rpc.policy_handle handle = new rpc.policy_handle();
		handle.uuid = new rpc.uuid_t();

		SamrConnect2 req = new SamrConnect2("\\\\" + servername, 0x02000000, handle);
		call(0, req);
		if( req.retval != 0 ) {
			throw new Exception( "0x" + Hexdump.toHexString( req.retval, 8 ));
		}

		return handle;
	}

	rpc.policy_handle samrOpenDomain(rpc.policy_handle samr_handle,
				lsarpc.LsaDomainInfo info) throws Exception {
		rpc.policy_handle dom_handle = new rpc.policy_handle();
		dom_handle.uuid = new rpc.uuid_t();

		SamrOpenDomain req = new SamrOpenDomain(samr_handle, 0x02000000, info.sid, dom_handle);
		call(0, req);
		if( req.retval != 0 ) {
			throw new Exception( "0x" + Hexdump.toHexString( req.retval, 8 ));
		}

		return dom_handle;
	}
	rpc.policy_handle samrOpenUser(rpc.policy_handle dom_handle, int rid) throws Exception {
		rpc.policy_handle user_handle = new rpc.policy_handle();
		user_handle.uuid = new rpc.uuid_t();

		SamrOpenUser req = new SamrOpenUser(dom_handle, 0x02000000, rid, user_handle);
		call(0, req);
		if( req.retval != 0 ) {
			throw new Exception( "0x" + Hexdump.toHexString( req.retval, 8 ));
		}

		return user_handle;
	}
	TypedRid[] samrGetGroupsForUser(rpc.policy_handle user_handle) throws Exception {
		samr.TypedRidArray groups = new TypedRidArray();

		SamrGetGroupsForUser req = new SamrGetGroupsForUser(user_handle, groups);
		call(0, req);
		if( req.retval != 0 ) {
			throw new Exception( "0x" + Hexdump.toHexString( req.retval, 8 ));
		}

		return groups.groups;
	}
	int samrLookupNameInDomain(rpc.policy_handle dom_handle, String name) throws Exception {
		UnicodeString[] names = new UnicodeString[1];
		names[0] = new UnicodeString( name, false );
		IntArray rids = new IntArray();
		IntArray types = new IntArray();

		SamrLookupNamesInDomain req = new SamrLookupNamesInDomain(dom_handle, 1, names, rids, types);
		call(0, req);
		if( req.retval != 0 ) {
			throw new Exception( "0x" + Hexdump.toHexString( req.retval, 8 ));
		}

		return rids.rids[0];
	}
	void samrCloseHandle(rpc.policy_handle handle) throws Exception {
		SamrCloseHandle req = new SamrCloseHandle(handle);
		call(0, req);
		if( req.retval != 0 ) {
			throw new Exception( "0x" + Hexdump.toHexString( req.retval, 8 ));
		}
	}

	public void doAll(lsarpc.LsaDomainInfo info) throws Exception {
		rpc.policy_handle samr_handle = samrConnect2();
		rpc.policy_handle dom_handle = samrOpenDomain(samr_handle, info);
		int rid = samrLookupNameInDomain(dom_handle, "miallen");
		rpc.policy_handle user_handle = samrOpenUser(dom_handle, rid);
		TypedRid[] groups = samrGetGroupsForUser(user_handle);
		if( groups != null ) {
			for( int i = 0; i < groups.length; i++ ) {
				System.out.println( "groups[" + i + "]=" + groups[i].rid );
			}
		}

		samrCloseHandle(user_handle);
		samrCloseHandle(dom_handle);
		samrCloseHandle(samr_handle);
	}

	public static void main(String[] args) throws Exception {
		if( args.length < 1 ) {
			System.err.println( "usage: TestSamr <servername> [<properties>]" );
			return;
		}

        Properties properties = null;
        if (args.length > 1) {
            properties = new Properties();
            properties.load(new FileInputStream(args[1]));
        }

		TestLsarpc lsa = new TestLsarpc(args[0], properties);
		TestSamr stub = new TestSamr(args[0], properties);

		stub.doAll(lsa.getDomainInfo());
	}
}
