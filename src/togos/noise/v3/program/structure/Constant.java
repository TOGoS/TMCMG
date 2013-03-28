package togos.noise.v3.program.structure;

import togos.lang.SourceLocation;
import togos.noise.v3.parser.Parser;
import togos.noise.v3.program.runtime.Binding;
import togos.noise.v3.program.runtime.Context;

public class Constant<V> extends Expression<V>
{
	public final V value;
	public Constant( V value, SourceLocation sLoc ) {
		super( sLoc );
		this.value = value;
	}

	@Override public Binding<V> bind( Context context ) {
		return new Binding.Constant<V>( value, sLoc );
    }
	
	public static <V> Constant<V> withValue( V value, SourceLocation sLoc ) {
		return new Constant<V>( value, sLoc );
	}
	
	@Override public String toString() {
		if( value instanceof Number ) {
			return value.toString();
		} else if( value instanceof String ) {
			return Parser.quote((String)value);
		} else {
			return value.toString();
		}
	}
	
	@Override public String toAtomicString() {
		return toString();
	}
}
