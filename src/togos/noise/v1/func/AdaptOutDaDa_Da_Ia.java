package togos.noise.v1.func;

import togos.noise.v1.data.DataDa;
import togos.noise.v1.data.DataDaDa;
import togos.noise.v1.data.DataIa;
import togos.noise.v1.lang.Expression;
import togos.noise.v1.rewrite.ExpressionRewriter;

public class AdaptOutDaDa_Da_Ia implements FunctionDaDa_Ia, Expression
{
	FunctionDaDa_Da next;
	public AdaptOutDaDa_Da_Ia( FunctionDaDa_Da next ) {
		this.next = next;
	}
	
	public DataIa apply( final DataDaDa in ) {
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
		return new AdaptOutDaDa_Da_Ia( (FunctionDaDa_Da)v.rewrite(next) );
	}
	
	public String toTnl() {
		return ((Expression)next).toTnl();
	}
	
	public int getTriviality() {
	    return 0;
	}
}
