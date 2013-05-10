package togos.lang;


public class BaseSourceLocation implements SourceLocation
{
	public static final BaseSourceLocation fake( String description ) {
		return new BaseSourceLocation("("+description+")",0,0);
	}
	public static final BaseSourceLocation NONE = fake("no source");
	
	protected final String sourceFilename;
	protected final int sourceLineNumber, sourceColumnNumber;
	
	public BaseSourceLocation( String filename, int lineNumber, int columnNumber ) {
		this.sourceFilename = filename;
		this.sourceLineNumber = lineNumber;
		this.sourceColumnNumber = columnNumber;
	}
	
	public String getSourceFilename() {
		return sourceFilename;
	}
	
	public int getSourceLineNumber() {
		return sourceLineNumber;
	}
	
	public int getSourceColumnNumber() {
		return sourceColumnNumber;
	}

	public static String toString( SourceLocation sl ) {
		if( sl.getSourceLineNumber() != 0 ) {
			return "at "+sl.getSourceFilename()+":"+sl.getSourceLineNumber()+","+sl.getSourceColumnNumber();
		} else {
			return "in "+sl.getSourceFilename();
		}
	}
	
	public String toString() {
		return toString(this);
	}
	
	public boolean equals( Object oth ) {
		if( !(oth instanceof SourceLocation) ) return false;
		
		SourceLocation osl = (SourceLocation)oth;
		if( !sourceFilename.equals(osl.getSourceFilename()) ) return false;
		if( sourceLineNumber != osl.getSourceLineNumber() ) return false;
		if( sourceColumnNumber != osl.getSourceColumnNumber() ) return false;
		
		return true;
	}
}
