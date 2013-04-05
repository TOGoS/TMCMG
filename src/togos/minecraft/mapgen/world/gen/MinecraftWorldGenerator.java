package togos.minecraft.mapgen.world.gen;

public interface MinecraftWorldGenerator
{
	/** Used to color the overhead preview */
	public LayeredTerrainFunction getTerrainFunction();
	/** Used to build actual maps */
	public ChunkMunger getChunkMunger();
}
