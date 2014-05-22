import java.io.*;
import java.util.*;
import jcifs.util.Hexdump;
import ndr.*;
import rpc.*;

public class TestAtsvc extends atsvc {

	String servername;

	TestAtsvc(String servername, Properties properties) {
		this.servername = servername;
		setAddress("ncacn_np:" + servername + "[\\PIPE\\atsvc]");
		setProperties(properties);
	}

	void atsvcScheduleJobAdd( String command, Date job_time, TestSrvsvc srvsvc ) throws Exception {
		GregorianCalendar cal = new GregorianCalendar();

		if( job_time == null ) {
			Date d = srvsvc.doRemoteTOD();
			cal.setTime( d );

/* The AT service time resolution is 1 minute. Submitting the job for the
 * current minute or even a few seconds into the future does not work. The
 * job must be submitted for the next minute. So no matter how good we are
 * at determining the current time it may take as long as 1 minute before
 * the job is actually ran. Additionally we must add a few seconds on top
 * of that to assure that the delay between querying the time and submitting
 * the job does not span the minute rollover.
 */

			cal.add( Calendar.SECOND, 70 ); /* 1min 10sec in future */
			job_time = cal.getTime();
		}

		cal.setTime( job_time );
		cal.set( Calendar.HOUR_OF_DAY, 0 );
		cal.set( Calendar.MINUTE, 0 );
		cal.set( Calendar.SECOND, 0 );
		cal.set( Calendar.MILLISECOND, 0 );
		long millisSince12am = job_time.getTime() - cal.getTime().getTime();

System.out.println( "trunc: " + cal.getTime() + " jt: " + job_time + " diff: " + millisSince12am );

		AtInfo info = new AtInfo();
		info.job_time = (int)(millisSince12am & 0xFFFFFFFFL);
		info.command = command;
		ScheduleJobAdd req = new ScheduleJobAdd("\\\\" + servername, info, 0);
		call(0, req);
		if( req.retval != 0 ) {
			throw new Exception( "0x" + Hexdump.toHexString( req.retval, 8 ));
		}

		System.out.println( req.retval + ": " + req.job_id );
	}

	public static void main(String[] args) throws Exception {
		if( args.length < 1 ) {
			System.err.println( "usage: TestAtsvc <servername> [<properties>]" );
			return;
		}

        Properties properties = null;
        if (args.length > 1) {
            properties = new Properties();
            properties.load(new FileInputStream(args[1]));
        }

		TestAtsvc stub = new TestAtsvc(args[0], properties);
		TestSrvsvc srvsvc = new TestSrvsvc(args[0], properties);

		stub.atsvcScheduleJobAdd( "notepad.exe", null, srvsvc );
	}
}
