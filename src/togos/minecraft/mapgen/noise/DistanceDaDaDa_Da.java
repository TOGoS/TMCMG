package togos.minecraft.mapgen.noise;

import togos.minecraft.mapgen.noise.api.FunctionDaDaDa_Da;

public class DistanceDaDaDa_Da implements FunctionDaDaDa_Da
{
	public void apply( int count, double[] inX, double[] inY, double[] inZ, double[] out ) {
		for( int i=0; i<count; ++i ) {
			out[i] = Math.sqrt(inX[i]*inX[i]+inY[i]*inY[i]+inZ[i]*inZ[i]);
		}
	}
}
