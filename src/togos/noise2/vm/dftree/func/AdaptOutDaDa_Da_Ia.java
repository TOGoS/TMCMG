package togos.noise2.vm.dftree.func;

import togos.noise2.rewrite.ExpressionRewriter;
import togos.noise2.vm.dftree.data.DataDa;
import togos.noise2.vm.dftree.data.DataDaDa;
import togos.noise2.vm.dftree.data.DataIa;
import togos.noise2.vm.dftree.lang.Expression;

public class AdaptOutDaDa_Da_Ia implements FunctionDaDa_Ia, Expression
{
	FunctionDaDa_Da next;
	public AdaptOutDaDa_Da_Ia( FunctionDaDa_Da next ) {
		this.next = next;
	}
	
	public DataIa apply( DataDaDa in ) {
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
		return new AdaptOutDaDa_Da_Ia( (FunctionDaDa_Da)v.rewrite(next) );
	}
	
	public String toTnl() {
		return ((Expression)next).toTnl();
	}
	
	public int getTriviality() {
	    return 0;
	}
}
