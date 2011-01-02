package togos.noise2.rewrite;

import togos.noise2.function.SmartFunctionDaDaDa_Da;
import togos.noise2.lang.Expression;
import togos.noise2.lang.FunctionUtil;

public class ConstantFolder implements ExpressionRewriter
{
	public static ConstantFolder instance = new ConstantFolder();
	
	public Object rewrite( Object f ) {
		if( f instanceof SmartFunctionDaDaDa_Da &&
			((SmartFunctionDaDaDa_Da)f).isConstant() )
		{
			return FunctionUtil.getConstantFunction((SmartFunctionDaDaDa_Da)f);
		} else if( f instanceof Expression ) {
			return ((Expression)f).rewriteSubExpressions(this);
		} else {
			return f;
		}
	}
}
