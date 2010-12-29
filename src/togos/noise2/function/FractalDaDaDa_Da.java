package togos.noise2.function;

import togos.noise2.lang.FunctionUtil;

public class FractalDaDaDa_Da implements SmartFunctionDaDaDa_Da
{
	int iterations;
	double inithscale, hscale;
	double initvscale, vscale;
	double ztrans;
	SmartFunctionDaDaDa_Da next;
	
	public FractalDaDaDa_Da( int iterations, double inithscale, double initvscale, double hscale, double vscale, double ztrans, SmartFunctionDaDaDa_Da next ) {
		this.iterations = iterations;
		this.inithscale = inithscale;
		this.initvscale = initvscale;
		this.hscale = hscale;
		this.vscale = vscale;
		this.ztrans = ztrans;
		this.next = next;
	}
	
	public void apply(int count, double[] inX, double[] inY, double[] inZ, double[] out) {
		double[] xfX = new double[count];
		double[] xfY = new double[count];
		double[] xfZ = new double[count];
		double[] subOut = new double[count];
		double hs = this.inithscale;
		double vs = this.initvscale;
		for( int j=0; j<count; ++j ) {
			out[j] = 0;
		}
		for( int i=0; i<iterations; ++i ) {
			for( int j=0; j<count; ++j ) {
				xfX[j] = inX[j]/hs;
				xfY[j] = inY[j]/hs;
				xfZ[j] = inZ[j]/hs+ztrans*i;
			}
			next.apply(count, xfX, xfY, xfZ, subOut);
			for( int j=0; j<count; ++j ) {
				out[j] += subOut[j] * vs;
			}
			hs *= hscale;
			vs *= vscale;
		}
	}
	
	public boolean isConstant() {
		return next.isConstant();
	}
	
	public SmartFunctionDaDaDa_Da simplify() {
		return FunctionUtil.collapseIfConstant(this);
	}
}
