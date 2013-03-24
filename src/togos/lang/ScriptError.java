package togos.lang;

import togos.lang.SourceLocation;

/**
 * Used to indicate errors encountered while parsing, compiling, or running scripts.
 */
public class ScriptError extends Exception
{
	private static final long serialVersionUID = 1L;

	public SourceLocation sourceLocation;
	public ScriptError( String message, SourceLocation sloc ) {
		super(message);
		this.sourceLocation = sloc;
	}
	
	public ScriptError( Exception cause, SourceLocation sloc ) {
		super(cause);
		this.sourceLocation = sloc;
	}
	
	public String getRawMessage() {
		return super.getMessage();
	}
	
	public String getMessage() {
		SourceLocation sloc = sourceLocation;
		String locMsg = "";
		if( sloc != null ) {
			if( sloc.getSourceLineNumber() == -1 ) {
				locMsg = " in "+sloc.getSourceFilename();
			} else {
				locMsg = " at "+sloc.getSourceFilename()+":"+sloc.getSourceLineNumber()+","+sloc.getSourceColumnNumber();
			}
		}
		return super.getMessage() + locMsg;
	}
}
