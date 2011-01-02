package togos.noise2.function;

import togos.noise2.InputDaDaDa;
import togos.noise2.rewrite.ExpressionRewriter;

public class SimplexDaDaDa_Da extends SmartFunctionDaDaDa_Da
{
	public static SimplexDaDaDa_Da instance = new SimplexDaDaDa_Da();
	
	public void apply( InputDaDaDa in, double[] out ) {
		SimplexNoise sn = new SimplexNoise();
		for( int i=in.count-1; i>=0; --i ) {
			out[i] = sn.apply((float)in.x[i], (float)in.y[i], (float)in.z[i]);
		}
	}
	
	public boolean isConstant() {  return false;  }
	
	public Object rewriteSubExpressions(ExpressionRewriter rw) {
		return this;
	}
	
	public String toString() {
		return "simplex";
	}
}
