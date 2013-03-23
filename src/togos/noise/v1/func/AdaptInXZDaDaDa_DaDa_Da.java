package togos.noise.v1.func;

import togos.noise.v1.data.DataDa;
import togos.noise.v1.data.DataDaDa;
import togos.noise.v1.data.DataDaDaDa;
import togos.noise.v1.lang.Expression;
import togos.noise.v1.rewrite.ExpressionRewriter;

/**
 * So called because the outer data's X and Y are mapped to the inner data's X and Z
 * This is deprecated because it depends on x, y, and z having strange scope.
 * Scripts that depend on it should be rewritten to pass them explicitly. 
 */
@Deprecated
public class AdaptInXZDaDaDa_DaDa_Da implements FunctionDaDa_Da, Expression
{
	static Constant_Da Y = new Constant_Da(0); 
	
	FunctionDaDaDa_Da next;
	
	public AdaptInXZDaDaDa_DaDa_Da( FunctionDaDaDa_Da next ) {
		this.next = next;
	}

	public DataDa apply( DataDaDa in ) {
		DataDa y = Y.apply(in);
		return next.apply(new DataDaDaDa(in.getLength(),in.x, y.x, in.y));
	}
	
	public Object rewriteSubExpressions( ExpressionRewriter v ) {
	    return new AdaptInXZDaDaDa_DaDa_Da( (FunctionDaDaDa_Da)v.rewrite(next) );
	}
	
	public String toString() {
		return "adapt-in-dada-dadada-da("+next.toString()+")";
	}
	
	public String toTnl() {
	    return ((Expression)next).toTnl();
	}

	public Object[] directSubExpressions() {
		if( next instanceof Expression ) {
			return new Expression[]{ (Expression)next };
		} else {
			return new Expression[0];
		}
    }
	
	public int getTriviality() {
	    return 0;
	}
}
