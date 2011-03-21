package togos.noise2.function;

import togos.noise2.data.DataDa;
import togos.noise2.data.DataDaDaDa;
import togos.noise2.lang.Expression;
import togos.noise2.rewrite.ExpressionRewriter;

public class PerlinDaDaDa_Da extends TNLFunctionDaDaDa_Da
{
	public static PerlinDaDaDa_Da instance = new PerlinDaDaDa_Da();
	
	public D5_2Perlin perlin = new D5_2Perlin();

	public DataDa apply( DataDaDaDa in ) {
		double[] out = new double[in.getLength()];
	    for( int i=in.getLength()-1; i>=0; --i ) {
	    	out[i] = perlin.get(in.x[i], in.y[i], in.z[i]);
	    }
	    return new DataDa(out);
    }
	
	public boolean isConstant() {  return false;  }
	
	public Object rewriteSubExpressions(ExpressionRewriter rw) {
		return this;
	}
	
	public Expression[] directSubExpressions() {
		return new Expression[]{};
	}
	
	public String toTnl() {
		return "perlin";
	}
}
