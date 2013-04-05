package togos.noise.v3.program.structure;

import togos.lang.CompileError;
import togos.lang.SourceLocation;
import togos.noise.v3.program.runtime.Binding;
import togos.noise.v3.program.runtime.Context;

public abstract class Expression<V> extends ProgramNode 
{
	public Expression( SourceLocation sLoc ) {
	    super(sLoc);
    }
	
	/**
	 * BIG IMPORTANT NOTE:
	 * 
	 * The reasons bindings are returned instead of the final value:
	 * - context may not be completely set up, yet!  By deferring actual evaluation until
	 *   later, calling code has a chance to finalize it (this is taken care
	 *   of by Block; other expressions don't need to worry about it).
	 * - bindings can represent expressions that include variables!
	 * 
	 * The sourceLocation of the returned binding should be that
	 * of this expression.
	 */
	public abstract Binding<? extends V> bind( Context context ) throws CompileError;
	
	public String toAtomicString() {
		return "(" + toString() + ")";
	}
}
