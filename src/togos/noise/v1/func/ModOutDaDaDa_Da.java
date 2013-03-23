package togos.noise.v1.func;

/**
 * Calculates 'floored division' remainders.  i.e.:
 * 
 *    13 %  10 =  3 
 *   -13 %  10 =  7
 *    13 % -10 = -7
 *   -13 % -10 = -3
 */
public class ModOutDaDaDa_Da extends ReduceOutDaDaDa_Da
{
	public ModOutDaDaDa_Da( FunctionDaDaDa_Da[] components ) {
		super(components);
	}
	
    private static final long fastFloor(double n) {
    	if( n < 0 ) {
    		long add = (long)(n-1);
    		return (long)(n-add)+add;
    	} else {
    		return (long)n;
    	}
    }
	
	protected void reduce( int count, double[] subOut, double[] out ) {
		for( int j=count-1; j>=0; --j ) {
			double d = fastFloor(out[j] / subOut[j]);
			out[j] = out[j] - d*subOut[j];
		}
	}
	
	protected String getOperatorSymbol() {
		return "%";
	}
}
