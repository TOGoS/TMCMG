package togos.noise.v3.program.structure;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import togos.lang.SourceLocation;
import togos.noise.v3.program.runtime.ValueHandle;

public class Block<V> extends ValueNode<V>
{
	Map<String,ValueNode<?>> symbolDefinitions = Collections.emptyMap();
	ValueNode<V> value;
	
	public Block( SourceLocation sLoc ) {
	    super(sLoc);
	    // TODO Auto-generated constructor stub
    }
	
	@Override
    public Callable<V> evaluate( Map<String,Callable<?>> context ) {
		final Map<String,Callable<?>> newContext = new HashMap<String,Callable<?>>(context);
		for( final Map.Entry<String,ValueNode<?>> symbolDef : symbolDefinitions.entrySet() ) {
			newContext.put(
				symbolDef.getKey(),
				new ValueHandle<Object>( symbolDef.getValue().sLoc ) {
					@Override
                    protected Object evaluate() throws Exception {
						return symbolDef.getValue().evaluate(newContext).call();
                    }
				}
			);
		}
		
	    // TODO Auto-generated method stub
	    return null;
    }
}
