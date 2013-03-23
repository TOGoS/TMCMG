package togos.noise.v1.func;


public class MaxOutDaDaDa_Da extends ReduceOutDaDaDa_Da
{
	public MaxOutDaDaDa_Da( FunctionDaDaDa_Da[] components ) {
		super(components);
	}
	
	protected void reduce( int count, double[] subOut, double[] out ) {
		for( int j=0; j<count; ++j ) {
			out[j] = Math.max(out[j],subOut[j]);
		}
	}
	
	protected String getMacroName() {
		return "max";
	}
}
