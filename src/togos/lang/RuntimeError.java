package togos.lang;

import togos.lang.SourceLocation;

public class RuntimeError extends ScriptError
{
	private static final long serialVersionUID = 1L;
	
	public RuntimeError( String message, SourceLocation sloc ) {
		super( message, sloc );
	}
	
	public RuntimeError( Exception cause, SourceLocation sloc ) {
		super( cause, sloc );
	}
}
