package togos.minecraft.mapgen;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jnbt.Tag;

/**
 * A HashMap that's slightly easier to add Tags to.
 */
public class TagMap implements Map
{
	private static final long serialVersionUID = 1L;
	
	List values = new ArrayList();
	
	public static class TagEntry implements Map.Entry {
		Tag t;
		public TagEntry(Tag t) {
			this.t = t;
		}

		public Object getKey() {
			return t.getName();
		}

		public Object getValue() {
			return t;
		}

		public Object setValue( Object arg0 ) {
			throw new UnsupportedOperationException();
		}
	}
	
	public void add( Tag t ) {
		//put( t.getName(), t );
		values.add( t );
	}

	public void clear() {
		// TODO Auto-generated method stub
		
	}

	public boolean containsKey( Object key ) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean containsValue( Object value ) {
		// TODO Auto-generated method stub
		return false;
	}

	public Set entrySet() {
		Set entries = new HashSet();
		for( Iterator i=values.iterator(); i.hasNext(); ) {
			entries.add( new TagEntry( (Tag)i.next() ));
		}
		return entries;
	}

	public Object get( Object key ) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	public Set keySet() {
		// TODO Auto-generated method stub
		return null;
	}

	public Object put( Object arg0, Object arg1 ) {
		// TODO Auto-generated method stub
		return null;
	}

	public void putAll( Map arg0 ) {
		// TODO Auto-generated method stub
		
	}

	public Object remove( Object key ) {
		// TODO Auto-generated method stub
		return null;
	}

	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	public Collection values() {
		return values;
	}
	
	
}
