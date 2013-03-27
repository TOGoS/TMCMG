package togos.noise.v3.program.structure;

import java.util.Map;
import java.util.concurrent.Callable;

import togos.lang.SourceLocation;

/**
 * TODO: Rename to 'Expression' if Eclipse will ever let me.
 */
public abstract class Expression<V> extends ProgramNode 
{
	public Expression( SourceLocation sLoc ) {
	    super(sLoc);
    }
	
	public abstract Callable<V> evaluate( Map<String,Callable<?>> context );
}
