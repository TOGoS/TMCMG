package togos.noise2.function;

import togos.noise2.InputDaDaDa;
import togos.noise2.rewrite.ExpressionRewriter;

public class FractalDaDaDa_Da extends SmartFunctionDaDaDa_Da
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
	
	public void apply(InputDaDaDa in, double[] out) {
		double[] xfX = new double[in.count];
		double[] xfY = new double[in.count];
		double[] xfZ = new double[in.count];
		double[] subOut = new double[in.count];
		double hs = this.inithscale;
		double vs = this.initvscale;
		for( int j=in.count-1; j>=0; --j ) {
			out[j] = 0;
		}
		for( int i=0; i<iterations; ++i ) {
			for( int j=in.count-1; j>=0; --j ) {
				xfX[j] = in.x[j]/hs;
				xfY[j] = in.y[j]/hs;
				xfZ[j] = in.z[j]/hs+ztrans*i;
			}
			next.apply(in.count, xfX, xfY, xfZ, subOut);
			for( int j=in.count-1; j>=0; --j ) {
				out[j] += subOut[j] * vs;
			}
			hs *= hscale;
			vs *= vscale;
		}
	}
	
	public boolean isConstant() {
		return next.isConstant();
	}
	
	public Object rewriteSubExpressions(ExpressionRewriter rw) {
		return this;
	}
	
	public String toString() {
		return "fractal("+iterations+", "+inithscale+", "+initvscale+", "+hscale+", "+vscale+", "+ztrans+", "+next+")";
	}
}
