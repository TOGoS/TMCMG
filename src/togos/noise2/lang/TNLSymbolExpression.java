package togos.noise2.lang;

public class TNLSymbolExpression extends TNLExpression
{
	public String symbol;
	
	public TNLSymbolExpression( String symbol, SourceLocation sloc, TNLExpression parent ) {
		super(sloc, parent);
	    this.symbol = symbol;
    }
	
	public boolean equals( Object o ) {
		if( o instanceof TNLSymbolExpression) {
			TNLSymbolExpression oe = (TNLSymbolExpression)o;
			return symbol.equals(oe.symbol) && super.equals(oe);
		}
		return false;
	}
	
	public int hashCode() {
		return 2 + super.hashCode() + symbol.hashCode();
	}
	
	public String toString() {
		return "symbol<"+symbol+">";
	}
}
