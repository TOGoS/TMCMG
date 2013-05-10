package togos.noise.v3;

import java.io.PrintStream;

import togos.lang.BaseSourceLocation;
import togos.lang.CompileError;
import togos.noise.v3.program.runtime.Binding;

public class BindingError extends CompileError
{
    private static final long serialVersionUID = 1L;
    
	public final Binding<?> binding;
	
	public BindingError( String text, Binding<?> binding ) {
		super( text, binding.sLoc );
		this.binding = binding;
	}
	
	protected void printBindingTrace( Binding<?> binding, String indent, PrintStream s ) {
		// TODO: be more explicit about what binding.sLoc does and refactor code
		// to clearly express it (hint: it happens to actually be an ASTNode).
		s.println(indent + BaseSourceLocation.toString(binding.sLoc) + ": " + binding.sLoc + " ("+binding.getClass().getName()+")");
		for( Binding<?> dep : binding.getDirectDependencies() ) {
			printBindingTrace( dep, indent + "  ", s );
		}
	}
	
	@Override public void printStackTrace( PrintStream s ) {
		s.println(getMessage());
		s.println("Expression dependencies:");
		printBindingTrace(binding, "  ", s);
		super.printStackTrace(s);
	}
}
