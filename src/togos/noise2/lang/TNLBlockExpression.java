package togos.noise2.lang;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class TNLBlockExpression extends TNLExpression
{
	Map definitions = new HashMap();
	TNLExpression value;
	
	public TNLBlockExpression( SourceLocation sloc, TNLExpression parent ) {
		super( sloc, parent );
	}
	
	public boolean equals( Object o ) {
		if( o instanceof TNLBlockExpression) {
			TNLBlockExpression oe = (TNLBlockExpression)o;
			return value.equals(oe.value) && definitions.equals(oe.definitions) && super.equals(oe);
		}
		return false;
	}
	
	public int hashCode() {
		return 3 + super.hashCode() + (definitions.hashCode() << 8) + (value.hashCode() << 16);
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("block { ");
		for( Iterator i=definitions.entrySet().iterator(); i.hasNext(); ) {
			Map.Entry e = (Map.Entry)i.next();
			sb.append(e.getKey() + " = " + e.getValue() + "; " );
		}
		sb.append( value.toString() );
		sb.append( " } (" );
		sb.append( ParseUtil.formatLocation(this) );
		sb.append( ") parent: " );
		if( parent == null ) sb.append( "null" );
		else sb.append( parent.toString() );
		return sb.toString();
	}

}
