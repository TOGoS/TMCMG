package togos.noise2.function;

import togos.noise2.data.DataDa;
import togos.noise2.data.DataDaDaDa;
import togos.noise2.rewrite.ExpressionRewriter;

public class SimplexDaDaDa_Da extends TNLFunctionDaDaDa_Da
{
	public static SimplexDaDaDa_Da instance = new SimplexDaDaDa_Da();
	
	public DataDa apply( DataDaDaDa in ) {
		double[] out = new double[in.getLength()];
		SimplexNoise sn = new SimplexNoise();
		for( int i=in.getLength()-1; i>=0; --i ) {
			out[i] = sn.apply((float)in.x[i], (float)in.y[i], (float)in.z[i]);
		}
		return new DataDa(out);
	}
	
	public boolean isConstant() {  return false;  }
	
	public Object rewriteSubExpressions(ExpressionRewriter rw) {
		return this;
	}
	
	public Object[] directSubExpressions() {
		return new Object[]{};
	}
	
	public String toTnl() {
		return "simplex";
	}
}
