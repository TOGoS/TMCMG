package togos.noise2.vm.dftree.func;

import togos.noise2.rewrite.ExpressionRewriter;
import togos.noise2.vm.dftree.data.DataDa;
import togos.noise2.vm.dftree.data.DataDaDaDa;

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
	
	public Object[] directSubExpressions() {
		return new Object[]{};
	}
	
	public String toTnl() {
		return "distance";
	}
}
