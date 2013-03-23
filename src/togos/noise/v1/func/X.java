package togos.noise.v1.func;

import togos.noise.v1.data.DataDa;
import togos.noise.v1.data.DataDaDaDa;
import togos.noise.v1.rewrite.ExpressionRewriter;

public class X extends TNLFunctionDaDaDa_Da
{
	public static final X instance = new X();
	
	public DataDa apply(DataDaDaDa in) {
		return new DataDa(in.getLength(),in.x);
	}
	
	public boolean isConstant() {  return false;  }
	
	public Object rewriteSubExpressions(ExpressionRewriter rw) {
		return this;
	}
	
	public Object[] directSubExpressions() {
		return new Object[]{};
	}
	
	public String toString() { return "x"; }
	public String toTnl() { return "x"; }
	
	public int getTriviality() {
	    return 200;
	}
}
