package togos.minecraft.mapgen.script;

public class CompileError extends RuntimeException
{
	private static final long serialVersionUID = 1L;
	
	public final SourceLocation sourceLocation;
	public CompileError( String message, SourceLocation sloc ) {
		super(message + " at "+sloc );
		this.sourceLocation = sloc;
	}
	
	public CompileError( Exception cause, SourceLocation sloc ) {
		super(cause);
		this.sourceLocation = sloc;
	}
}
