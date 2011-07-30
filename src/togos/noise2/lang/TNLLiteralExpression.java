package togos.noise2.lang;

import togos.lang.SourceLocation;

public class TNLLiteralExpression extends TNLExpression
{
	public final Object value;
	
	public TNLLiteralExpression( Object value, SourceLocation sloc, TNLExpression parent ) {
		super( sloc, parent );
		this.value = value;
	}
	
	protected String valueString() {
		if( value instanceof String ) {
			return "\"" + value + "\""; // TODO: escape properly
		} else {
			return value.toString();
		}
	}
	
	public String toString(boolean includeSourceLoc) {
		return includeSourceLoc ? (valueString() + sourceLocString()) : valueString();
	}
}
