package togos.noise2.lang;

import java.util.HashMap;
import java.util.Map;

import togos.lang.SourceLocation;

public class TNLBlockExpression extends TNLExpression
{
	public Map<String, TNLExpression> definitions = new HashMap<String, TNLExpression>();
	public TNLExpression value;
	
	public TNLBlockExpression( SourceLocation sloc, TNLExpression parent ) {
		super( sloc, parent );
	}
	
	public String toString( boolean includeSourceLoc ) {
		StringBuilder sb = new StringBuilder();
		sb.append("( ");
		for( Map.Entry<String,TNLExpression> e : definitions.entrySet() ) {
			sb.append(
				e.getKey() + " = " +
				e.getValue().toString(includeSourceLoc) + "; " );
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
