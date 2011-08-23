package togos.minecraft.mapgen.job;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import togos.minecraft.mapgen.queue.BlockingNonBlockingQueue;
import togos.service.Service;

public class JobService implements Service
{
	protected static List buildJobRunners( BlockingQueue jobQueue, int nThreads ) {
		ArrayList jobRunners = new ArrayList(nThreads);
		for( ; nThreads > 0 ; --nThreads ) {
			jobRunners.add(new JobRunner(jobQueue));
		}
		return jobRunners;
	}
	
	public final BlockingQueue jobQueue;
	final List jobRunners;
	
	protected JobService( BlockingQueue jobQueue, List jobRunners ) {
		this.jobQueue = jobQueue;
		this.jobRunners = jobRunners;
	}
	
	public JobService( BlockingQueue jobQueue, int nThreads ) {
		this( jobQueue, buildJobRunners(jobQueue,nThreads) );
	}
	
	public JobService( BlockingQueue jobQueue ) {
		this( jobQueue, Runtime.getRuntime().availableProcessors() );
	}
	
	public JobService( int nThreads ) {
		this( new BlockingNonBlockingQueue(nThreads*5), nThreads );
	}
	
	public JobService() {
		this( Runtime.getRuntime().availableProcessors() );
	}
	
	public int getThreadCount() {
		return this.jobRunners.size();
	}
	
	////
	
	public void put( Runnable r ) throws InterruptedException {
		jobQueue.put(r);
	}
	
	public void add( Runnable r ) {
		jobQueue.add(r);
	}
	
	public boolean offer( Runnable r ) {
		return jobQueue.offer(r);
	}
	
	public BlockingQueue getJobQueue() {
		return jobQueue;
	}
	
	////
	
	public synchronized void start() {
		for( Iterator i=jobRunners.iterator(); i.hasNext(); ) {
			((JobRunner)i.next()).start();
		}
		if( jobQueue instanceof Service ) {
			((Service)jobQueue).start();
		}
	}
	
	public synchronized void halt() {
		for( Iterator i=jobRunners.iterator(); i.hasNext(); ) {
			((JobRunner)i.next()).halt();
		}
		if( jobQueue instanceof Service ) {
			((Service)jobQueue).halt();
		}
	}
}
