package togos.noise2.function;


public class MaxOutDaDaDa_Da extends ReduceOutDaDaDa_Da
{
	public MaxOutDaDaDa_Da( SmartFunctionDaDaDa_Da[] components ) {
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
