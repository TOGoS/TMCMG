package togos.noise2.lang;

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
}
