package togos.noise2.lang;

public class TNLLiteralExpression extends TNLExpression
{
	Object value;
	
	public TNLLiteralExpression( Object value, SourceLocation sloc, TNLExpression parent ) {
		super( sloc, parent );
		this.value = value;
	}
	
	public boolean equals( Object o ) {
		if( o instanceof TNLLiteralExpression) {
			TNLLiteralExpression oe = (TNLLiteralExpression)o;
			if( value == oe.value ) {
			} else if( value == null || oe.value == null ) {
				return false;
			} else if( !value.equals(oe.value) ) {
				return false;
			}
			return super.equals(o);
		}
		return false;
	}
	
	public int hashCode() {
		return 1 + super.hashCode() + (value == null ? 0 : value.hashCode());
	}
	
	public String toString() {
		return "literal<"+value+">";
	}
}
