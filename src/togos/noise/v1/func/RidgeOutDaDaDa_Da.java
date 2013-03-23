package togos.noise.v1.func;

import togos.noise.v1.data.DataDa;
import togos.noise.v1.data.DataDaDaDa;
import togos.noise.v1.lang.FunctionUtil;
import togos.noise.v1.rewrite.ExpressionRewriter;

public class RidgeOutDaDaDa_Da extends TNLFunctionDaDaDa_Da
{
	FunctionDaDaDa_Da lower;
	FunctionDaDaDa_Da upper;
	FunctionDaDaDa_Da ridged;
	
	public RidgeOutDaDaDa_Da( FunctionDaDaDa_Da lower, FunctionDaDaDa_Da upper, FunctionDaDaDa_Da ridged ) {
		this.lower = lower;
		this.upper = upper;
		this.ridged = ridged;
	}
	
	private static final long fastfloor(double n) {
		return n > 0 ? (long) n : (long) n - 1;
	}
	
	public DataDa apply( DataDaDaDa in ) {
		final int vectorSize = in.getLength();
		double[] lower = this.lower.apply(in).x;
		double[] upper = this.upper.apply(in).x;
		double[] input = this.ridged.apply(in).x;
		double[] out = new double[vectorSize];
		for( int i=vectorSize-1; i>=0; --i ) {
			if( upper[i] > lower[i] ) {
				double d = upper[i]-lower[i];
				double k = (input[i]-lower[i])/(d*2);
				double c = fastfloor(k);
				k -= c;
				out[i] = lower[i] + d*2*(k - 2*fastfloor(2*k)*(k-0.5));
			} else {
				out[i] = (lower[i] + upper[i]) / 2;
			}
		}
		return new DataDa(vectorSize,out);
	}
	
	public boolean isConstant() {
		return FunctionUtil.isConstant(lower) && FunctionUtil.isConstant(upper) && FunctionUtil.isConstant(ridged);
	}
	
	public Object rewriteSubExpressions(ExpressionRewriter rw) {
		return new RidgeOutDaDaDa_Da(
			(TNLFunctionDaDaDa_Da)rw.rewrite(lower),
			(TNLFunctionDaDaDa_Da)rw.rewrite(upper),
			(TNLFunctionDaDaDa_Da)rw.rewrite(ridged)
		);
	}
	
	public Object[] directSubExpressions() {
		return new Object[]{ upper, lower, ridged };
	}
	
	public String toString() {
		return "ridge("+lower+", "+upper+", "+ridged+")";
	}
	
	public String toTnl() {
		return "ridge("+FunctionUtil.toTnl(lower)+", "+FunctionUtil.toTnl(upper)+", "+FunctionUtil.toTnl(ridged)+")";
	}
}
