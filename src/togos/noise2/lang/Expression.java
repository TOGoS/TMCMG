package togos.noise2.lang;

import togos.noise2.rewrite.ExpressionRewriter;

public interface Expression
{
	/**
	 * Should call v.rewrite() on all sub-expressions
	 */
	public Object rewriteSubExpressions(ExpressionRewriter v); 
}
