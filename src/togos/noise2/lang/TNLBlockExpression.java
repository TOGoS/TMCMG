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
	
	public String toString( boolean includeSourceLoc ) {
		StringBuilder sb = new StringBuilder();
		sb.append("( ");
		for( Iterator i=definitions.entrySet().iterator(); i.hasNext(); ) {
			Map.Entry e = (Map.Entry)i.next();
			sb.append(
				e.getKey() + " = " +
				((TNLExpression)e.getValue()).toString(includeSourceLoc) + "; " );
		}
		sb.append( ((TNLExpression)value).toString( includeSourceLoc ) );
		sb.append( ")" );
		if( includeSourceLoc ) {
			sb.append("[" );
			sb.append( ParseUtil.formatLocation(this) );
			sb.append("]");
		}
		
		sb.append( ")" );
		return sb.toString();
	}
}
