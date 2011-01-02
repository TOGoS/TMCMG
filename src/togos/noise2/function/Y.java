package togos.noise2.function;

import togos.noise2.rewrite.ExpressionRewriter;

public class Y implements SmartFunctionDaDaDa_Da
{
	public static final Y instance = new Y();
	
	public void apply(int count, double[] inX, double[] inY, double[] inZ, double[] out) {
		for( int i=0; i<count; ++i ) {
			out[i] = inY[i];
		}
	}
	
	public boolean isConstant() {  return false;  }
	
	public Object rewriteSubExpressions(ExpressionRewriter rw) {
		return this;
	}
	
	public String toString() { return "y"; }
}
