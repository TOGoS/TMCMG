package togos.noise2.function;

import togos.noise2.lang.FunctionUtil;

public class RidgeOutDaDaDa_Da implements SmartFunctionDaDaDa_Da
{
	SmartFunctionDaDaDa_Da lower;
	SmartFunctionDaDaDa_Da upper;
	SmartFunctionDaDaDa_Da ridged;
	
	public RidgeOutDaDaDa_Da( SmartFunctionDaDaDa_Da lower, SmartFunctionDaDaDa_Da upper, SmartFunctionDaDaDa_Da ridged ) {
		this.lower = lower;
		this.upper = upper;
		this.ridged = ridged;
	}
	
	public void apply(int count, double[] inX, double[] inY, double[] inZ, double[] out) {
		double[] lower = new double[count];
		this.lower.apply(count, inX, inY, inZ, lower);
		double[] upper = new double[count];
		this.upper.apply(count, inX, inY, inZ, upper);
		this.ridged.apply(count, inX, inY, inZ, out);
		for( int i=0; i<count; ++i ) {
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
	
	public SmartFunctionDaDaDa_Da simplify() {
		RidgeOutDaDaDa_Da simplified;
		try {
			simplified = (RidgeOutDaDaDa_Da)clone();
        } catch( CloneNotSupportedException e ) {
        	throw new RuntimeException(e);
        }
        simplified.lower = lower.simplify();
        simplified.upper = upper.simplify();
        simplified.ridged = ridged.simplify();
		return FunctionUtil.collapseIfConstant(simplified);
	}
}
