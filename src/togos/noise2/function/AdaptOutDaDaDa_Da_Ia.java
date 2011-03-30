package togos.noise2.function;

import togos.noise2.data.DataDa;
import togos.noise2.data.DataDaDaDa;
import togos.noise2.data.DataIa;
import togos.noise2.lang.Expression;
import togos.noise2.rewrite.ExpressionRewriter;

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
			out[i] = (int)Math.floor(d.v[i]);
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
