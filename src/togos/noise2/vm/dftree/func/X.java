package togos.noise2.vm.dftree.func;

import togos.noise2.rewrite.ExpressionRewriter;
import togos.noise2.vm.dftree.data.DataDa;
import togos.noise2.vm.dftree.data.DataDaDaDa;

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
