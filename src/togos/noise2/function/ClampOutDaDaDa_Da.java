package togos.noise2.function;

import togos.noise2.data.DataDa;
import togos.noise2.data.DataDaDaDa;
import togos.noise2.rewrite.ExpressionRewriter;

public class ClampOutDaDaDa_Da extends TNLFunctionDaDaDa_Da
{
	TNLFunctionDaDaDa_Da lower;
	TNLFunctionDaDaDa_Da upper;
	TNLFunctionDaDaDa_Da clamped;
	
	public ClampOutDaDaDa_Da( TNLFunctionDaDaDa_Da lower, TNLFunctionDaDaDa_Da upper, TNLFunctionDaDaDa_Da clamped ) {
		this.lower = lower;
		this.upper = upper;
		this.clamped = clamped;
	}
	
	public DataDa apply( DataDaDaDa in ) {
		double[] lower = this.lower.apply(in).v;
		double[] upper = this.upper.apply(in).v;
		double[] clamped = this.clamped.apply(in).v;
		double[] out = new double[in.getLength()];
		for( int i=in.getLength()-1; i>=0; --i ) {
			if( clamped[i] < lower[i] ) out[i] = lower[i];
			else if( clamped[i] > upper[i] ) out[i] = upper[i];
			else out[i] = clamped[i];
		}
		return new DataDa(out);
	}
	
	public boolean isConstant() {
		return lower.isConstant() && upper.isConstant() && clamped.isConstant();
	}
	
	public Object rewriteSubExpressions(ExpressionRewriter rw) {
		return new ClampOutDaDaDa_Da(
			(TNLFunctionDaDaDa_Da)rw.rewrite(lower),
			(TNLFunctionDaDaDa_Da)rw.rewrite(upper),
			(TNLFunctionDaDaDa_Da)rw.rewrite(clamped)
		);
	}
	
	public String toTnl() {
		return "clamp("+lower+", "+upper+", "+clamped+")";
	}
}
