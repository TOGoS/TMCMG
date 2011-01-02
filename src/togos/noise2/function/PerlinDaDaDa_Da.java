package togos.noise2.function;

import togos.noise2.InputDaDaDa;
import togos.noise2.rewrite.ExpressionRewriter;


public class PerlinDaDaDa_Da extends SmartFunctionDaDaDa_Da
{
	public static PerlinDaDaDa_Da instance = new PerlinDaDaDa_Da();
	
	public D5_2Perlin perlin = new D5_2Perlin();

	public void apply( InputDaDaDa in, double[] out ) {
	    for( int i=in.count-1; i>=0; --i ) {
	    	out[i] = perlin.get(in.x[i], in.y[i], in.z[i]);
	    }
    }
	
	public boolean isConstant() {  return false;  }
	
	public Object rewriteSubExpressions(ExpressionRewriter rw) {
		return this;
	}
	
	public String toString() {
		return "perlin";
	}
}
