package togos.noise2.vm.dftree.func;

import togos.noise2.vm.dftree.lang.Expression;

public abstract class TNLFunctionDaDaDa_Da
	implements FunctionDaDaDa_Da, Expression, PossiblyConstant
{
	public String toString() {
		throw new RuntimeException(getClass()+"#toString must be specified separately from toTnl");
	}
	
	public int getTriviality() {
		return 0;
	}
}
