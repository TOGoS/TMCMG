package togos.noise.v1.func;

import togos.noise.v1.data.DataDa;
import togos.noise.v1.data.DataDaDaDa;
import togos.noise.v1.rewrite.ExpressionRewriter;

public class DistanceDaDaDa_Da extends TNLFunctionDaDaDa_Da
{
	public DataDa apply( DataDaDaDa in ) {
		final int len = in.getLength();
		double[] out = new double[len];
		for( int i=in.getLength()-1; i>=0; --i ) {
			out[i] = Math.sqrt(in.x[i]*in.x[i]+in.y[i]*in.y[i]+in.z[i]*in.z[i]);
		}
		return new DataDa(len, out);
	}
	
	public boolean isConstant() {  return false;  }
	
	public Object rewriteSubExpressions(ExpressionRewriter rw) {
		return this;
	}
	
	public Object[] directSubExpressions() {
		return new Object[]{};
	}
	
	public String toTnl() {
		return "distance";
	}
}
