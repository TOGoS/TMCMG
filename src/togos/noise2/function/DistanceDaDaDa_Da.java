package togos.noise2.function;

import togos.noise2.InputDaDaDa;
import togos.noise2.rewrite.ExpressionRewriter;

public class DistanceDaDaDa_Da extends SmartFunctionDaDaDa_Da
{
	public void apply( InputDaDaDa in, double[] out ) {
		for( int i=in.count-1; i>=0; --i ) {
			out[i] = Math.sqrt(in.x[i]*in.x[i]+in.y[i]*in.y[i]+in.z[i]*in.z[i]);
		}
	}
	
	public boolean isConstant() {  return false;  }
	
	public Object rewriteSubExpressions(ExpressionRewriter rw) {
		return this;
	}
	
	public String toString() {
		return "distance";
	}
}
