package togos.noise2.lang;

import togos.lang.SourceLocation;

public abstract class TNLExpression implements SourceLocation
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
	
	public String getSourceFilename() { return sourceFilename; }
	public int getSourceLineNumber() { return sourceLine; }
	public int getSourceColumnNumber() { return sourceColumn; }
	
	protected String sourceLocString() {
		return "[" + getSourceFilename() + ":" + getSourceLineNumber() + "," + getSourceColumnNumber() + "]";
	}
	
	public abstract String toString( boolean includeSourceLoc );
	
	/* Big note:
	 * 
	 * equals() and hashCode() take all this expression's information, including
	 * source location, sub-expressions, and parent expressions into account.
	 * 
	 * They do this by stringifying this expression, its parent, etc, and
	 * are therefore slow.  They are here only to help with unit testing.
	 */
	
	public boolean equals( Object otherThing ) {
		if( otherThing == this ) return true;
		
		if( !(otherThing instanceof TNLExpression) ) return false;
		
		TNLExpression oe = (TNLExpression)otherThing;
		
		if( !toString(true).equals(oe.toString(true)) ) return false;
		
		if( parent == oe.parent ) return true;
		if( parent == null || oe.parent == null ) return false;
		return parent.equals( oe.parent );
	}
	
	public String toString() {
		return toString(false);
	}
	
	public int hashCode() {
		return 1 + toString().hashCode() + (parent == null ? 0 : parent.hashCode());
	}
}
