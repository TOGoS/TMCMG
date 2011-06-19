package togos.noise2.rdf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExpressionBuilder
{
	public static class AttributeEntry implements Map.Entry {
		final Object k, v;
		public AttributeEntry( Object k, Object v ) {
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
	
	String typeName;
	List attributeEntries = new ArrayList();
	
	public ExpressionBuilder(String typeName) {
		this.typeName = typeName;
	}
	public ExpressionBuilder create(String typeName) {
		return new ExpressionBuilder(typeName);
	}
	public ExpressionBuilder with(String attrName, Object value) {
		attributeEntries.add(new AttributeEntry(attrName, value));
		return this;
	}
	/** Don't call with(...) any more after this; you'll screw it up! */
	public Expression toExpression() {
		return new BaseExpression(typeName, attributeEntries);
	}
};
