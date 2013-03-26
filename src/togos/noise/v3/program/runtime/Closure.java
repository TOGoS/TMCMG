package togos.noise.v3.program.runtime;

import java.util.Map;
import java.util.concurrent.Callable;

import togos.noise.Function;
import togos.noise.v3.program.structure.FunctionDefinition;

public class Closure<V> implements Function<BoundArgumentList,Callable<V>>
{
	final FunctionDefinition<V> function;
	final Map<String,Callable<?>> context;
	
	public Closure( FunctionDefinition<V> function, Map<String,Callable<?>> context ) {
		this.function = function;
		this.context = context;
	}
	
	public Callable<V> apply( BoundArgumentList args ) {
		return null; // TODO
	}
}
