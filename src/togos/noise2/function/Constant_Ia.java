package togos.noise2.function;


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
	
	int value; // this sale is for a limited time only!
	
	public Constant_Ia( int value ) {
		this.value = value;
	}
	
	public void apply( int count, int[] out ) {
		for( int j=0; j<count; ++j ) {
			out[j] = value;
		}
	}
	
	public void apply( int count, double[] inX, double[] inY, int[] out ) {
		apply( count, out );
	}
	
	public boolean equals( Object oth ) {
		if( !(oth instanceof Constant_Ia) ) return false;
		return value == ((Constant_Ia)oth).value;
	}
}
