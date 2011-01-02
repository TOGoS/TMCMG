package togos.noise2.function;

import togos.noise2.rewrite.ExpressionRewriter;

public class ScaleOutDaDaDa_Da implements SmartFunctionDaDaDa_Da
{
	SmartFunctionDaDaDa_Da next;
	double scale;
	public ScaleOutDaDaDa_Da( double scale, SmartFunctionDaDaDa_Da next ) {
		this.next = next;
		this.scale = scale;
	}
	
	public void apply( int count, double[] inX, double[] inY, double[] inZ, double[] out ) {
		next.apply(count, inX, inY, inZ, out);
		for( int i=0; i<count; ++i ) out[i] *= scale;
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
