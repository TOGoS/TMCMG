package togos.noise2.function;


public class MinOutDaDaDa_Da extends ReduceOutDaDaDa_Da
{
	public MinOutDaDaDa_Da( SmartFunctionDaDaDa_Da[] components ) {
		super(components);
	}
	
	protected void reduce( int count, double[] subOut, double[] out ) {
		for( int j=0; j<count; ++j ) {
			out[j] = Math.min(out[j],subOut[j]);
		}
	}
	
	protected String getMacroName() {
		return "min";
	}
}
