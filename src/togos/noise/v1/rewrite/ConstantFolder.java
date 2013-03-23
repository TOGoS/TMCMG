package togos.noise.v1.rewrite;

import togos.noise.v1.func.FunctionDaDaDa_Da;
import togos.noise.v1.func.FunctionDaDaDa_Ia;
import togos.noise.v1.func.PossiblyConstant;
import togos.noise.v1.lang.Expression;
import togos.noise.v1.lang.FunctionUtil;

public class ConstantFolder implements ExpressionRewriter
{
	public static ConstantFolder instance = new ConstantFolder();
	
	public Object rewrite( Object f ) {
		if( f instanceof PossiblyConstant && ((PossiblyConstant)f).isConstant() ) {
			if( f instanceof FunctionDaDaDa_Da ) {
				return FunctionUtil.getConstantFunction((FunctionDaDaDa_Da)f);
			}
			if( f instanceof FunctionDaDaDa_Ia ) {
				return FunctionUtil.getConstantFunction((FunctionDaDaDa_Ia)f);
			}
		}
		
		if( f instanceof Expression ) {
			return ((Expression)f).rewriteSubExpressions(this);
		}
		
		return f;
	}
}
