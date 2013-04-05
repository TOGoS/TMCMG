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
	 *   later, calling code has a chance to finalize it.
	 * - bindings can represent expressions that include variables!
	 * 
	 * Therefore, this function should not actually *use* context, but
	 * only store it and return a Binding that may use it when queried.
	 * 
	 * The sourceLocation of the returned binding should be that
	 * of this expression.
	 */
	public abstract Binding<V> bind( Context context ) throws CompileError;
	
	public String toAtomicString() {
		return "(" + toString() + ")";
	}
}
