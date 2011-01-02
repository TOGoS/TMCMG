package togos.noise2.function;

import togos.noise2.InputDaDaDa;
import togos.noise2.lang.Expression;

public abstract class SmartFunctionDaDaDa_Da
	implements FunctionDaDaDa_Da, Expression
{
	public abstract boolean isConstant();
	
	public abstract void apply( InputDaDaDa input, double[] output );
	
	public final void apply( int count, double[] inX, double[] inY, double[] inZ, double[] out ) {
		apply( new InputDaDaDa( count, inX, inY, inZ ), out );
	}
}
