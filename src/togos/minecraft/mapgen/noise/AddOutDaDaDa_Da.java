package togos.minecraft.mapgen.noise;

import togos.minecraft.mapgen.noise.api.FunctionDaDaDa_Da;

public class AddOutDaDaDa_Da implements FunctionDaDaDa_Da
{
	FunctionDaDaDa_Da[] components;
	public AddOutDaDaDa_Da( FunctionDaDaDa_Da[] components ) {
		this.components = components;
	}
	
	public void apply( int count, double[] inX, double[] inY, double[] inZ, double[] out ) {
		for( int i=0; i<count; ++i ) out[i] = 0;
		double[] subOut = new double[count];
		for( int i=0; i<components.length; ++i ) {
			components[i].apply(count, inX, inY, inZ, subOut);
			for( int j=0; j<count; ++j ) {
				out[j] += subOut[j];
			}
		}
	}
}
