package togos.noise2.function;

import togos.noise2.data.DataDa;
import togos.noise2.data.DataDaDa;
import togos.noise2.data.DataIa;
import togos.noise2.lang.Expression;
import togos.noise2.rewrite.ExpressionRewriter;

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
