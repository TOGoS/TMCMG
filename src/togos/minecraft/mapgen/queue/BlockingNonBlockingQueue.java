package togos.minecraft.mapgen.queue;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import togos.service.Service;

/**
 * A queue whose BlockingQueue methods (put(), take()) are delegated
 * to a limited BlockingQueue (the 'destination') but whose add() method will add items
 * to a non-limited queue (the 'list') if the limited one is full.
 * 
 * When run(), the 'list' will be continuously drained
 * to the destination using dest.put(list.take()).
 * 
 * Operations that take items off the head of the queue are all delegated to the destination.
 * Many operations not commonly needed for queues throw UnsupportedOperationExceptions.
 */
public class BlockingNonBlockingQueue implements BlockingQueue, Runnable, Service
{
	BlockingQueue dest;
	BlockingQueue list = new LinkedBlockingQueue();
	Thread drainThread;
	
	public BlockingNonBlockingQueue( BlockingQueue dest ) {
		this.dest = dest;
	}
	
	public BlockingNonBlockingQueue( int capacity ) {
		this( new ArrayBlockingQueue(capacity) );
	}

	public void run() {
		try {
			while( true ) {
				dest.put(list.take());
			}
		} catch( InterruptedException e ) {
			Thread.currentThread().interrupt();
		}
	}
	
	public synchronized void start() {
		if( drainThread == null ) {
			drainThread = new Thread(this);
			drainThread.start();
		}
	}
	
	public synchronized void halt() {
		if( drainThread != null ) {
			drainThread.interrupt();
			drainThread = null;
		}
	}
	
	////
	
	public boolean add( Object item ) {
		if( !dest.offer(item) ) {
			list.add( item );
		}
		return true;
	}
	public boolean addAll( Collection items ) {
		return list.addAll( items );
	}
	public void clear() {
	    throw new UnsupportedOperationException();
	}
	public boolean contains( Object o ) {
		throw new UnsupportedOperationException();
	}
	public boolean containsAll( Collection o ) {
		throw new UnsupportedOperationException();
	}
	public int drainTo( Collection col ) {
	    return dest.drainTo( col );
	}
	public int drainTo( Collection arg0, int arg1 ) {
		return dest.drainTo(arg0, arg1);
	}
	public Object element() {
	    return dest.element();
	}
	public boolean offer( Object item ) {
		return dest.offer(item);
    }
	public boolean offer( Object arg0, long arg1, TimeUnit arg2 )
            throws InterruptedException {
	    return dest.offer(arg0, arg1, arg2);
    }
	public Object poll( long timeout, TimeUnit unit )
            throws InterruptedException {
	    return dest.poll(timeout, unit);
    }
	public void put( Object item ) throws InterruptedException {
		dest.put(item);
    }
	public int remainingCapacity() {
	    return dest.remainingCapacity();
    }
	public boolean remove( Object o ) {
		throw new UnsupportedOperationException();
    }
	public Object take() throws InterruptedException {
	    return dest.take();
    }
	public Object peek() {
	    return dest.peek();
    }
	public Object poll() {
	    return dest.poll();
    }
	public Object remove() {
		return dest.remove();
    }
	public boolean isEmpty() {
	    return dest.isEmpty() && list.isEmpty();
    }
	public Iterator iterator() {
	    return dest.iterator();
    }
	public boolean removeAll( Collection arg0 ) {
		throw new UnsupportedOperationException();
    }
	public boolean retainAll( Collection arg0 ) {
		throw new UnsupportedOperationException();
    }
	public int size() {
	    return dest.size() + list.size();
    }
	public Object[] toArray() {
		throw new UnsupportedOperationException();
    }
	public Object[] toArray( Object[] arg0 ) {
		throw new UnsupportedOperationException();
    }
}
