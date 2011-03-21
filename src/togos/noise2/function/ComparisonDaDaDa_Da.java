package togos.noise2.function;

import togos.noise2.lang.Expression;

public abstract class ComparisonDaDaDa_Da extends TNLFunctionDaDaDa_Da
{
	TNLFunctionDaDaDa_Da a, b;
	
	public ComparisonDaDaDa_Da( TNLFunctionDaDaDa_Da a, TNLFunctionDaDaDa_Da b ) {
		this.a = a;
		this.b = b;
	}
	
	public boolean isConstant() {
		return a.isConstant() && b.isConstant();
	}
	
	protected abstract String getOperatorSymbol();
	
	public String toTnl() {
		return "(" + a.toTnl() + " " + getOperatorSymbol() + " " + b.toTnl() + ")";
	}
	
	public Expression[] directSubExpressions() {
		return new Expression[]{ a, b };
	}
}
