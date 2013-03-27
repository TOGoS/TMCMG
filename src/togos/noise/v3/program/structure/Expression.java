package togos.noise.v3.program.structure;

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
	 */
	public abstract Binding<V> evaluate( Context context );
	
	public String toAtomicString() {
		return "(" + toString() + ")";
	}
}
