/**
 * 
 */
package togos.minecraft.mapgen.world.gen;

import togos.minecraft.mapgen.world.Materials;
import togos.noise2.function.FunctionDaDa_DaIa;
import togos.noise2.function.FunctionDaDa_Ia;

public class GroundColorFunction implements FunctionDaDa_Ia {
	FunctionDaDa_DaIa groundFunction;
	public GroundColorFunction( FunctionDaDa_DaIa groundFunction ) {
		this.groundFunction = groundFunction;
	}
	
	public void apply( int count, double[] inX, double[] inY, int[] out ) {
		double[] height = new double[count];
		groundFunction.apply(count, inX, inY, height, out);
		for( int i=0; i<count; ++i ) {
			out[i] = Materials.getByBlockType(out[i]).color;
		}
	}
}