package togos.minecraft.mapgen.noise;

import togos.minecraft.mapgen.noise.api.FunctionDaDaDa_Da;

public abstract class ReduceOutDaDaDa_Da implements FunctionDaDaDa_Da
{
	FunctionDaDaDa_Da[] components;
	public ReduceOutDaDaDa_Da( FunctionDaDaDa_Da[] components ) {
		this.components = components;
	}
	
	protected abstract void reduce( int count, double[] subOut, double[] out );
	
	public void apply( int count, double[] inX, double[] inY, double[] inZ, double[] out ) {
		components[0].apply(count, inX, inY, inZ, out);
		double[] subOut = new double[count];
		for( int i=1; i<components.length; ++i ) {
			components[i].apply(count, inX, inY, inZ, subOut);
			reduce( count, subOut, out );
		}
	}
}
