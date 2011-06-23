package togos.noise2.lang;

public class TNLExpression
{
	public String sourceFilename = "(unknown)";
	public int sourceLine, sourceColumn;
	public TNLExpression parent;
	
	public TNLExpression( SourceLocation sloc, TNLExpression parent ) {
		this.sourceFilename = sloc.getSourceFilename();
		this.sourceLine = sloc.getSourceLineNumber();
		this.sourceColumn = sloc.getSourceColumnNumber();
		this.parent = parent;
	}
	
	public boolean equals( Object o ) {
		if( o instanceof TNLExpression) {
			TNLExpression oe = (TNLExpression)o;
			if( !sourceFilename.equals(oe.sourceFilename) ) return false;
			if( sourceLine != oe.sourceLine ) return false;
			if( sourceColumn != oe.sourceColumn ) return false;
			if( parent == oe.parent ) {
				return true;
			} else if( parent == null || oe.parent == null ) {
				return false;
			} else {
				return parent.equals(oe.parent);
			}
		}
		return false;
	}
	
	public int hashCode() {
		return sourceFilename.hashCode() + sourceLine + sourceColumn + (parent == null ? 0 : parent.hashCode());
	}
}
