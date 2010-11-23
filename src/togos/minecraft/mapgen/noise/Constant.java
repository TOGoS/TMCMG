package togos.minecraft.mapgen.noise;

import togos.minecraft.mapgen.noise.api.FunctionDaDaDa_Da;

public class Constant implements FunctionDaDaDa_Da
{
	double value; // this sale is for a limited time only!
	
	public Constant( double value ) {
		this.value = value;
	}
	
	public void apply( int count, double[] inX, double[] inY, double[] inZ, double[] out ) {
		for( int j=0; j<count; ++j ) {
			out[j] = value;
		}
	}
}
