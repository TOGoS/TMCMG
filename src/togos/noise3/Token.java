package togos.noise3;

import togos.lang.SourceLocation;

public class Token implements SourceLocation
{
	enum Type {
		SYMBOL,
		QUOTED_STRING
	};
	
	public final Type type;
	public final String text;
	public final String filename;
	public final int lineNumber;
	public final int columnNumber;
	
	public Token( Type t, String text, String filename, int lineNumber, int columnNumber ) {
		this.type = t;
		this.text = text;
		this.filename = filename;
		this.lineNumber = lineNumber;
		this.columnNumber = columnNumber;
	}
	
	@Override public String getSourceFilename() { return filename; }
	@Override public int getSourceLineNumber() { return lineNumber; }
	@Override public int getSourceColumnNumber() { return columnNumber; }
}
