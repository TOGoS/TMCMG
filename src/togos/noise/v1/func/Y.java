package togos.noise.v1.func;

import togos.noise.v1.data.DataDa;
import togos.noise.v1.data.DataDaDaDa;
import togos.noise.v1.rewrite.ExpressionRewriter;

public class Y extends TNLFunctionDaDaDa_Da
{
	public static final Y instance = new Y();
	
	public DataDa apply(DataDaDaDa in) {
		return new DataDa(in.getLength(),in.y);
	}
	
	public boolean isConstant() {  return false;  }
	
	public Object rewriteSubExpressions(ExpressionRewriter rw) {
		return this;
	}
	
	public Object[] directSubExpressions() {
		return new Object[]{};
	}
	
	public String toString() { return "y"; }
	public String toTnl() { return "y"; }
	
	public int getTriviality() {
	    return 200;
	}
}
