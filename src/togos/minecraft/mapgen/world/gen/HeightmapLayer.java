package togos.minecraft.mapgen.world.gen;

import togos.noise.v1.func.FunctionDaDaDa_Ia;
import togos.noise.v1.func.FunctionDaDa_Da;

public class HeightmapLayer
{
	public FunctionDaDaDa_Ia typeFunction;
	public FunctionDaDa_Da floorHeightFunction;
	public FunctionDaDa_Da ceilingHeightFunction;
	
	public HeightmapLayer(
		FunctionDaDaDa_Ia typeFunction,
		FunctionDaDa_Da floorHeightFunction,
		FunctionDaDa_Da ceilingHeightFunction
	) {
		this.typeFunction = typeFunction;
		this.floorHeightFunction = floorHeightFunction;
		this.ceilingHeightFunction = ceilingHeightFunction;
	}
}