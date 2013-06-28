package togos.minecraft.mapgen.world.structure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jnbt.ByteArrayTag;
import org.jnbt.ByteTag;
import org.jnbt.CompoundTag;
import org.jnbt.IntArrayTag;
import org.jnbt.IntTag;
import org.jnbt.ListTag;
import org.jnbt.LongTag;
import org.jnbt.Tag;

import togos.minecraft.mapgen.TagMap;
import togos.minecraft.mapgen.world.ChunkUtil;

public class ChunkData extends MiniChunkData
{
	public static final int NORMAL_CHUNK_WIDTH  =  16;
	public static final int NORMAL_CHUNK_HEIGHT = 256;
	public static final int NORMAL_CHUNK_DEPTH  =  16;
	
	public ChunkData( long px, long py, long pz, int w, int h, int d ) {
		super(px,py,pz,w,h,d);
		for( int i=0; i<width*depth; ++i ) biomeData[i] = -1;
	}
	
	/*
	public final int height = 128; // Y/+up/-down
	public final int depth  =  16; // Z/+west/-east
	public final int width  =  16; // X/+south/-north
	
	            (N, -x)
	               |
	               |
	               |
	               |
	(W, +z)--------0---------(E, -z) 
	               |
	               |
	               |
   	               |
	            (S, +x)
	*/
	
	public byte[] skyLightData   = new byte[(height*depth*width+1)/2];
	public byte[] blockLightData = new byte[(height*depth*width+1)/2];
	public int[] lightHeightData = new int[depth*width];
	public byte[] biomeData      = new byte[depth*width];
	public boolean terrainPopulated = false;
	
	//// light ////
	
	public byte getSkyLight( int x, int y, int z ) {
		return ChunkUtil.getNybble(skyLightData, blockIndex(x,y,z));
	}
	public void setSkyLight( int x, int y, int z, int value ) {
		ChunkUtil.putNybble(skyLightData, blockIndex(x,y,z), value);
	}
	
	public byte getBlockLight( int x, int y, int z ) {
		return ChunkUtil.getNybble(blockLightData, blockIndex(x,y,z));
	}
	public void setBlockLight( int x, int y, int z, int value ) {
		ChunkUtil.putNybble(blockLightData, blockIndex(x,y,z), value);
	}
	
	/*
	 * http://www.minecraftwiki.net/wiki/Chunk_format says:
	 *  
	 *  HeightMap: 1024 bytes(256 TAG_Int) of heightmap data. 16 x 16. Each byte
	 *  records the lowest level in each column where the light from the sky is
	 *  at full strength. Speeds computing of the SkyLight. Note: This array's
	 *  indexes are ordered ZX whereas the other array indexes are ordered XZ or YZX
	 */
	
	protected int lightHeightIndex( int x, int z ) {
		return z*width+x;
	}
	
	public void setLightHeight( int x, int z, int height ) {
		lightHeightData[lightHeightIndex(x,z)] = (byte)(height);
	}
	
	//// biome ////
	
	protected int biomeIndex( int x, int z ) {
		return z*width+x;
	}
	
	public void setBiome( int x, int z, byte b ) {
		biomeData[biomeIndex(x,z)] = b;
    }
	
	//// Saving/loading ////
	
	static class Section {
		final byte index; // multiply by 16 to get y-position of bottom
		final int w, h, d;
		final byte[] blockIdsLow;
		final byte[] blockIdsHigh;
		final byte[] blockData;
		final byte[] skyLight;
		final byte[] blockLight;
		boolean hasHighBlockIds;
		boolean isNotEmpty;
		
		public Section( byte index, int w, int h, int d ) {
			this.index = index;
			this.w = w; this.h = h; this.d = d;
			this.blockIdsLow  = new byte[w*h*d];
			this.blockIdsHigh = new byte[w*h*d/2];
			this.blockData    = new byte[w*h*d/2];
			this.skyLight     = new byte[w*h*d/2];
			this.blockLight   = new byte[w*h*d/2];
			hasHighBlockIds = false;
		}
		
		public void copyFrom( ChunkData cd ) {
			hasHighBlockIds = false;
			isNotEmpty = false;
			for( int idx=0, y=0; y<h; ++y ) {
				int absY = y+index*16;
				for( int z=0; z<d; ++z ) {
					for( int x=0; x<w; ++x, ++idx ) {
						short blockId = cd.getBlockId(x,absY,z);
						if( blockId != 0 ) isNotEmpty = true;
						byte highId = (byte)(blockId >> 8);
						if( highId != 0 ) hasHighBlockIds = true;

						blockIdsLow[idx] = (byte)blockId;
						ChunkUtil.putNybble( blockIdsHigh, idx, highId );
						ChunkUtil.putNybble( blockData , idx, cd.getBlockData(x,absY,z) );
						ChunkUtil.putNybble( skyLight  , idx, cd.getSkyLight(x,absY,z) );
						ChunkUtil.putNybble( blockLight, idx, cd.getBlockLight(x,absY,z) );
					}
				}
			}
		}
		
