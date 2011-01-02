package togos.noise2.function;

import togos.noise2.InputDaDaDa;
import togos.noise2.rewrite.ExpressionRewriter;

public class RidgeOutDaDaDa_Da extends SmartFunctionDaDaDa_Da
{
	SmartFunctionDaDaDa_Da lower;
	SmartFunctionDaDaDa_Da upper;
	SmartFunctionDaDaDa_Da ridged;
	
	public RidgeOutDaDaDa_Da( SmartFunctionDaDaDa_Da lower, SmartFunctionDaDaDa_Da upper, SmartFunctionDaDaDa_Da ridged ) {
		this.lower = lower;
		this.upper = upper;
		this.ridged = ridged;
	}
	
	public void apply( InputDaDaDa in, double[] out ) {
		double[] lower = new double[in.count];
		this.lower.apply(in, lower);
		double[] upper = new double[in.count];
		this.upper.apply(in, upper);
		this.ridged.apply(in, out);
		for( int i=in.count-1; i>=0; --i ) {
			double d = upper[i]-lower[i];

			// TODO: I'm guessing there's a better way to do this
			if( d == 0 ) {
				out[i] = lower[i];
			} else {
				/*
				// coorect but presumably slow:
				while( out[i] > upper[i] || out[i] < lower[i] ) {
					if( out[i] > upper[i] ) {
						out[i] = upper[i]-(out[i]-upper[i]);
					}
					if( out[i] < lower[i] ) {
						out[i] = lower[i]+(lower[i]-out[i]);
					}
				}
				*/
				
				double k = (out[i]-lower[i])/(d*2);
				double c = Math.floor(k);
				k -= c;
				out[i] = lower[i] + d*2*(k - 2*Math.floor(2*k)*(k-0.5));
			}
		}
	}
	
	public boolean isConstant() {
		return lower.isConstant() && upper.isConstant() && ridged.isConstant();
	}
	
	public Object rewriteSubExpressions(ExpressionRewriter rw) {
		return new RidgeOutDaDaDa_Da(
			(SmartFunctionDaDaDa_Da)rw.rewrite(lower),
			(SmartFunctionDaDaDa_Da)rw.rewrite(upper),
			(SmartFunctionDaDaDa_Da)rw.rewrite(ridged)
		);
	}
	
	public String toString() {
		return "ridge("+lower+", "+upper+", "+ridged+")";
	}
}
