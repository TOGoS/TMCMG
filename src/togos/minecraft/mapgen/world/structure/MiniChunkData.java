package togos.minecraft.mapgen.world.structure;

import java.util.ArrayList;
import java.util.List;

import togos.minecraft.mapgen.world.ChunkUtil;
import togos.minecraft.mapgen.world.Materials;

public class MiniChunkData
{
	/**
	 * X,Y,Z coordinates (in blocks a.k.a. world units a.k.a. meters)
	 * of the bottom northeast corner of the chunk within the world.
	 */
	public final long posX, posY, posZ;
	public final int width, height, depth;
	
	public short[] blockIds;
	public byte[] blockData;
	public List<TileEntityData> tileEntityData = new ArrayList<TileEntityData>();
	
	public MiniChunkData( long px, long py, long pz, int width, int height, int depth ) {
		this.posX = px;
		this.posY = py;
		this.posZ = pz;
		this.width = width;
		this.height = height;
		this.depth = depth;
		this.blockIds = new short[height*depth*width];
		this.blockData = new byte[(height*depth*width+1)/2];
	}
	
	/*
	 * Return the 
	 * X,Y,Z coordinates (in blocks a.k.a. world units a.k.a. meters)
	 * of the bottom northeast corner of the chunk within the world.
	 */
	public long getChunkPositionX() { return posX; }
	public long getChunkPositionY() { return posY; }
	public long getChunkPositionZ() { return posZ; }
	
	public int getChunkWidth() {  return width;  }
	public int getChunkHeight() { return height; }
	public int getChunkDepth() {  return depth;  }
	
	protected int blockIndex( int x, int y, int z ) {
		return y + z*height + x*depth*height;
	}
	
	//// Block ////
	
	public short getBlockId( int x, int y, int z ) {
		return blockIds[ blockIndex(x,y,z) ];
	}
	
	public void setBlockId( int x, int y, int z, short blockNum ) {
		blockIds[ blockIndex(x,y,z) ] = blockNum;
	}
	
	public byte getBlockData( int x, int y, int z ) {
		return ChunkUtil.getNybble( blockData, blockIndex(x,y,z) );
	}
	
	public void setBlockData( int x, int y, int z, byte value ) {
		ChunkUtil.putNybble( blockData, blockIndex(x,y,z), value );
	}
	
	public void setBlock( int x, int y, int z, short blockId, byte blockData ) {
		setBlockId( x, y, z, blockId );
		setBlockData( x, y, z, blockData );
	}
	
	public void setBlock( int x, int y, int z, int material ) {
		setBlock( x, y, z,
			(short)((material >> Materials.BLOCK_ID_SHIFT) & Materials.BLOCK_ID_MASK),
			(byte)((material >> Materials.BLOCK_DATA_SHIFT) & Materials.BLOCK_DATA_MASK)
		);
	}
}