		public CompoundTag toTag() {
			TagMap<Tag> components = new TagMap<Tag>();
			components.add(new ByteTag("Y", index));
			components.add(new ByteArrayTag("Blocks", blockIdsLow));
			if( hasHighBlockIds ) components.add(new ByteArrayTag("Data", blockIdsHigh));
			components.add(new ByteArrayTag("Data", blockData));
			components.add(new ByteArrayTag("SkyLight", skyLight));
			components.add(new ByteArrayTag("BlockLight", blockLight));
			return new CompoundTag("Section", components);
		}
	}
	
	public Tag toLevelTag() {
		TagMap<Tag> levelTags = new TagMap<Tag>();
		//levelTags.add(new ByteArrayTag("Blocks", blockIds));
		
		ArrayList<CompoundTag> sectionTags = new ArrayList<CompoundTag>();
		final int sectionHeight = 16;
		// Note: if height is not evenly divisible by sectionHeight,
		// your top section will be missing.
		for( int i=0; i<height/sectionHeight; ++i ) {
			Section s = new Section((byte)i, width, sectionHeight, depth);
			s.copyFrom(this);
			if( s.isNotEmpty ) {
				sectionTags.add( s.toTag() );
			}
		}
		
		levelTags.add( new ListTag<CompoundTag>("Sections", CompoundTag.class, sectionTags) );
		
		//levelTags.add(new ByteArrayTag("Data", blockData));
		//levelTags.add(new ByteArrayTag("SkyLight", skyLightData));
		//levelTags.add(new ByteArrayTag("BlockLight", blockLightData));
		
		levelTags.add(new ByteArrayTag("Biomes", biomeData));
		levelTags.add(new IntArrayTag("HeightMap", lightHeightData));
		levelTags.add(new ListTag<CompoundTag>("Entities", CompoundTag.class, Collections.<CompoundTag>emptyList()));
		
		List<CompoundTag> tileEntityTags = new ArrayList<CompoundTag>();
		for( TileEntityData ted : tileEntityData ) {
			tileEntityTags.add( ted.toTag() );
		}
		levelTags.add(new ListTag<CompoundTag>("TileEntities", CompoundTag.class, tileEntityTags));
		
		levelTags.add(new LongTag("LastUpdate", 23392));//System.currentTimeMillis()));
		levelTags.add(new IntTag("xPos", (int)(getChunkPositionX()/getChunkWidth())));
		levelTags.add(new IntTag("zPos", (int)(getChunkPositionZ()/getChunkDepth())));
		levelTags.add(new ByteTag("TerrainPopulated", (byte)(terrainPopulated ? 1 : 0)));
		return new CompoundTag("Level", levelTags);
	}
	
	public static ChunkData forChunkCoords( long cx, long cz ) {
		return new ChunkData(
			cx*NORMAL_CHUNK_WIDTH, 0, cz*NORMAL_CHUNK_DEPTH,
			NORMAL_CHUNK_WIDTH, NORMAL_CHUNK_HEIGHT, NORMAL_CHUNK_DEPTH
		);
	}
	
	/*
	public static ChunkData fromTag( CompoundTag t ) {
		Map<String,Tag> m = t.getValue();
		IntTag xPos = (IntTag)m.get( "xPos" );
		IntTag zPos = (IntTag)m.get( "zPos" );
		
		ChunkData cd = forChunkCoords(
			xPos.getIntValue(),
			zPos.getIntValue()
		);
		
		byte[] blockLowIds = ((ByteArrayTag)m.get("Blocks")).getValue(); 
		cd.blockIds = new short[blockLowIds.length];
		for( int i=blockLowIds.length-1; i>=0; --i ) {
			cd.blockIds[i] = blockLowIds[i];
		}
		// TODO: read Add (high block ID nybbles)
		cd.blockData = ((ByteArrayTag)m.get("Data")).getValue();
		cd.skyLightData = ((ByteArrayTag)m.get("SkyLight")).getValue();
		cd.blockLightData = ((ByteArrayTag)m.get("BlockLight")).getValue();
		cd.lightHeightData = ((IntArrayTag)m.get("HeightMap")).getInts();
		// TODO: this part
		//cd.tileEntityData = ((CompoundTag)m.get("TileEntities")).getValue();
		return cd;
	}
	*/
}
