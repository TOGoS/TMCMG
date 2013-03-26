package togos.noise.v3.program.structure;

import java.util.Map;
import java.util.concurrent.Callable;

import togos.lang.SourceLocation;
import togos.noise.v3.program.runtime.Closure;

public class FunctionDefinition<V> extends ValueNode<Closure<V>>
{
	final ParameterList parameterList;
	final ValueNode<V> definition;
	
	public FunctionDefinition( ParameterList parameterList, ValueNode<V> definition, SourceLocation sLoc ) {
	    super(sLoc);
	    this.parameterList = parameterList;
	    this.definition = definition;
    }

	@Override
    public Callable<Closure<V>> evaluate( final Map<String, Callable<?>> context ) {
		return new Callable<Closure<V>>() {
			@Override
            public Closure<V> call() throws Exception {
				return new Closure<V>( FunctionDefinition.this, context );
			}
		};
    }
}
