package togos.noise.v1.func;


public class OrOutDaDaDa_Da extends ReduceOutDaDaDa_Da
{
	public OrOutDaDaDa_Da( FunctionDaDaDa_Da[] components ) {
		super(components);
	}
	
	protected void reduce( int count, double[] subOut, double[] out ) {
		for( int j=0; j<count; ++j ) {
			out[j] = (out[j] > 0 || subOut[j] > 0) ? 1 : 0;
		}
	}
	
	protected String getOperatorSymbol() {
		return "or";
	}
}
