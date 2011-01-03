package togos.noise2.function;

import togos.noise2.InputDaDaDa;
import togos.noise2.rewrite.ExpressionRewriter;

public class TranslateInDaDaDa_Da extends SmartFunctionDaDaDa_Da
{
	double dx, dy, dz;
	SmartFunctionDaDaDa_Da next;
	public TranslateInDaDaDa_Da( double dx, double dy, double dz, SmartFunctionDaDaDa_Da next ) {
		this.dx = dx;
		this.dy = dy;
		this.dz = dz;
		this.next = next;
	}
	
	public void apply( InputDaDaDa in, double[] out ) {
		double[] tx = new double[in.count];
		double[] ty = new double[in.count];
		double[] tz = new double[in.count];
		for( int i=in.count-1; i>=0; --i ) {
			tx[i] = in.x[i]+dx;
			ty[i] = in.y[i]+dy;
			tz[i] = in.z[i]+dz;
		}
		next.apply(in.count, tx, ty, tz, out);
	}
	
	public boolean isConstant() {
		return next.isConstant();
	}
	
	public Object rewriteSubExpressions(ExpressionRewriter rw) {
		return new TranslateInDaDaDa_Da(dx, dy, dz,
			(SmartFunctionDaDaDa_Da)rw.rewrite(next));
	}
	
	public String toString() {
		return "translate-in("+dx+", "+dy+", "+dz+", "+next+")";
	}
}
