package togos.minecraft.mapgen.world.gen;

import togos.noise2.function.FunctionDaDa_DaIa;

public interface WorldGenerator
{
	/** Used to color in overhead preview */
	public FunctionDaDa_DaIa getGroundFunction();
	public ChunkMunger getChunkMunger();
}
