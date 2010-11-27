package togos.minecraft.mapgen.noise;

import togos.minecraft.mapgen.noise.api.FunctionDaDaDa_Da;

public class TranslateInDaDaDa_Da implements FunctionDaDaDa_Da
{
	double dx, dy, dz;
	FunctionDaDaDa_Da next;
	public TranslateInDaDaDa_Da( double dx, double dy, double dz, FunctionDaDaDa_Da next ) {
		this.dx = dx;
		this.dy = dy;
		this.dz = dz;
		this.next = next;
	}
	
	public void apply( int count, double[] inX, double[] inY, double[] inZ,	double[] out ) {
		double[] tx = new double[count];
		double[] ty = new double[count];
		double[] tz = new double[count];
		for( int i=0; i<count; ++i ) {
			tx[i] = inX[i]+dx;
			ty[i] = inY[i]+dy;
			tz[i] = inZ[i]+dz;
		}
		next.apply(count, tx, ty, tz, out);
	}
}
