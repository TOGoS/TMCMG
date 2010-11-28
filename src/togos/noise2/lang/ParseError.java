package togos.noise2.lang;

public class ParseError extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	SourceLocation sloc;
	public ParseError( String message, SourceLocation sloc ) {
		super(message);
		this.sloc = sloc;
	}
}
