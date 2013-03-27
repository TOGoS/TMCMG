package togos.noise.v3.program.structure;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import togos.lang.SourceLocation;
import togos.noise.v3.program.runtime.ValueHandle;

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
    public Callable<V> evaluate( Map<String,Callable<?>> context ) {
		final Map<String,Callable<?>> newContext = new HashMap<String,Callable<?>>(context);
		for( final Map.Entry<String,Expression<Object>> symbolDef : symbolDefinitions.entrySet() ) {
			newContext.put(
				symbolDef.getKey(),
				new ValueHandle<Object>( symbolDef.getValue().sLoc ) {
					@Override protected Object evaluate() throws Exception {
						return symbolDef.getValue().evaluate(newContext).call();
                    }
				}
			);
		}
		return new ValueHandle<V>( value.sLoc ) {
			@Override protected V evaluate() throws Exception {
				return value.evaluate(newContext).call();
            }
		};
    }
}
