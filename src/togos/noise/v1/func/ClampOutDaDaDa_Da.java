package togos.noise.v1.func;

import togos.noise.v1.data.DataDa;
import togos.noise.v1.data.DataDaDaDa;
import togos.noise.v1.lang.FunctionUtil;
import togos.noise.v1.rewrite.ExpressionRewriter;

public class ClampOutDaDaDa_Da extends TNLFunctionDaDaDa_Da
{
	FunctionDaDaDa_Da lower;
	FunctionDaDaDa_Da upper;
	FunctionDaDaDa_Da clamped;
	
	public ClampOutDaDaDa_Da( FunctionDaDaDa_Da lower, FunctionDaDaDa_Da upper, FunctionDaDaDa_Da clamped ) {
		this.lower = lower;
		this.upper = upper;
		this.clamped = clamped;
	}
	
	public DataDa apply( DataDaDaDa in ) {
		final int vectorSize = in.getLength();
		double[] lower = this.lower.apply(in).x;
		double[] upper = this.upper.apply(in).x;
		double[] input = this.clamped.apply(in).x;
		double[] out = new double[vectorSize];
		for( int i=vectorSize-1; i>=0; --i ) {
			out[i] = (input[i] < upper[i]) ? (input[i] > lower[i]) ? input[i] : lower[i] : upper[i];
		}
		return new DataDa(vectorSize,out);
	}
	
	public boolean isConstant() {
		return FunctionUtil.isConstant(lower) && FunctionUtil.isConstant(upper) && FunctionUtil.isConstant(clamped);
	}
	
	public Object rewriteSubExpressions(ExpressionRewriter rw) {
		return new ClampOutDaDaDa_Da(
			(TNLFunctionDaDaDa_Da)rw.rewrite(lower),
			(TNLFunctionDaDaDa_Da)rw.rewrite(upper),
			(TNLFunctionDaDaDa_Da)rw.rewrite(clamped)
		);
	}
	
	public String toString() {
		return "clamp("+lower+", "+upper+", "+clamped+")";
	}
	
	public String toTnl() {
		return "clamp("+FunctionUtil.toTnl(lower)+", "+FunctionUtil.toTnl(upper)+", "+FunctionUtil.toTnl(clamped)+")";
	}
	
	public Object[] directSubExpressions() {
		return new Object[]{ lower, upper, clamped };
	}
}
