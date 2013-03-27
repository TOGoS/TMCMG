package togos.noise.v3.program.structure;

import java.util.Map;
import java.util.concurrent.Callable;

import togos.lang.ScriptError;
import togos.lang.SourceLocation;
import togos.noise.v3.program.runtime.ValueHandle;

public class SymbolReference extends Expression<Object>
{
	final String symbol;
	
	public SymbolReference( String symbol, SourceLocation sLoc ) {
		super( sLoc );
		this.symbol = symbol;
	}

	@Override
    public Callable<Object> evaluate( final Map<String, Callable<?>> context ) {
		return new ValueHandle<Object>( sLoc ) {
			@Override
            protected Object evaluate() throws Exception {
				if( !context.containsKey(symbol) ) {
					throw new ScriptError( "Symbol '"+symbol+"' is undefined", sLoc );
				}
				return context.get(symbol).call();
            }
		};
    }
}
