package togos.noise.v3.program.structure;

import togos.lang.SourceLocation;
import togos.noise.v3.program.runtime.Binding;
import togos.noise.v3.program.runtime.Closure;
import togos.noise.v3.program.runtime.Context;

public class FunctionDefinition<V> extends Expression<Closure<V>>
{
	public final ParameterList parameterList;
	public final Expression<? extends V> definition;
	
	public FunctionDefinition( ParameterList parameterList, Expression<? extends V> definition, SourceLocation sLoc ) {
	    super(sLoc);
	    this.parameterList = parameterList;
	    this.definition = definition;
    }

	@Override
    public Binding<Closure<V>> bind( final Context context ) {
		return new Binding.Constant<Closure<V>>( sLoc ) {
			@Override protected Closure<V> evaluate() throws Exception {
	            return new Closure<V>( FunctionDefinition.this, context );
            }
		};
    }
	
	@Override public String toString() {
		return parameterList.toAtomicString() + " -> " + definition.toAtomicString();
	}
}
