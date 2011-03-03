package togos.noise2.lang;

/**
 * A single 'word' in a TNL program.
 * '+', 'simplex', '(', ';', ',' each get a token.
 */
public class Token implements SourceLocation
{
	public String value;
	public String filename;
	public int lineNumber, columnNumber;
	
	public Token( String token, String filename, int lineNumber, int columnNumber ) {
		this.value = token;
		this.filename = filename;
		this.lineNumber = lineNumber;
		this.columnNumber = columnNumber;
	}
	public String getSourceFilename() {
		return filename;
	}
	public int getSourceLineNumber() {
		return lineNumber;
	}
	public int getSourceColumnNumber() {
		return columnNumber;
	}
	
	public String toString() {
		return value;
	}
	
	public boolean equals( Object oth ) {
		if( !(oth instanceof Token) ) return false;
		Token ott = (Token)oth;
		if( !value.equals(ott.value) ) return false;
		if( !filename.equals(ott.filename) ) return false;
		if( lineNumber != ott.lineNumber ) return false;
		if( columnNumber != ott.columnNumber ) return false;
		return true;
	}
}
