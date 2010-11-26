package togos.minecraft.mapgen.noise;

import togos.minecraft.mapgen.noise.api.FunctionDaDa_Da;
import togos.minecraft.mapgen.noise.api.FunctionDaDaDa_Da;

public class AdaptDaDa_DaDaDa_Da implements FunctionDaDa_Da
{
	FunctionDaDa_Da z;
	FunctionDaDaDa_Da next;
	public AdaptDaDa_DaDaDa_Da( FunctionDaDa_Da z, FunctionDaDaDa_Da next ) {
		this.z = z;
		this.next = next;
	}
	public AdaptDaDa_DaDaDa_Da( FunctionDaDaDa_Da next ) {
		this( Constant_Da.ZERO, next );
	}
	
	public void apply( int count, double[] inX, double[] inY, double[] out ) {
		double[] inZ = new double[count];
		z.apply(count, inX, inY, inZ);
		next.apply(count, inX, inY, inZ, out);
	}
}
