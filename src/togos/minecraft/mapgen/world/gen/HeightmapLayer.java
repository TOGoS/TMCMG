package togos.minecraft.mapgen.world.gen;

import togos.noise.v1.func.FunctionDaDaDa_Ia;
import togos.noise.v1.func.FunctionDaDa_Da;
import togos.noise.v1.func.LFunctionDaDaDa_Ia;
import togos.noise.v1.func.LFunctionDaDa_Da;
import togos.noise.v1.lang.FunctionUtil;

public class HeightmapLayer
{
	public final FunctionDaDaDa_Ia typeFunction;
	public final FunctionDaDa_Da floorHeightFunction;
	public final FunctionDaDa_Da ceilingHeightFunction;

	public final LFunctionDaDaDa_Ia lTypeFunction;
	public final LFunctionDaDa_Da lFloorHeightFunction;
	public final LFunctionDaDa_Da lCeilingHeightFunction;
	
	public HeightmapLayer(
		FunctionDaDaDa_Ia typeFunction,
		FunctionDaDa_Da floorHeightFunction,
		FunctionDaDa_Da ceilingHeightFunction
	) {
		this.typeFunction = typeFunction;
		this.floorHeightFunction = floorHeightFunction;
		this.ceilingHeightFunction = ceilingHeightFunction;
		
		this.lTypeFunction = FunctionUtil.toLDaDaDa_Ia(typeFunction);
		this.lFloorHeightFunction = FunctionUtil.toLDaDa_Da(floorHeightFunction);
		this.lCeilingHeightFunction = FunctionUtil.toLDaDa_Da(ceilingHeightFunction);
	}
}
