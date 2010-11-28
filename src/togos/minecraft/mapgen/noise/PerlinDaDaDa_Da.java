package togos.minecraft.mapgen.noise;

import togos.minecraft.mapgen.noise.api.FunctionDaDaDa_Da;

public class PerlinDaDaDa_Da implements FunctionDaDaDa_Da
{
	public static PerlinDaDaDa_Da instance = new PerlinDaDaDa_Da();
	
	public D5_2Perlin perlin = new D5_2Perlin();

	public void apply( int count, double[] inX, double[] inY, double[] inZ, double[] out ) {
	    for( int i=0; i<count; ++i ) {
	    	out[i] = perlin.get(inX[i], inY[i], inZ[i]);
	    }
    }
}
