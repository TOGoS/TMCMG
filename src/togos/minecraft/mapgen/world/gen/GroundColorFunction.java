/**
 * 
 */
package togos.minecraft.mapgen.world.gen;

import togos.minecraft.mapgen.world.Materials;
import togos.noise2.data.DataDaDa;
import togos.noise2.data.DataDaIa;
import togos.noise2.data.DataIa;
import togos.noise2.function.FunctionDaDa_DaIa;
import togos.noise2.function.FunctionDaDa_Ia;

public class GroundColorFunction implements FunctionDaDa_Ia {
	FunctionDaDa_DaIa groundFunction;
	public GroundColorFunction( FunctionDaDa_DaIa groundFunction ) {
		this.groundFunction = groundFunction;
	}
	
	public DataIa apply( DataDaDa in ) {
		DataDaIa ground = groundFunction.apply(in);
		int[] type = ground.i;
		int[] color = new int[type.length];
		for( int i=0; i<color.length; ++i ) {
			color[i] = Materials.getByBlockType(type[i]).color;
		}
		return new DataIa(color);
	}
}