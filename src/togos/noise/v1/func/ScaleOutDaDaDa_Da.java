package togos.noise.v1.func;

import togos.noise.v1.data.DataDa;
import togos.noise.v1.data.DataDaDaDa;
import togos.noise.v1.rewrite.ExpressionRewriter;

public class ScaleOutDaDaDa_Da extends TNLFunctionDaDaDa_Da
{
	TNLFunctionDaDaDa_Da next;
	double scale;
	public ScaleOutDaDaDa_Da( double scale, TNLFunctionDaDaDa_Da next ) {
		this.next = next;
		this.scale = scale;
	}
	
	public DataDa apply( DataDaDaDa in ) {
		final int vectorSize = in.getLength();
		double[] scaled = next.apply(in).x;
		double[] out = new double[vectorSize];
		for( int i=vectorSize-1; i>=0; --i ) {
			out[i] = scaled[i] * scale;
		}
		return new DataDa(vectorSize,out);
	}
	
	public boolean isConstant() {
		return next.isConstant();
	}
	
	public Object rewriteSubExpressions(ExpressionRewriter rw) {
		return new ScaleOutDaDaDa_Da(scale,
			(TNLFunctionDaDaDa_Da)rw.rewrite(next));
	}
	
	public Object[] directSubExpressions() {
		return new Object[]{ next };
	}
	
	public String toTnl() {
		return "scale-out("+scale+", "+next.toTnl()+")";
	}
}
