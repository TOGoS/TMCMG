package togos.minecraft.mapgen.job;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AsyncJobListScheduler implements JobListScheduler, JobSource
{
	protected List jobIterators = new ArrayList();
	protected int jobIteratorPosition = 0;
	protected volatile boolean running = true;
	
	public synchronized Job getNextJob() throws InterruptedException {
		while( running ) {
			int originalPosition = jobIteratorPosition;
			if( jobIterators.size() > 0 ) {
				// Find a job:
				do {
					try {
						Iterator it = (Iterator)jobIterators.get(jobIteratorPosition);
						if( it == null ) {
						} else if( !it.hasNext() ) {
							jobIterators.set(jobIteratorPosition, null);
						} else {
							return (Job)it.next();
						}
					} finally {
						jobIteratorPosition = (jobIteratorPosition+1)%jobIterators.size();
					}
				} while( jobIteratorPosition != originalPosition );
			}
			wait();
		}
		return null;
	}
	
	class OutstandingJobIterator implements Iterator
	{
		protected String name;
		protected Iterator next;
		protected int outstandingJobCount = 0;
		protected boolean cancelled = false;
		
		public OutstandingJobIterator( Iterator next, String name ) {
			this.next = next;
			this.name = name;
		}
		
		public synchronized boolean hasNext() {
			return !cancelled && next.hasNext();
		}
		
		public synchronized Object next() {
			Job j = (Job)next.next();
			j.addStatusListener(new JobStatusListener() {
				public void jobStatusUpdated( Job j ) {
					if( j.status == Job.STATUS_FETCHED ) {
						synchronized( OutstandingJobIterator.this ) {
							++outstandingJobCount;
						}
					} else if( j.isDone() ) {
						synchronized( OutstandingJobIterator.this ) {
							--outstandingJobCount;
							if( outstandingJobCount == 0 && !hasNext() ) {
								//System.err.println(name + " is out of jobs!");
							}
							OutstandingJobIterator.this.notifyAll();
						}
					}
				}
			});
			synchronized(j) {
				if( j.status < Job.STATUS_FETCHED ) {
					j.setStatus(Job.STATUS_FETCHED);
				} else {
					// We can deal with the problems, but it's probably
					// better to not allow this.
					throw new RuntimeException("Job is already fetched!");
				}
			}
			if( outstandingJobCount == 0 ) {
				// This can happen if some jobs are already fetched and
				// run from a different iterator.  This shouldn't happen
				// if we throw the exception above.
				// Given that we throw an exception in that case (see above)
				// this should never happen.
				throw new RuntimeException("Outstanding job count should be > 0");
				//notifyAll();
			}
	        return j;
	    }
		
		public synchronized void remove() {
			next.remove();
	    }
		
		protected synchronized void cancel() {
			this.cancelled = true;
			notifyAll();
		}

		public boolean allJobsDone() {
			return cancelled || (!next.hasNext() && outstandingJobCount == 0);
        }
	}
	
	public synchronized JobListHandle enqueueJobs( Iterator jobs, String listName ) {
		final OutstandingJobIterator oji = new OutstandingJobIterator(jobs, listName);
		
		synchronized( this ) {
			initIndex: {
				for( int i=0; i<jobIterators.size(); ++i ) {
					if( jobIterators.get(i) == null ) {
						jobIterators.set(i,oji);
						break initIndex;
					}
				}
				jobIterators.add(oji);
			}
			AsyncJobListScheduler.this.notify();
		}
		
		return new JobListHandle() {
			public void join() throws InterruptedException {
				synchronized( oji ) {
					while( !oji.allJobsDone() ) {
						//System.err.println("Job iterator "+oji.name+" has more: "+oji.hasNext()+", outstanding job count: "+oji.outstandingJobCount);
						oji.wait();
					}
				}
			}
			
			public void cancel() {
				oji.cancel();
			}
		};
	}
	
	public synchronized void halt() {
		this.running = false;
		notifyAll();
	}
}
