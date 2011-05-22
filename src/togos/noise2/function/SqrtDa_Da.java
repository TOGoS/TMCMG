package togos.noise2.function;

import togos.noise2.data.DataDa;
import togos.noise2.data.DataDaDaDa;
import togos.noise2.lang.FunctionUtil;
import togos.noise2.rewrite.ExpressionRewriter;

public class SqrtDa_Da extends TNLFunctionDaDaDa_Da implements FunctionDaDaDa_Da {
	
	private FunctionDaDaDa_Da arg;
	
	public SqrtDa_Da( FunctionDaDaDa_Da arg) {
		this.arg = arg;
	}
	
	public DataDa apply( DataDaDaDa in) {
		int len = in.getLength();
		double[] dat = arg.apply(in).x;
		double[] out = new double[len];
		for (int i = 0; i < len; i++) {
			out[i] = dat[i] > 0 ? Math.sqrt(dat[i]) : 0;
		}
		return new DataDa( out );
	}
	
	public String toString() {
		return "sqrt(" + arg + ")";
	}

	public String toTnl() { 
		return "sqrt(" + FunctionUtil.toTnl(arg) + ")"; 
	}

	public boolean isConstant() { 
		return FunctionUtil.isConstant(arg); 
	}

	public Object[] directSubExpressions() {
		return new Object[]{ arg };
    }

	public Object rewriteSubExpressions( ExpressionRewriter rw ) {
	    return new SqrtDa_Da((FunctionDaDaDa_Da)rw.rewrite(arg));
    }

}
