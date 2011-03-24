package togos.noise2.function;

import togos.noise2.data.DataDa;
import togos.noise2.data.DataDaDaDa;
import togos.noise2.rewrite.ExpressionRewriter;

public class X extends TNLFunctionDaDaDa_Da
{
	public static final X instance = new X();
	
	public DataDa apply(DataDaDaDa in) {
		return new DataDa(in.x);
	}
	
	public boolean isConstant() {  return false;  }
	
	public Object rewriteSubExpressions(ExpressionRewriter rw) {
		return this;
	}
	
	public Object[] directSubExpressions() {
		return new Object[]{};
	}
	
	public String toTnl() { return "x"; }
}
