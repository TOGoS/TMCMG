package togos.noise2.lang;

import togos.lang.SourceLocation;

public class BaseSourceLocation implements SourceLocation
{
	public static final BaseSourceLocation NONE = new BaseSourceLocation("(no source)",0,0);
	
	protected String sourceFilename;
	protected int sourceLineNumber, sourceColumnNumber;
	
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
	
	public String toString() {
		if( sourceLineNumber != 0 ) {
			return "at "+sourceFilename+":"+sourceLineNumber+","+sourceColumnNumber;
		} else {
			return "in "+sourceFilename;
		}
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
