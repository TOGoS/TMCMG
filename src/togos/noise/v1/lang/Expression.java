package togos.noise.v1.lang;

import togos.noise.v1.rewrite.ExpressionRewriter;

public interface Expression
{
	/**
	 * Should call v.rewrite() on all sub-expressions, using the
	 * return value to replace the sub-expressions in what is
	 * otherwise a copy of this object.
	 */
	public Object rewriteSubExpressions(ExpressionRewriter v);
	
	/**
	 * Return a list of all sub-expressions
	 * that will be called with the *same inputs* as this expression.
	 * (they might not necessarily implement Expression).
	 */
	public abstract Object[] directSubExpressions();
	
	/**
	 * A string that is
	 * - valid TNL and 
	 * - is semantically equivalent to this expression.
	 *   - parts of the expression that make no difference samantically,
	 *     such as cache(), can and should be left out. 
	 */
	public String toTnl();
	
	/**
	 * 0   = not trivial
	 * 100 = need to allocate an array, but populating it is dead simple (e.g. a constant)
	 * 200 = returns existing object, possibly with wrapper
	 * 300 = returns something very simple, no need for new object
	 */
	public int getTriviality();
}
