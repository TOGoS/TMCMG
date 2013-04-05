package togos.noise.v3.program.structure;

import java.util.Map;

import togos.lang.CompileError;
import togos.lang.SourceLocation;
import togos.noise.v3.program.runtime.Binding;
import togos.noise.v3.program.runtime.Context;

public class Block<V> extends Expression<V>
{
	final Map<String,Expression<?>> symbolDefinitions;
	final Expression<? extends V> value;
	
	public Block( Map<String,Expression<?>> defs, Expression<? extends V> value, SourceLocation sLoc ) {
	    super(sLoc);
	    this.symbolDefinitions = defs;
	    this.value = value;
    }
	
	@Override
    public Binding<V> bind( Context context ) throws CompileError {
		final Context newContext = new Context(context);
		for( final Map.Entry<String,Expression<?>> symbolDef : symbolDefinitions.entrySet() ) {
			newContext.put(
				symbolDef.getKey(),
				symbolDef.getValue().bind(newContext)
			);
		}
		return new Binding.Delegated<V>( value.bind(newContext), sLoc );
    }
	
	public String toString() {
		String r = "";
		for( Map.Entry<String,Expression<?>> def : symbolDefinitions.entrySet() ) {
			r += def.getKey() + " = " + def.getValue().toAtomicString();
			r += "; ";
		}
		return r + value.toAtomicString();
	}
}
