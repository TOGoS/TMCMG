package togos.noise2.function;

import togos.noise2.InputDaDaDa;
import togos.noise2.rewrite.ExpressionRewriter;

public class ClampOutDaDaDa_Da extends SmartFunctionDaDaDa_Da
{
	SmartFunctionDaDaDa_Da lower;
	SmartFunctionDaDaDa_Da upper;
	SmartFunctionDaDaDa_Da clamped;
	
	public ClampOutDaDaDa_Da( SmartFunctionDaDaDa_Da lower, SmartFunctionDaDaDa_Da upper, SmartFunctionDaDaDa_Da clamped ) {
		this.lower = lower;
		this.upper = upper;
		this.clamped = clamped;
	}
	
	public void apply( InputDaDaDa in, double[] out ) {
		double[] lower = new double[in.count];
		this.lower.apply(in, lower);
		double[] upper = new double[in.count];
		this.upper.apply(in, upper);
		this.clamped.apply(in, out);
		for( int i=in.count-1; i>=0; --i ) {
			if( out[i] < lower[i] ) out[i] = lower[i];
			if( out[i] > upper[i] ) out[i] = upper[i];
		}
	}
	
	public boolean isConstant() {
		return lower.isConstant() && upper.isConstant() && clamped.isConstant();
	}
	
	public Object rewriteSubExpressions(ExpressionRewriter rw) {
		return new ClampOutDaDaDa_Da(
			(SmartFunctionDaDaDa_Da)rw.rewrite(lower),
			(SmartFunctionDaDaDa_Da)rw.rewrite(upper),
			(SmartFunctionDaDaDa_Da)rw.rewrite(clamped)
		);
	}
	
	public String toString() {
		return "clamp("+lower+", "+upper+", "+clamped+")";
	}
}
