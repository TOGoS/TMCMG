package togos.noise.v3.program.structure;

import togos.lang.SourceLocation;
import togos.noise.v3.parser.Parser;
import togos.noise.v3.program.runtime.Binding;
import togos.noise.v3.program.runtime.Context;

public class Constant<V> extends Expression<V>
{
	protected final V value;
	protected final Class<V> valueType;
	
	public Constant( V value, Class<V> valueType, SourceLocation sLoc ) {
		super( sLoc );
		this.value = value;
		this.valueType = valueType;
	}
	
	@Override public Binding<V> bind( Context context ) {
		return Binding.forValue( value, valueType, sLoc );
    }
	
	public static <V> Constant<V> forValue( V value, Class<V> valueType, SourceLocation sLoc ) {
		return new Constant<V>( value, valueType, sLoc );
	}
	
	@Override public String toString() {
		return Parser.toLiteral( value );
	}
	
	@Override public String toAtomicString() {
		return toString();
	}
}
