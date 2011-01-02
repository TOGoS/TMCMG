package togos.noise2.function;

import togos.noise2.rewrite.ExpressionRewriter;


public class PerlinDaDaDa_Da implements SmartFunctionDaDaDa_Da
{
	public static PerlinDaDaDa_Da instance = new PerlinDaDaDa_Da();
	
	public D5_2Perlin perlin = new D5_2Perlin();

	public void apply( int count, double[] inX, double[] inY, double[] inZ, double[] out ) {
	    for( int i=0; i<count; ++i ) {
	    	out[i] = perlin.get(inX[i], inY[i], inZ[i]);
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
