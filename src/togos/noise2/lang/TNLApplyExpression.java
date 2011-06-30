package togos.noise2.lang;

import java.util.List;
import java.util.Map;
import java.util.Iterator;

public class TNLApplyExpression extends TNLExpression
{
	public TNLExpression functionExpression;
	public List argumentExpressions;
	public List namedArgumentExpressionEntries;
	
	public TNLApplyExpression( TNLExpression functionExpression,
			List argumentExpressions, List namedArgumentExpressionEntries,
			SourceLocation sloc, TNLExpression parent ) {
		super( sloc, parent );
		this.functionExpression = functionExpression;
		this.argumentExpressions = argumentExpressions;
		this.namedArgumentExpressionEntries = namedArgumentExpressionEntries;
	}
	
	public String toString( boolean includeSourceLoc ) {
		StringBuilder sb = new StringBuilder();
		sb.append( functionExpression.toString( includeSourceLoc ) );
		boolean frist = true;
		if( argumentExpressions.size() > 0 || namedArgumentExpressionEntries.size() > 0 ) {
			for( Iterator i=argumentExpressions.iterator(); i.hasNext(); ) {
				if( !frist ) sb.append(", ");
				TNLExpression e = ((TNLExpression)i.next());
				sb.append( e.toString(includeSourceLoc) );
				frist = false;
			}
			for( Iterator i=namedArgumentExpressionEntries.iterator(); i.hasNext(); ) {
				if( !frist ) sb.append(", ");
				Map.Entry en = ((Map.Entry)i.next());
				String name = (String)en.getKey();
				TNLExpression e = ((TNLExpression)en.getValue());
				sb.append( name ); // TODO: escape properly
				sb.append( '@' );
				sb.append( e.toString(includeSourceLoc) );
				frist = false;
			}
		} else {
			sb.append("()");
		}
		if( includeSourceLoc ) sb.append( sourceLocString() );
		return sb.toString();
	}
}
