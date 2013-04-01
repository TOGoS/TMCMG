package togos.noise.v3.program.runtime;

import java.util.List;

public class LinkedListNode<V> {
	public static final LinkedListNode<?> EMPTY = new LinkedListNode<Object>(); 
	
	public final boolean isEmpty; 
	public final V head;
	public final LinkedListNode<? extends V> tail;
	public LinkedListNode( V head, LinkedListNode<? extends V> tail ) {
		this.isEmpty = false;
		this.head = head;
		this.tail = tail;
	}
	
	private LinkedListNode() {
		this.isEmpty = true;
		this.head = null;
		this.tail = this;
	}
	
	public String toString() {
		if( this.isEmpty ) return "[]";
		
		String res = null;
		LinkedListNode<? extends V> n = this;
		while( !n.isEmpty ) {
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
}
