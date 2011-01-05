package togos.noise2.function;

import togos.noise2.data.DataDa;
import togos.noise2.data.DataDaDaDa;
import togos.noise2.rewrite.ExpressionRewriter;

public class DistanceDaDaDa_Da extends TNLFunctionDaDaDa_Da
{
	public DataDa apply( DataDaDaDa in ) {
		double[] out = new double[in.getLength()];
		for( int i=in.getLength()-1; i>=0; --i ) {
			out[i] = Math.sqrt(in.x[i]*in.x[i]+in.y[i]*in.y[i]+in.z[i]*in.z[i]);
		}
		return new DataDa(out);
	}
	
	public boolean isConstant() {  return false;  }
	
	public Object rewriteSubExpressions(ExpressionRewriter rw) {
		return this;
	}
	
	public String toTnl() {
		return "distance";
	}
}
