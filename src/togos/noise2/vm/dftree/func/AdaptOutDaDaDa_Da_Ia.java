package togos.noise2.vm.dftree.func;

import togos.noise2.lang.Expression;
import togos.noise2.rewrite.ExpressionRewriter;
import togos.noise2.vm.dftree.data.DataDa;
import togos.noise2.vm.dftree.data.DataDaDaDa;
import togos.noise2.vm.dftree.data.DataIa;

public class AdaptOutDaDaDa_Da_Ia implements FunctionDaDaDa_Ia, Expression
{
	FunctionDaDaDa_Da next;
	public AdaptOutDaDaDa_Da_Ia( FunctionDaDaDa_Da next ) {
		this.next = next;
	}
	
	public DataIa apply( DataDaDaDa in ) {
		int[] out = new int[in.getLength()];
		DataDa d = next.apply(in);
		for( int i=d.getLength()-1; i>=0; --i ) {
			out[i] = (int)Math.floor(d.x[i]);
		}
		return new DataIa(out);
	}
	
	public Object[] directSubExpressions() {
		return new Object[]{ next };
	}
	
	public Object rewriteSubExpressions( ExpressionRewriter v ) {
		return new AdaptOutDaDaDa_Da_Ia( (FunctionDaDaDa_Da)v.rewrite(next) );
	}
	
	public String toTnl() {
		return ((Expression)next).toTnl();
	}
	
	public int getTriviality() {
	    return 0;
	}
}
