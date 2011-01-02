package togos.noise2.function;

import togos.noise2.rewrite.ExpressionRewriter;

public class TranslateInDaDaDa_Da implements SmartFunctionDaDaDa_Da
{
	double dx, dy, dz;
	SmartFunctionDaDaDa_Da next;
	public TranslateInDaDaDa_Da( double dx, double dy, double dz, SmartFunctionDaDaDa_Da next ) {
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
