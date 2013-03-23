package togos.noise.v1.func;

import togos.noise.v1.data.DataDa;
import togos.noise.v1.data.DataDaDaDa;
import togos.noise.v1.rewrite.ExpressionRewriter;

public class Z extends TNLFunctionDaDaDa_Da
{
	public static final Z instance = new Z();
	
	public DataDa apply(DataDaDaDa in) {
		return new DataDa(in.getLength(),in.z);
	}
	
	public boolean isConstant() {  return false;  }
	
	public Object rewriteSubExpressions(ExpressionRewriter rw) {
		return this;
	}
	
	public Object[] directSubExpressions() {
		return new Object[]{};
	}
	
	public String toString() { return "z"; }
	public String toTnl() { return "z"; }
	
	public int getTriviality() {
	    return 200;
	}
}
