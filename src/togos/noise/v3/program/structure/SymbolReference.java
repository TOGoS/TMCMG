package togos.noise.v3.program.structure;

import togos.lang.ScriptError;
import togos.lang.SourceLocation;
import togos.noise.v3.program.runtime.Binding;
import togos.noise.v3.program.runtime.Context;

public class SymbolReference extends Expression<Object>
{
	final String symbol;
	
	public SymbolReference( String symbol, SourceLocation sLoc ) {
		super( sLoc );
		this.symbol = symbol;
	}

	@Override
    public Binding<Object> evaluate( final Context context ) {
		return new Binding.Delegated<Object>() {
			@Override
            protected Binding<Object> generateDelegate() throws Exception {
				if( !context.containsKey(symbol) ) {
					throw new ScriptError( "Symbol '"+symbol+"' is undefined", sLoc );
				}
				return context.get(symbol);
            }
		};
    }
	
	@Override public String toString() {
		// TODO: If there is ever a symbol escaping mechanism, would need to use it here
		return symbol;
	}
	@Override public String toAtomicString() {
		return toString();
	}
}
