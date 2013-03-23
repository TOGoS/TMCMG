package togos.noise.v1.func;

import togos.noise.v1.data.DataDa;
import togos.noise.v1.data.DataDaDaDa;
import togos.noise.v1.data.DataIa;
import togos.noise.v1.lang.Expression;
import togos.noise.v1.rewrite.ExpressionRewriter;

public class AdaptOutDaDaDa_Da_Ia implements FunctionDaDaDa_Ia, Expression
{
	FunctionDaDaDa_Da next;
	public AdaptOutDaDaDa_Da_Ia( FunctionDaDaDa_Da next ) {
		this.next = next;
	}
	
	public DataIa apply( final DataDaDaDa in ) {
		final int vectorSize = in.getLength();
		int[] out = new int[vectorSize];
		DataDa d = next.apply(in);
		for( int i=vectorSize-1; i>=0; --i ) {
			out[i] = (int)Math.floor(d.x[i]);
		}
		return new DataIa(vectorSize, out);
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
