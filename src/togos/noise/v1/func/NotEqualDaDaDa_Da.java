package togos.noise.v1.func;

import togos.noise.v1.data.DataDa;
import togos.noise.v1.data.DataDaDaDa;
import togos.noise.v1.rewrite.ExpressionRewriter;

public class NotEqualDaDaDa_Da extends ComparisonDaDaDa_Da
{
	public NotEqualDaDaDa_Da( FunctionDaDaDa_Da a, FunctionDaDaDa_Da b ) {
		super( a, b );
	}
	
	protected String getOperatorSymbol() {
		return "!=";
	}

	public DataDa apply( DataDaDaDa in ) {
		final int vectorSize = in.getLength();
		double[] oa = a.apply(in).x;
		double[] ob = b.apply(in).x;
		double[] res = new double[vectorSize];
		for( int i=vectorSize-1; i>=0; --i ) {
			res[i] = oa[i] != ob[i] ? 1 : 0; 
		}
		return new DataDa(vectorSize,res);
	}

	public Object rewriteSubExpressions( ExpressionRewriter rw ) {
		return new NotEqualDaDaDa_Da(
			(FunctionDaDaDa_Da)rw.rewrite(a),
			(FunctionDaDaDa_Da)rw.rewrite(b)
		);
	}
}
