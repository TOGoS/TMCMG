package togos.noise2.function;

import togos.noise2.InputDaDaDa;
import togos.noise2.rewrite.ExpressionRewriter;

public class ScaleOutDaDaDa_Da extends SmartFunctionDaDaDa_Da
{
	SmartFunctionDaDaDa_Da next;
	double scale;
	public ScaleOutDaDaDa_Da( double scale, SmartFunctionDaDaDa_Da next ) {
		this.next = next;
		this.scale = scale;
	}
	
	public void apply( InputDaDaDa in, double[] out ) {
		next.apply(in, out);
		for( int i=in.count-1; i>=0; --i ) out[i] *= scale;
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
