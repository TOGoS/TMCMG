package togos.noise2.function;

import togos.noise2.InputDaDaDa;
import togos.noise2.rewrite.ExpressionRewriter;

public class Y extends SmartFunctionDaDaDa_Da
{
	public static final Y instance = new Y();
	
	public void apply(InputDaDaDa in, double[] out) {
		for( int i=0; i<in.count; ++i ) {
			out[i] = in.y[i];
		}
	}
	
	public boolean isConstant() {  return false;  }
	
	public Object rewriteSubExpressions(ExpressionRewriter rw) {
		return this;
	}
	
	public String toString() { return "y"; }
}
