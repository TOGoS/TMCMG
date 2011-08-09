package togos.noise2.vm.dftree.func;

import togos.noise2.rewrite.ExpressionRewriter;
import togos.noise2.vm.dftree.data.DataDa;
import togos.noise2.vm.dftree.data.DataDaDa;
import togos.noise2.vm.dftree.data.DataDaDaDa;
import togos.noise2.vm.dftree.lang.Expression;

/**
 * So called because the outer data's X and Y are mapped to the inner data's X and Z 
 */
public class AdaptInXZDaDaDa_DaDa_Da implements FunctionDaDa_Da, Expression
{
	static Constant_Da Y = new Constant_Da(0); 
	
	FunctionDaDaDa_Da next;
	
	public AdaptInXZDaDaDa_DaDa_Da( FunctionDaDaDa_Da next ) {
		this.next = next;
	}

	public DataDa apply( DataDaDa in ) {
		DataDa y = Y.apply(in);
		return next.apply(new DataDaDaDa(in.getLength(),in.x, y.x, in.y, "adaptX0Y:"+in.getDataId()));
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
