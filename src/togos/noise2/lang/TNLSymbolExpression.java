package togos.noise2.lang;

public class TNLSymbolExpression extends TNLExpression
{
	public String symbol;
	
	public TNLSymbolExpression( String symbol, SourceLocation sloc, TNLExpression parent ) {
		super(sloc, parent);
	    this.symbol = symbol;
    }
	
	protected String symbolString() {
		return "`" + symbol + "`"; // TODO: escape properly
	}
	
	public String toString(boolean includeSourceLoc) {
		return includeSourceLoc ? (symbolString() + sourceLocString()) : symbolString();
	}
}
