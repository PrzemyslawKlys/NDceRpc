import java.io.*;
import java.util.Properties;
import jcifs.util.Hexdump;
import ndr.*;
import rpc.*;

public class TestLsarpc extends lsarpc {

	String servername;

	TestLsarpc(String servername, Properties properties) {
		this.servername = servername;
		setAddress("ncacn_np:" + servername + "[\\PIPE\\lsarpc]");
		setProperties(properties);
	}
	rpc.policy_handle lsarOpenPolicy() throws Exception {
		rpc.policy_handle handle = new rpc.policy_handle();
		LsaObjectAttributes attrs = new LsaObjectAttributes();
		LsaQosInfo qos = new LsaQosInfo();

		qos.length = 12;
		qos.impersonation_level = 2;
		qos.context_mode = 1;

		attrs.length = 24;
		attrs.security_quality_of_service = qos;

		LsarOpenPolicy req = new LsarOpenPolicy("\\", attrs, 0x02000000, handle);
		call(0, req);
		if( req.retval != 0 ) {
			throw new Exception( "0x" + Hexdump.toHexString( req.retval, 8 ));
		}

		System.out.println(req.retval + ": " + handle.uuid.time_low);

		return handle;
	}
	LsaDomainInfo lsarGetAccountDomain(rpc.policy_handle handle) throws Exception {
		LsaDomainInfo info = new LsaDomainInfo();
		info.name = new UnicodeString(false);

		LsarQueryInformationPolicy req = new LsarQueryInformationPolicy(handle, (short)POLICY_INFO_ACCOUNT_DOMAIN, (NdrObject)info);
		call(0, req);
		if( req.retval != 0 ) {
			throw new Exception( "0x" + Hexdump.toHexString( req.retval, 8 ));
		}

		System.out.println(req.retval + ": " + info.name);

		return info;
	}
	void lsarClose(rpc.policy_handle handle) throws Exception {
		LsarClose req = new LsarClose(handle);
		call(0, req);
		if( req.retval != 0 ) {
			throw new Exception( "0x" + Hexdump.toHexString( req.retval, 8 ));
		}
	}

	public LsaDomainInfo getDomainInfo() throws Exception {
		rpc.policy_handle handle = lsarOpenPolicy();
		LsaDomainInfo info = lsarGetAccountDomain(handle);
		lsarClose(handle);
		return info;
	}

	public void lookupSID( String sid ) throws Exception {
		rpc.policy_handle handle = lsarOpenPolicy();
		lsa_SidArray sids = new lsa_SidArray();
		sids.num_sids = 1;
		sids.sids = new lsa_SidPtr[sids.num_sids];
		sids.sids[0] = new lsa_SidPtr();
		sids.sids[0].sid = SID.toSID(sid);

		lsa_RefDomainList domains = new lsa_RefDomainList();
		lsa_TransNameArray names = new lsa_TransNameArray();

		LsarLookupSids req = new LsarLookupSids(handle,
				sids, domains, names, (short)1, 1);
		call(0, req);
		if( req.retval != 0 ) {
			throw new Exception( "0x" + Hexdump.toHexString( req.retval, 8 ));
		}

		System.out.println( new UnicodeString( names.names[0].name, false ) );

		lsarClose(handle);
	}

	public static void main(String[] args) throws Exception {
		if( args.length < 1 ) {
			System.err.println( "usage: TestLsarpc <servername> [<properties>]" );
			return;
		}

        Properties properties = null;
        if (args.length > 1) {
            properties = new Properties();
            properties.load(new FileInputStream(args[1]));
        }

		TestLsarpc stub = new TestLsarpc(args[0], properties);

		stub.getDomainInfo();
		//stub.lookupSID("S-1-5-21-1319786636-1892384142-370684871-1003");
		//stub.lookupSID("S-1-5-21-2533836172-396908157-422722257-513");
		stub.lookupSID("S-1-5-32-550");
	}
}
