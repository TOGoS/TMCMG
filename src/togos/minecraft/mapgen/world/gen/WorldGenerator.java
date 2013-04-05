package togos.minecraft.mapgen.world.gen;

import java.util.Map;

import togos.minecraft.mapgen.LFunctionDaDa_Da_Ia;
import togos.noise.v1.func.LFunctionDaDa_DaIa;

public interface WorldGenerator
{
	/** Used to color the overhead preview */
	public LFunctionDaDa_DaIa getGroundFunction();
	public LFunctionDaDa_Da_Ia getColumnFunction();
	public ChunkMunger getChunkMunger();
	
	/**
	 * Returns a set of named 'extra components', which can be anything.
	 * Some standard ones:
	 *   "temperature" - a FunctionDaDa_Da (or possibly DaDaDa_Da, etc)
	 *     that returns the temperature at every X,Z point on the map
	 *   "humidity" - see above, but for humidity
	 */
	public Map<String,Object> getComponents();
}
