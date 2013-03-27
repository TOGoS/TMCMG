package togos.noise.v3.program.structure;

import java.util.Map;

import togos.lang.SourceLocation;
import togos.noise.v3.program.runtime.Binding;
import togos.noise.v3.program.runtime.Context;

public class Block<V> extends Expression<V>
{
	final Map<String,Expression<Object>> symbolDefinitions;
	final Expression<V> value;
	
	public Block( Map<String,Expression<Object>> defs, Expression<V> value, SourceLocation sLoc ) {
	    super(sLoc);
	    this.symbolDefinitions = defs;
	    this.value = value;
    }
	
	@Override
    public Binding<V> evaluate( Context context ) {
		final Context newContext = new Context(context);
		for( final Map.Entry<String,Expression<Object>> symbolDef : symbolDefinitions.entrySet() ) {
			newContext.put(
				symbolDef.getKey(),
				new Binding.Constant<Object>( symbolDef.getValue().sLoc ) {
					@Override protected Object evaluate() throws Exception {
						return symbolDef.getValue().evaluate(newContext).getValue();
                    }
				}
			);
		}
		return new Binding.Constant<V>( value.sLoc ) {
			@Override protected V evaluate() throws Exception {
				return value.evaluate(newContext).getValue();
            }
		};
    }
}
