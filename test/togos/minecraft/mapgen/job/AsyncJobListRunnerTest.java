package togos.minecraft.mapgen.job;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

public class AsyncJobListRunnerTest extends TestCase
{
	protected void runJobs( AsyncJobListRunner ajlr ) {
		try {
			Job j;
			while( (j = ajlr.getNextJob()) != null ) {
				//System.err.println("Run a job");
				j.setStatus(Job.STATUS_COMPLETE);
			}
		} catch( InterruptedException e ) {
			throw new RuntimeException(e);
		}
	}
	
	class ThingyJob extends Job {
		public String thingy;
	}
	
	protected int outstandingJobCount;
	
	public void testRunSomeJobs(boolean cancelSome) throws InterruptedException {
		JobStatusListener jsl = new JobStatusListener() {
			public void jobStatusUpdated( Job j ) {
				if( j.isDone() ) {
					synchronized( AsyncJobListRunnerTest.this ) {
						--outstandingJobCount;
					}
				}
			}
		};

		int jobsPerSet = 100;
		
		List jobs1 = new ArrayList();
		for( int i=0; i<jobsPerSet; ++i ) {
			ThingyJob j = new ThingyJob();
			j.thingy = "A";
			j.addStatusListener(jsl);
			jobs1.add(j);
		}
		
		List jobs2 = new ArrayList();
		for( int i=0; i<jobsPerSet; ++i ) {
			ThingyJob j = new ThingyJob();
			j.thingy = "B";
			j.addStatusListener(jsl);
			jobs2.add(j);
		}
		
		List jobs3 = new ArrayList();
		for( int i=0; i<jobsPerSet; ++i ) {
			ThingyJob j = new ThingyJob();
			j.thingy = "C";
			j.addStatusListener(jsl);
			jobs3.add(j);
		}
		
		List jobs4 = new ArrayList();
		for( int i=0; i<jobsPerSet; ++i ) {
			ThingyJob j = new ThingyJob();
			j.thingy = "D";
			j.addStatusListener(jsl);
			jobs4.add(j);
		}
		
		List jobs5 = new ArrayList();
		for( int i=0; i<jobsPerSet; ++i ) {
			ThingyJob j = new ThingyJob();
			j.thingy = "E";
			j.addStatusListener(jsl);
			jobs5.add(j);
			// Try to screw it up!
			/*
			jobs4.add(j);
			jobs3.add(j);
			jobs2.add(j);
			jobs1.add(j);
			*/
		}
		
		final AsyncJobListRunner ajlr = new AsyncJobListRunner();
		new Thread() {
			public void run() {
				runJobs(ajlr);
			};
		}.start();
		outstandingJobCount = 5 * jobsPerSet;
		/*
			jobs1.size() +
			jobs2.size() +
			jobs3.size() +
			jobs4.size() +
			jobs5.size();
			*/
		JobListHandle jlh1 = ajlr.enqueueJobs(jobs1.iterator(), "A");
		JobListHandle jlh2 = ajlr.enqueueJobs(jobs2.iterator(), "B");
		JobListHandle jlh3 = ajlr.enqueueJobs(jobs3.iterator(), "C");
		JobListHandle jlh4 = ajlr.enqueueJobs(jobs4.iterator(), "D");
		JobListHandle jlh5 = ajlr.enqueueJobs(jobs5.iterator(), "E");
		
		if( cancelSome ) jlh4.cancel();
		
		//System.err.println("Join job lists 1...");
		jlh1.join();
		//System.err.println("Join job lists 2...");
		jlh2.join();
		
		if( cancelSome ) jlh5.cancel();
		
		//System.err.println("Join job lists 3...");
		jlh3.join();
		//System.err.println("Join job lists 4...");
		jlh4.join();
		//System.err.println("Join job lists 5...");
		jlh5.join();
		//System.err.println("All job lists finished");
		
		if( cancelSome ) {
			assertTrue( outstandingJobCount >= 0 && outstandingJobCount <= 2 * jobsPerSet );
		} else {
			assertEquals( 0, outstandingJobCount );
		}
		System.err.println(outstandingJobCount);
		ajlr.halt();
	}
	
	public void testRunSomeJobs() throws InterruptedException {
		testRunSomeJobs(false);
	}
	
	public void testRunAndCancelSomeJobs() throws InterruptedException {
		testRunSomeJobs(true);
	}
}
