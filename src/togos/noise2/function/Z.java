package togos.noise2.function;

import togos.noise2.rewrite.ExpressionRewriter;

public class Z implements SmartFunctionDaDaDa_Da
{
	public static final Z instance = new Z();
	
	public void apply(int count, double[] inX, double[] inY, double[] inZ, double[] out) {
		for( int i=0; i<count; ++i ) {
			out[i] = inZ[i];
		}
	}
	
	public boolean isConstant() {  return false;  }
	
	public Object rewriteSubExpressions(ExpressionRewriter rw) {
		return this;
	}
	
	public String toString() { return "z"; }
}
