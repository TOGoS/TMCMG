package togos.minecraft.mapgen.script;

public class Token implements SourceLocation
{
	public String token;
	public String filename;
	public int lineNumber, columnNumber;
	
	public Token( String token, String filename, int lineNumber, int columnNumber ) {
		this.token = token;
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
}
