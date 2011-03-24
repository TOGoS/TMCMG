package togos.noise2.function;

import togos.noise2.data.DataDa;
import togos.noise2.data.DataDaDaDa;
import togos.noise2.lang.FunctionUtil;
import togos.noise2.rewrite.ExpressionRewriter;

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
