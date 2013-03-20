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
public class BlockingNonBlockingQueue<Item> implements BlockingQueue<Item>, Runnable, Service
{
	BlockingQueue<Item> dest;
	BlockingQueue<Item> list = new LinkedBlockingQueue<Item>();
	Thread drainThread;
	
	public BlockingNonBlockingQueue( BlockingQueue<Item> dest ) {
		this.dest = dest;
	}
	
	public BlockingNonBlockingQueue( int capacity ) {
		this( new ArrayBlockingQueue<Item>(capacity) );
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
	
	public boolean add( Item item ) {
		if( !dest.offer(item) ) {
			list.add( item );
		}
		return true;
	}
	public boolean addAll( Collection<? extends Item> items ) {
		return list.addAll( items );
	}
	public void clear() {
	    throw new UnsupportedOperationException();
	}
	public boolean contains( Object o ) {
		throw new UnsupportedOperationException();
	}
	public boolean containsAll( Collection<?> o ) {
		throw new UnsupportedOperationException();
	}
	public int drainTo( Collection<? super Item> col ) {
	    return dest.drainTo( col );
	}
	public int drainTo( Collection<? super Item> arg0, int arg1 ) {
		return dest.drainTo(arg0, arg1);
	}
	public Item element() {
	    return dest.element();
	}
	public boolean offer( Item item ) {
		return dest.offer(item);
    }
	public boolean offer( Item arg0, long arg1, TimeUnit arg2 )
            throws InterruptedException {
	    return dest.offer(arg0, arg1, arg2);
    }
	public Item poll( long timeout, TimeUnit unit )
            throws InterruptedException {
	    return dest.poll(timeout, unit);
    }
	public void put( Item item ) throws InterruptedException {
		dest.put(item);
    }
	public int remainingCapacity() {
	    return dest.remainingCapacity();
    }
	public boolean remove( Object o ) {
		throw new UnsupportedOperationException();
    }
	public Item take() throws InterruptedException {
	    return dest.take();
    }
	public Item peek() {
	    return dest.peek();
    }
	public Item poll() {
	    return dest.poll();
    }
	public Item remove() {
		return dest.remove();
    }
	public boolean isEmpty() {
	    return dest.isEmpty() && list.isEmpty();
    }
	public Iterator<Item> iterator() {
	    return dest.iterator();
    }
	public boolean removeAll( Collection<?> arg0 ) {
		throw new UnsupportedOperationException();
    }
	public boolean retainAll( Collection<?> arg0 ) {
		throw new UnsupportedOperationException();
    }
	public int size() {
	    return dest.size() + list.size();
    }
	public Item[] toArray() {
		throw new UnsupportedOperationException();
    }
	public <T> T[] toArray( T[] arg0 ) {
		throw new UnsupportedOperationException();
    }
}
