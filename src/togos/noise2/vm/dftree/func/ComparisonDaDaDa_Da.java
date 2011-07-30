package togos.noise2.vm.dftree.func;

import togos.noise2.vm.dftree.lang.FunctionUtil;


public abstract class ComparisonDaDaDa_Da extends TNLFunctionDaDaDa_Da
{
	FunctionDaDaDa_Da a, b;
	
	public ComparisonDaDaDa_Da( FunctionDaDaDa_Da a, FunctionDaDaDa_Da b ) {
		this.a = a;
		this.b = b;
	}
	
	public boolean isConstant() {
		return FunctionUtil.isConstant(a) && FunctionUtil.isConstant(b);
	}
	
	protected abstract String getOperatorSymbol();
	
	public String toString() {
		return "(" + a + " " + getOperatorSymbol() + " " + b + ")";
	}
	
	public String toTnl() {
		return "(" + FunctionUtil.toTnl(a) + " " + getOperatorSymbol() + " " + FunctionUtil.toTnl(b) + ")";
	}
	
	public Object[] directSubExpressions() {
		return new Object[]{ a, b };
	}
}
