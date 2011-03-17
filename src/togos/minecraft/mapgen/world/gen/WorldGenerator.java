package togos.minecraft.mapgen.world.gen;

import java.util.Map;

import togos.noise2.function.FunctionDaDa_DaIa;

public interface WorldGenerator
{
	/** Used to color the overhead preview */
	public FunctionDaDa_DaIa getGroundFunction();
	public ChunkMunger getChunkMunger();
	
	/**
	 * Returns a set of named 'extra components', which can be anything.
	 * Some standard ones:
	 *   "temperature" - a FunctionDaDa_Da (or possibly DaDaDa_Da, etc)
	 *     that returns the temperature at every X,Z point on the map
	 *   "humidity" - see above, but for humidity
	 */
	public Map getComponents();
}
