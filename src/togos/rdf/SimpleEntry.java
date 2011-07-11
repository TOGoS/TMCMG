package togos.rdf;

import java.util.Map;

public class SimpleEntry implements Map.Entry {
	final Object k, v;
	public SimpleEntry( Object k, Object v ) {
		this.k = k; this.v = v;
	}
	public Object getKey() {
	    return k;
	}
	public Object getValue() {
	    return v;
	}
	public Object setValue( Object v ) {
        throw new UnsupportedOperationException("Can't set value on me!");
    }
	
	public boolean equals( Object obj ) {
	    if( obj instanceof Map.Entry ) {
	    	Map.Entry e = (Map.Entry)obj;
	    	return k.equals(e.getKey()) && v.equals(e.getValue());
	    }
    	return false;
	}
}
