package togos.noise.v3.program.structure;

import togos.lang.CompileError;
import togos.lang.SourceLocation;
import togos.noise.v3.program.runtime.Binding;
import togos.noise.v3.program.runtime.Closure;
import togos.noise.v3.program.runtime.Context;
import togos.noise.v3.program.runtime.Function;

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
		return (Binding<Closure<V>>)Binding.forValue( new Closure<V>( FunctionDefinition.this, context ), sLoc );
    }
	
	@Override public String toString() {
		return parameterList.toAtomicString() + " -> " + definition.toAtomicString();
	}
}
