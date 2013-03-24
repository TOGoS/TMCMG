package togos.noise.v3.parser.ast;

import togos.lang.SourceLocation;

public class ASTNode implements SourceLocation
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

}
