package togos.minecraft.mapgen.noise;

import togos.minecraft.mapgen.noise.api.FunctionDaDaDa_Da;
import togos.minecraft.mapgen.noise.api.FunctionDaDa_Da;

public class Constant implements FunctionDaDaDa_Da, FunctionDaDa_Da
{
	public static final Constant ZERO = new Constant(0);
	
	public static Constant forValue( double v ) {
		if( v == 0 ) {
			return ZERO;
		} else {
			return new Constant(v);
		}
	}
	
	double value; // this sale is for a limited time only!
	
	public Constant( double value ) {
		this.value = value;
	}
	
	public void apply( int count, double[] out ) {
		for( int j=0; j<count; ++j ) {
			out[j] = value;
		}
	}
	
	public void apply( int count, double[] inX, double[] inY, double[] inZ, double[] out ) {
		apply( count, out );
	}
	
	public void apply( int count, double[] inX, double[] inY, double[] out ) {
		apply( count, out );
	}
}
