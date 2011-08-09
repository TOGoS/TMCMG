package togos.minecraft.mapgen.job;

import java.util.concurrent.BlockingQueue;

import togos.service.Service;

public class JobRunner implements Runnable, Service
{
	protected BlockingQueue jobQueue;
	
	public JobRunner( BlockingQueue jobQueue ) {
		this.jobQueue = jobQueue;
	}
	
	public void run() {
		try {
			while( true ) {
				Runnable r = (Runnable)jobQueue.take();
				r.run();
			}
		} catch( InterruptedException e ) {
			Thread.currentThread().interrupt();
		}
	}
	
	Thread thread;
	public synchronized void start() {
		if( this.thread == null ) {
			this.thread = new Thread(this);
			this.thread.start();
		}
	}
	public synchronized void halt() {
		if( this.thread != null ) {
			this.thread.interrupt();
			this.thread = null;
		}
	}
}
