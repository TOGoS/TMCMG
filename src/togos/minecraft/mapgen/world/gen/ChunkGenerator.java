package togos.minecraft.mapgen.world.gen;

import togos.minecraft.mapgen.world.structure.ChunkData;

public class ChunkGenerator
{
	public int chunkWidth  = ChunkData.NORMAL_CHUNK_WIDTH;
	public int chunkHeight = ChunkData.NORMAL_CHUNK_HEIGHT;
	public int chunkDepth  = ChunkData.NORMAL_CHUNK_DEPTH;
	
	public ChunkMunger cm;
	
	public ChunkGenerator( ChunkMunger cm ) {
		this.cm = cm;
	}
	
	/**
	 * @param cx number of chunks south of origin
	 * @param cz number of chunks west of origin
	 * @return new chunkdata object with coordinate stuff filled out
	 */
	public ChunkData createBlankChunk( int cx, int cz ) {
		return new ChunkData(
			cx*chunkWidth, 0, cz*chunkDepth,
			chunkWidth, chunkHeight, chunkDepth
		);
	}
	
	/**
	 * @param cx number of chunks south of origin
	 * @param cz number of chunks west of origin
	 * @return new chunkdata object munged by cm
	 */
	public ChunkData generateChunk( int cx, int cz ) {
		ChunkData cd = createBlankChunk( cx, cz );
		cm.mungeChunk(cd);
		return cd;
	}
}
