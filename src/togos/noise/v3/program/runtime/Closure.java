package togos.noise.v3.program.runtime;

import togos.noise.Function;
import togos.noise.v3.program.structure.FunctionDefinition;

public class Closure<V> implements Function<BoundArgumentList,Binding<V>>
{
	final FunctionDefinition<V> function;
	final Context context;
	
	public Closure( FunctionDefinition<V> function, Context context ) {
		this.function = function;
		this.context = context;
	}
	
	public Binding<V> apply( BoundArgumentList args ) {
		return null; // TODO
	}
}
