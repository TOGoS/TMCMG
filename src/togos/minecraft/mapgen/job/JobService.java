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
	
	public final BlockingNonBlockingQueue jobQueue;
	final List jobRunners;
	
	protected JobService( BlockingNonBlockingQueue jobQueue, List jobRunners ) {
		this.jobQueue = jobQueue;
		this.jobRunners = jobRunners;
	}
	
	protected JobService( BlockingNonBlockingQueue jobQueue, int nThreads ) {
		this( jobQueue, buildJobRunners(jobQueue,nThreads) );
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
	
	public BlockingNonBlockingQueue getJobQueue() {
		return jobQueue;
	}
	
	////
	
	public synchronized void start() {
		for( Iterator i=jobRunners.iterator(); i.hasNext(); ) {
			((JobRunner)i.next()).start();
		}
		jobQueue.start();
	}
	
	public synchronized void halt() {
		for( Iterator i=jobRunners.iterator(); i.hasNext(); ) {
			((JobRunner)i.next()).halt();
		}
		jobQueue.halt();
	}
}
