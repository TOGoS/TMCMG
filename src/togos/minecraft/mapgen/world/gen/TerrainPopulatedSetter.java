package togos.minecraft.mapgen.world.gen;

import togos.minecraft.mapgen.world.structure.ChunkData;

public class TerrainPopulatedSetter implements ChunkMunger
{
	public static final TerrainPopulatedSetter instance = new TerrainPopulatedSetter();
	
	private TerrainPopulatedSetter() { };

	public void mungeChunk( ChunkData cd ) {
		cd.terrainPopulated = true;
	}
}
