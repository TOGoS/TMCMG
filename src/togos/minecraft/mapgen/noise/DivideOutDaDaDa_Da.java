package togos.minecraft.mapgen.noise;

import togos.minecraft.mapgen.noise.api.FunctionDaDaDa_Da;

public class DivideOutDaDaDa_Da extends ReduceOutDaDaDa_Da
{
	public DivideOutDaDaDa_Da( FunctionDaDaDa_Da[] components ) {
		super(components);
	}
	
	protected void reduce( int count, double[] subOut, double[] out ) {
		for( int j=0; j<count; ++j ) {
			out[j] /= subOut[j];
		}
	}
	
	protected String getOperatorSymbol() {
		return "/";
	}
}
