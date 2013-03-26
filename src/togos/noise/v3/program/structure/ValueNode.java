package togos.noise.v3.program.structure;

import java.util.Map;
import java.util.concurrent.Callable;

import togos.lang.SourceLocation;

public abstract class ValueNode<V> extends ProgramNode 
{
	public ValueNode( SourceLocation sLoc ) {
	    super(sLoc);
    }
	
	public abstract Callable<V> evaluate( Map<String,Callable<?>> context );
}
