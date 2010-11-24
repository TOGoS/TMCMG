package togos.minecraft.mapgen.world.gen;

import togos.minecraft.mapgen.world.structure.ChunkData;

public interface ChunkFunction
{
	public ChunkData getChunk( int x, int z );
}
