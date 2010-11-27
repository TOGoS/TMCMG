package togos.minecraft.mapgen.noise;

import togos.minecraft.mapgen.noise.api.FunctionDaDa_Da;
import togos.minecraft.mapgen.noise.api.FunctionDaDa_Ia;

public class AdaptDaDa_Da_Ia implements FunctionDaDa_Ia
{
	FunctionDaDa_Da next;
	public AdaptDaDa_Da_Ia( FunctionDaDa_Da next ) {
		this.next = next;
	}
	
	public void apply( int count, double[] inX, double[] inY, int[] out ) {
		double[] dout = new double[count];
		next.apply(count, inX, inY, dout);
		for( int i=0; i<count; ++i ) {
			out[i] = (int)Math.floor(dout[i]);
		}
	}
}
