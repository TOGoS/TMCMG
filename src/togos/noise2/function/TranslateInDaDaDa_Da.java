package togos.noise2.function;

import togos.noise2.data.DataDa;
import togos.noise2.data.DataDaDaDa;
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
	
	public DataDa apply( DataDaDaDa in ) {
		double[] tx = new double[in.getLength()];
		double[] ty = new double[in.getLength()];
		double[] tz = new double[in.getLength()];
		for( int i=in.getLength()-1; i>=0; --i ) {
			tx[i] = in.x[i]+dx;
			ty[i] = in.y[i]+dy;
			tz[i] = in.z[i]+dz;
		}
		return next.apply(new DataDaDaDa(tx, ty, tz));
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
