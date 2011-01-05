package togos.noise2.rewrite;

import togos.noise2.function.TNLFunctionDaDaDa_Da;
import togos.noise2.lang.Expression;
import togos.noise2.lang.FunctionUtil;

public class ConstantFolder implements ExpressionRewriter
{
	public static ConstantFolder instance = new ConstantFolder();
	
	public Object rewrite( Object f ) {
		if( f instanceof TNLFunctionDaDaDa_Da &&
			((TNLFunctionDaDaDa_Da)f).isConstant() )
		{
			return FunctionUtil.getConstantFunction((TNLFunctionDaDaDa_Da)f);
		} else if( f instanceof Expression ) {
			return ((Expression)f).rewriteSubExpressions(this);
		} else {
			return f;
		}
	}
}
