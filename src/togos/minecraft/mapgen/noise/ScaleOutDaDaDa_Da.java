package togos.minecraft.mapgen.noise;

import togos.minecraft.mapgen.noise.api.FunctionDaDaDa_Da;

public class ScaleOutDaDaDa_Da implements FunctionDaDaDa_Da
{
	FunctionDaDaDa_Da next;
	double scale;
	public ScaleOutDaDaDa_Da( FunctionDaDaDa_Da next, double scale ) {
		this.next = next;
		this.scale = scale;
	}
	
	public void apply( int count, double[] inX, double[] inY, double[] inZ, double[] out ) {
		next.apply(count, inX, inY, inZ, out);
		for( int i=0; i<count; ++i ) out[i] *= scale;
	}
}
