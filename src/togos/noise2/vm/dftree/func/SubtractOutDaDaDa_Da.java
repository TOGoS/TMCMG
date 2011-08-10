package togos.noise2.vm.dftree.func;


public class SubtractOutDaDaDa_Da extends ReduceOutDaDaDa_Da
{
	public SubtractOutDaDaDa_Da( FunctionDaDaDa_Da[] components ) {
		super(components);
	}
	
	protected void reduce( int count, double[] subOut, double[] out ) {
		for( int j=count-1; j>=0; --j ) {
			out[j] -= subOut[j];
		}
	}
	
	protected String getOperatorSymbol() {
		return "-";
	}
}
