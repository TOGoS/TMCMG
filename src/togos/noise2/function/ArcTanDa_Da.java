package togos.noise2.function;

import togos.noise2.data.DataDa;
import togos.noise2.data.DataDaDaDa;
import togos.noise2.rewrite.ExpressionRewriter;

public class ArcTanDa_Da extends TNLFunctionDaDaDa_Da implements FunctionDa_Da
{
	public static ArcTanDa_Da instance = new ArcTanDa_Da();
	
	public DataDa apply( DataDa in ) {
		int len = in.getLength();
		double[] dat = new double[len];
		for( int i=0; i<len; ++i ) {
			dat[i] = Math.atan(in.x[i]);
		}
		return new DataDa( dat );
	}
	
	public DataDa apply( DataDaDaDa in ) {
		return apply((DataDa)in);
	}

	public Object[] directSubExpressions() {
		return new Object[]{};
    }

	public Object rewriteSubExpressions( ExpressionRewriter v ) {
	    return this;
    }

	public String toTnl() {
	    return "arctan";
    }

	public boolean isConstant() {
	    return false;
    }
}
