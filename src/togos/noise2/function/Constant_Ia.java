package togos.noise2.function;

import togos.noise2.data.DataDaDa;
import togos.noise2.data.DataIa;

public class Constant_Ia implements FunctionDaDa_Ia
{
	public static final Constant_Ia ZERO = new Constant_Ia(0);
	
	public static Constant_Ia forValue( int v ) {
		if( v == 0 ) {
			return ZERO;
		} else {
			return new Constant_Ia(v);
		}
	}
	
	int value;
	
	public Constant_Ia( int value ) {
		this.value = value;
	}
	
	public DataIa apply( int count ) {
		int[] out = new int[count];
		for( int j=0; j<count; ++j ) {
			out[j] = value;
		}
		return new DataIa(out);
	}
	
	public DataIa apply( DataDaDa in ) {
		return apply( in.getLength() );
	}
	
	public boolean equals( Object oth ) {
		if( !(oth instanceof Constant_Ia) ) return false;
		return value == ((Constant_Ia)oth).value;
	}
	
	public String toString() {
		return "constant-int("+value+")";
	}
	
	public String toTnl() {
		return Integer.toString(value);
	}
}
