package togos.minecraft.mapgen.util;

import togos.mf.value.ByteChunk;
import togos.minecraft.mapgen.io.RegionFile;
import togos.minecraft.mapgen.world.structure.ChunkData;

public class SubRegion
{
	public final long x, y, z;
	public final short chunksWide, chunksHigh, chunksDeep;
	public final short chunkWidth, chunkHeight, chunkDepth;
	public final byte chunkFormat;
	public final ByteChunk[] chunkData;
	public final int[] chunkMtime;
	
	public SubRegion( long x, long y, long z, short chunksWide, short chunksHigh, short chunksDeep ) {
		this( x, y, z, chunksWide, chunksHigh, chunksDeep,
			(byte)ChunkData.NORMAL_CHUNK_WIDTH,
			(byte)ChunkData.NORMAL_CHUNK_HEIGHT,
			(byte)ChunkData.NORMAL_CHUNK_DEPTH);
	}
	
	public SubRegion( long x, long y, long z, short chunksWide, short chunksHigh, short chunksDeep, short chunkWidth, short chunkHeight, short chunkDepth ) {
		this.x = x; this.y = y; this.z = z;
		this.chunksWide = chunksWide;
		this.chunksHigh = chunksHigh;
		this.chunksDeep = chunksDeep;
		this.chunkWidth  = chunkWidth;
		this.chunkHeight = chunkHeight;
		this.chunkDepth  = chunkDepth;
		this.chunkFormat = RegionFile.VERSION_DEFLATE;
		int nChunks = chunksWide*chunksHigh*chunksDeep;
		this.chunkData = new ByteChunk[nChunks];
		this.chunkMtime = new int[nChunks];
	}
	
	public boolean equals( Object o ) {
		if( o == this ) return true;
		if( !(o instanceof SubRegion) ) return false;
		
		SubRegion osr = (SubRegion)o;
		if( x != osr.x || y != osr.y || z != osr.z ||
		    chunksWide != osr.chunksWide || chunksHigh != osr.chunksHigh ||
		    chunksDeep != osr.chunksDeep || chunkWidth != osr.chunkWidth ||
		    chunkHeight != osr.chunkHeight || chunkDepth != osr.chunkDepth ) {
			return false;
		}
		
		for( int i=0; i<chunkData.length; ++i ) {
			if( !chunkData[i].equals(osr.chunkData[i]) ) return false;
			if( chunkMtime[i] != osr.chunkMtime[i] ) return false;
		}
		
		return true;
	}
	
	public String toString() {
		return "SubRegion("+x+","+y+","+z+";"+chunksWide+","+chunksHigh+","+chunksDeep+";"+chunkWidth+","+chunkHeight+","+chunkDepth+")";
	}
}
