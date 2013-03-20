package togos.minecraft.mapgen.job;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import togos.minecraft.mapgen.queue.BlockingNonBlockingQueue;
import togos.service.Service;

public class JobService implements Service
{
	protected static List<JobRunner> buildJobRunners( BlockingQueue<Runnable> jobQueue, int nThreads ) {
		ArrayList<JobRunner> jobRunners = new ArrayList<JobRunner>(nThreads);
		for( ; nThreads > 0 ; --nThreads ) {
			jobRunners.add(new JobRunner(jobQueue));
		}
		return jobRunners;
	}
	
	public final BlockingQueue<Runnable> jobQueue;
	final List<JobRunner> jobRunners;
	
	protected JobService( BlockingQueue<Runnable> jobQueue, List<JobRunner> jobRunners ) {
		this.jobQueue = jobQueue;
		this.jobRunners = jobRunners;
	}
	
	public JobService( BlockingQueue<Runnable> jobQueue, int nThreads ) {
		this( jobQueue, buildJobRunners(jobQueue,nThreads) );
	}
	
	public JobService( BlockingQueue<Runnable> jobQueue ) {
		this( jobQueue, Runtime.getRuntime().availableProcessors() );
	}
	
	public JobService( int nThreads ) {
		this( new BlockingNonBlockingQueue<Runnable>(nThreads*5), nThreads );
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
	
	public BlockingQueue<Runnable> getJobQueue() {
		return jobQueue;
	}
	
	////
	
	public synchronized void start() {
		for( JobRunner r : jobRunners ) r.start();
		
		if( jobQueue instanceof Service ) {
			((Service)jobQueue).start();
		}
	}
	
	public synchronized void halt() {
		for( JobRunner r : jobRunners ) r.halt();
		
		if( jobQueue instanceof Service ) {
			((Service)jobQueue).halt();
		}
	}
}
