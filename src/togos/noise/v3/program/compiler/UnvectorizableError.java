package togos.noise.v3.program.compiler;

import togos.lang.CompileError;
import togos.lang.SourceLocation;

public class UnvectorizableError extends CompileError
{
	private static final long serialVersionUID = -3218877872514802701L;

	public UnvectorizableError(String message, SourceLocation sLoc) {
		super(message, sLoc);
	}
	
	public UnvectorizableError(Exception cause, SourceLocation sLoc) {
		super(cause, sLoc);
	}
}
