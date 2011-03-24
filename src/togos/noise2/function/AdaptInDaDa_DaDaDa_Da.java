package togos.noise2.function;

import togos.noise2.data.DataDa;
import togos.noise2.data.DataDaDa;
import togos.noise2.data.DataDaDaDa;
import togos.noise2.lang.Expression;
import togos.noise2.rewrite.ExpressionRewriter;

public class AdaptInDaDa_DaDaDa_Da implements FunctionDaDa_Da, Expression
{
	static Constant_Da Z = new Constant_Da(0); 
	
	FunctionDaDaDa_Da next;
	
	public AdaptInDaDa_DaDaDa_Da( FunctionDaDaDa_Da next ) {
		this.next = next;
	}

	public DataDa apply( DataDaDa in ) {
		DataDa z = Z.apply(in);
		return next.apply(new DataDaDaDa(in.x, in.y, z.v, in.getUrn()));
	}
	
	public Object rewriteSubExpressions( ExpressionRewriter v ) {
	    return new AdaptInDaDa_DaDaDa_Da( (FunctionDaDaDa_Da)v.rewrite(next) );
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
}
