package togos.noise.v3.parser.ast;

import togos.lang.SourceLocation;

public abstract class ASTNode implements SourceLocation
{
	protected String filename;
	protected int lineNumber, columnNumber;
	
	public ASTNode( SourceLocation loc ) {
		this.filename = loc.getSourceFilename();
		this.lineNumber = loc.getSourceLineNumber();
		this.columnNumber = loc.getSourceColumnNumber();
	}

	@Override public String getSourceFilename() {
		return filename;
	}

	@Override public int getSourceLineNumber() {
		return lineNumber;
	}

	@Override public int getSourceColumnNumber() {
		return columnNumber;
	}
	
	//// Stringification ////
	
	/**
	 * If atomic is false, toString does not need to
	 * wrap otherwise ambiguous expressions in parentheses
	 */
	public String toAtomicString() {
		return "(" + toString() + ")";
	}
}
