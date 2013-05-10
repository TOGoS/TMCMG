package togos.noise.v3.program.runtime;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Simple read-only linked list.
 */
public class LinkedListNode<V> implements Iterable<V> {
	public static final LinkedListNode<?> EMPTY = new LinkedListNode<Object>();
	
	@SuppressWarnings("unchecked")
	public static final <T> LinkedListNode<T> empty() {
		return (LinkedListNode<T>)EMPTY;
	}
	
	public final int length; 
	public final V head;
	public final LinkedListNode<? extends V> tail;
	public LinkedListNode( V head, LinkedListNode<? extends V> tail ) {
		this.length = tail.length + 1;
		this.head = head;
		this.tail = tail;
	}
	
	private LinkedListNode() {
		this.length = 0;
		this.head = null;
		this.tail = this;
	}
	
	public String toString() {
		if( this.length == 0 ) return "[]";
		
		String res = null;
		LinkedListNode<? extends V> n = this;
		while( n.length > 0 ) {
			res = (res == null ? "[\n" : res + ",\n") + "\t" + n.head;
			n = n.tail;
		}
		return res + "\n]";
	}
	
	public static <V> LinkedListNode<V> fromArray( V[] values ) {
		@SuppressWarnings("unchecked")
        LinkedListNode<V> tail = (LinkedListNode<V>)LinkedListNode.EMPTY;
		for( int i=values.length-1; i >= 0; --i ) {
			tail = new LinkedListNode<V>( values[i], tail );
		}
		return tail;
	}
	
	public static <V> LinkedListNode<V> fromList( List<V> values ) {
		@SuppressWarnings("unchecked")
        LinkedListNode<V> tail = (LinkedListNode<V>)LinkedListNode.EMPTY;
		for( int i=values.size()-1; i >= 0; --i ) {
			tail = new LinkedListNode<V>( values.get(i), tail );
		}
		return tail;
	}
	
	public List<V> toList() {
		ArrayList<V> list = new ArrayList<V>(length);
		LinkedListNode<? extends V> n = this;
		while( n.length > 0 ) { list.add(n.head); n = n.tail; }
		return list;
	}
	
	////
	
	// A nice advantage of linked lists is that you can get by without
	// iterators.  But if you really want one...
	@Override public Iterator<V> iterator() {
		return new Iterator<V>() {
			LinkedListNode<? extends V> n = LinkedListNode.this;
			
			@Override public boolean hasNext() {
				return n.length > 0;
			}
			
			@Override public V next() {
				try {
					return n.head;
				} finally {
					n = n.tail;
				}
			}
			
			@Override public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}
}
