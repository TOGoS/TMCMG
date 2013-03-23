package togos.noise.v1.func;

import togos.noise.v1.data.DataDa;
import togos.noise.v1.data.DataDaDaDa;
import togos.noise.v1.lang.FunctionUtil;
import togos.noise.v1.rewrite.ExpressionRewriter;

public class TranslateInDaDaDa_Da extends TNLFunctionDaDaDa_Da
{
	double dx, dy, dz;
	FunctionDaDaDa_Da next;
	public TranslateInDaDaDa_Da( double dx, double dy, double dz, FunctionDaDaDa_Da next ) {
		this.dx = dx;
		this.dy = dy;
		this.dz = dz;
		this.next = next;
	}
	
	public DataDa apply( DataDaDaDa in ) {
		final int vectorSize = in.getLength();
		double[] tx = new double[vectorSize];
		double[] ty = new double[vectorSize];
		double[] tz = new double[vectorSize];
		for( int i=vectorSize-1; i>=0; --i ) {
			tx[i] = in.x[i]+dx;
			ty[i] = in.y[i]+dy;
			tz[i] = in.z[i]+dz;
		}
		return next.apply(new DataDaDaDa(vectorSize, tx, ty, tz));
	}
	
	public boolean isConstant() {
		return FunctionUtil.isConstant(next);
	}
	
	public Object rewriteSubExpressions(ExpressionRewriter rw) {
		return new TranslateInDaDaDa_Da(dx, dy, dz,
			(TNLFunctionDaDaDa_Da)rw.rewrite(next));
	}
	
	public Object[] directSubExpressions() {
		return new Object[]{};
	}
	
	public String toTnl() {
		return "translate-in("+dx+", "+dy+", "+dz+", "+FunctionUtil.toTnl(next)+")";
	}
}
