package togos.noise2.function;

import togos.noise2.data.DataDa;
import togos.noise2.data.DataDaDaDa;
import togos.noise2.rewrite.ExpressionRewriter;

public class LessThanOrEqualDaDaDa_Da extends ComparisonDaDaDa_Da
{
	public LessThanOrEqualDaDaDa_Da( FunctionDaDaDa_Da a, FunctionDaDaDa_Da b ) {
		super( a, b );
	}
	
	protected String getOperatorSymbol() {
		return "<=";
	}

	public DataDa apply( DataDaDaDa in ) {
		double[] oa = a.apply(in).x;
		double[] ob = b.apply(in).x;
		double[] res = new double[oa.length];
		for( int i=res.length-1; i>=0; --i ) {
			res[i] = oa[i] <= ob[i] ? 1 : 0; 
		}
		return new DataDa(res);
	}

	public Object rewriteSubExpressions( ExpressionRewriter rw ) {
		return new LessThanOrEqualDaDaDa_Da(
			(FunctionDaDaDa_Da)rw.rewrite(a),
			(FunctionDaDaDa_Da)rw.rewrite(b)
		);
	}
}
