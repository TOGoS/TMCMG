package togos.lang;

public class CompileError extends ScriptError
{
	private static final long serialVersionUID = 1L;
	
	public CompileError( String message, SourceLocation sloc ) {
		super( message, sloc );
	}
	
	public CompileError( Exception cause, SourceLocation sloc ) {
		super( cause, sloc );
	}
}
