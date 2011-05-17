package togos.noise2.function;

import togos.noise2.data.DataDa;
import togos.noise2.data.DataDaDaDa;
import togos.noise2.lang.FunctionUtil;
import togos.noise2.rewrite.ExpressionRewriter;

public class FractalDaDaDa_Da extends TNLFunctionDaDaDa_Da
{
	int iterations;
	double inithscale, hscale;
	double initvscale, vscale;
	double ztrans;
	FunctionDaDaDa_Da next;
	
	public FractalDaDaDa_Da( int iterations, double inithscale, double initvscale, double hscale, double vscale, double ztrans, FunctionDaDaDa_Da next ) {
		this.iterations = iterations;
		this.inithscale = inithscale;
		this.initvscale = initvscale;
		this.hscale = hscale;
		this.vscale = vscale;
		this.ztrans = ztrans;
		this.next = next;
	}
	
	public DataDa apply(DataDaDaDa in) {
		double[] xfX = new double[in.getLength()];
		double[] xfY = new double[in.getLength()];
		double[] xfZ = new double[in.getLength()];
		double[] out = new double[in.getLength()];
		double hs = this.inithscale;
		double vs = this.initvscale;
		for( int j=in.getLength()-1; j>=0; --j ) {
			out[j] = 0;
		}
		for( int i=0; i<iterations; ++i ) {
			for( int j=in.getLength()-1; j>=0; --j ) {
				xfX[j] = in.x[j]/hs+ztrans*i;
				xfY[j] = in.y[j]/hs+ztrans*i;
				xfZ[j] = in.z[j]/hs+ztrans*i;
			}
			double[] subOut = next.apply(new DataDaDaDa(xfX,xfY,xfZ)).x;
			for( int j=in.getLength()-1; j>=0; --j ) {
				out[j] += subOut[j] * vs;
			}
			hs *= hscale;
			vs *= vscale;
		}
		return new DataDa(out);
	}
	
	public boolean isConstant() {
		return FunctionUtil.isConstant(next);
	}
	
	public Object rewriteSubExpressions(ExpressionRewriter rw) {
		return this;
	}
	
	public String toString() {
		return "fractal("+iterations+", "+inithscale+", "+initvscale+", "+hscale+", "+vscale+", "+ztrans+", "+next+")";
	}
	
	public String toTnl() {
		return "fractal("+iterations+", "+inithscale+", "+initvscale+", "+hscale+", "+vscale+", "+ztrans+", "+FunctionUtil.toTnl(next)+")";
	}
	
	public Object[] directSubExpressions() {
		return new Object[]{};
	}
}
