package togos.noise2.function;

import togos.noise2.rewrite.ExpressionRewriter;

public class SimplexDaDaDa_Da implements SmartFunctionDaDaDa_Da
{
	public static SimplexDaDaDa_Da instance = new SimplexDaDaDa_Da();
	
	public void apply( int count, double[] inX, double[] inY, double[] inZ, double[] out ) {
		SimplexNoise sn = new SimplexNoise();
		for( int i=0; i<count; ++i ) {
			out[i] = sn.apply((float)inX[i], (float)inY[i], (float)inZ[i]);
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
