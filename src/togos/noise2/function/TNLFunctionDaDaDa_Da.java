package togos.noise2.function;

import togos.noise2.lang.Expression;

public abstract class TNLFunctionDaDaDa_Da
	implements FunctionDaDaDa_Da, Expression
{
	public abstract boolean isConstant();
	
	public String toString() {
		return toTnl();
	}
}
