package togos.noise2.vm.dftree.func;

import togos.noise2.rewrite.ExpressionRewriter;
import togos.noise2.vm.dftree.data.DataDa;
import togos.noise2.vm.dftree.data.DataDaDaDa;

public class EqualDaDaDa_Da extends ComparisonDaDaDa_Da
{
	public EqualDaDaDa_Da( FunctionDaDaDa_Da a, FunctionDaDaDa_Da b ) {
		super( a, b );
	}
	
	protected String getOperatorSymbol() {
		return "==";
	}

	public DataDa apply( DataDaDaDa in ) {
		final int len = in.getLength();
		double[] oa = a.apply(in).x;
		double[] ob = b.apply(in).x;
		double[] res = new double[len];
		for( int i=res.length-1; i>=0; --i ) {
			res[i] = oa[i] == ob[i] ? 1 : 0; 
		}
		return new DataDa(len,res);
	}

	public Object rewriteSubExpressions( ExpressionRewriter rw ) {
		return new EqualDaDaDa_Da(
			(FunctionDaDaDa_Da)rw.rewrite(a),
			(FunctionDaDaDa_Da)rw.rewrite(b)
		);
	}
}
