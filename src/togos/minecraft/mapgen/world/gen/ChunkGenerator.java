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
		
		// Crappy attempt at lighting
		for( int z=0; z<cd.depth; ++z ) for( int x=0; x<cd.width; ++x ) {
			int lightLevel = 15;
			int y;
			for( y=cd.height-1; y>=0 && lightLevel >=0; --y ) {
				switch( cd.getBlockId(x,y,z) ) {
				case 0:
					break;
				case 0x14: // Glass
				case 0x4F: // Ice
				case 0x09: // Water
				case 0x12: // Leaves
					--lightLevel;
					break;
				default:
					lightLevel = 0;
				}
				// It is important to set the light for the empty spaces, not for
				// the solid blocks bordering them.  Otherwise Minecraft gets confused
				// and underground lighting is all goofy and dumb looking. 
				cd.setSkyLight( x, y, z, lightLevel );
			}
			cd.setLightHeight( x, z, y );
		}
		
		return cd;
	}
}
