package togos.minecraft.mapgen.world.gen;

import togos.minecraft.mapgen.world.ChunkUtil;
import togos.minecraft.mapgen.world.structure.ChunkData;

public class Lighter implements ChunkMunger
{
	public void mungeChunk( ChunkData cd ) {
		ChunkUtil.calculateLighting(cd, 15);
	}
}
