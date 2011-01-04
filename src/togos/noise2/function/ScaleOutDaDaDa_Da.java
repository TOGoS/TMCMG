package togos.noise2.function;

import togos.noise2.data.DataDa;
import togos.noise2.data.DataDaDaDa;
import togos.noise2.rewrite.ExpressionRewriter;

public class ScaleOutDaDaDa_Da extends SmartFunctionDaDaDa_Da
{
	SmartFunctionDaDaDa_Da next;
	double scale;
	public ScaleOutDaDaDa_Da( double scale, SmartFunctionDaDaDa_Da next ) {
		this.next = next;
		this.scale = scale;
	}
	
	public DataDa apply( DataDaDaDa in ) {
		double[] scaled = next.apply(in).v;
		double[] out = new double[in.getLength()];
		for( int i=in.getLength()-1; i>=0; --i ) {
			out[i] = scaled[i] * scale;
		}
		return new DataDa(out);
	}
	
	public boolean isConstant() {
		return next.isConstant();
	}
	
	public Object rewriteSubExpressions(ExpressionRewriter rw) {
		return new ScaleOutDaDaDa_Da(scale,
			(SmartFunctionDaDaDa_Da)rw.rewrite(next));
	}

	
	public String toString() {
		return "scale-out("+scale+", "+next+")";
	}
}
