package togos.minecraft.mapgen.world.structure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.jnbt.ByteArrayTag;
import org.jnbt.ByteTag;
import org.jnbt.CompoundTag;
import org.jnbt.IntTag;
import org.jnbt.ListTag;
import org.jnbt.LongTag;
import org.jnbt.Tag;

import togos.minecraft.mapgen.TagMap;

public class ChunkData
{
	public static int HEIGHT_MASK  = 0x7F;

	public final int height = 128; // Y/+up/-down
	public final int depth  =  16; // Z/+west/-east
	public final int width  =  16; // X/+south/-north
	
	public byte[] blockData      = new byte[height*depth*width];
	public byte[] blockExtraBits = new byte[height*depth*width/2];
	public byte[] skyLightData   = new byte[height*depth*width/2];
	public byte[] blockLightData = new byte[height*depth*width/2];
	public byte[] lightHeightData = new byte[256];
	public List tileEntityData = new ArrayList();
	public int x,z;
	
	protected int blockIndex( int x, int y, int z ) {
		return y + z*height + x*depth*height;
	}
	
	protected void putNybble( byte[] data, int index, int value ) {
		int byteIndex = index>>1;
		byte oldValue = data[byteIndex];
		if( (index & 0x1) == 0 ) {
			data[ byteIndex ] = (byte)((oldValue & 0xF0) | (value & 0x0F));
		} else {
			data[ byteIndex ] = (byte)((oldValue & 0x0F) | ((value<<4) & 0xF0));
		}
	}
	
	//// Block ////
	
	public void setBlock( int x, int y, int z, byte blockNum, byte extraBits ) {
		setBlock( x, y, z, blockNum );
		setBlockExtraBits( x, y, z, extraBits );
	}
	
	public byte getBlock( int x, int y, int z ) {
		return blockData[ blockIndex(x,y,z) ];
	}
	
	public void setBlock( int x, int y, int z, byte blockNum ) {
		blockData[ blockIndex(x,y,z) ] = blockNum;
	}
	
	public void setBlockExtraBits( int x, int y, int z, byte value ) {
		putNybble( blockExtraBits, blockIndex(x,y,z), value );
	}
	
	//// Sky light ////
	
	public void setSkyLight( int x, int y, int z, int value ) {
		putNybble(skyLightData, blockIndex(x,y,z), value);
	}
	
	//// Light height ////
	
	public void setLightHeight( int x, int z, int height ) {
		lightHeightData[z*width+x] = (byte)(height&HEIGHT_MASK);
	}
	
	public Tag toTag() {
		TagMap levelTags = new TagMap();
		levelTags.add(new ByteArrayTag("Blocks", blockData));
		levelTags.add(new ByteArrayTag("Data", blockExtraBits));
		levelTags.add(new ByteArrayTag("SkyLight", skyLightData));
		levelTags.add(new ByteArrayTag("BlockLight", blockLightData));
		levelTags.add(new ByteArrayTag("HeightMap", lightHeightData));
		levelTags.add(new ListTag("Entities", CompoundTag.class, Collections.EMPTY_LIST));
		
		List tileEntityTags = new ArrayList();
		for( Iterator tidi=tileEntityData.iterator(); tidi.hasNext(); ) {
			tileEntityTags.add( ((TileEntityData)tidi.next()).toTag() );
		}
		levelTags.add(new ListTag("TileEntities", CompoundTag.class, tileEntityTags));
		
		levelTags.add(new LongTag("LastUpdate", 23392));//System.currentTimeMillis()));
		levelTags.add(new IntTag("xPos", x));
		levelTags.add(new IntTag("zPos", z));
		levelTags.add(new ByteTag("TerrainPopulated", (byte)1));
		return new CompoundTag("Level", levelTags);
	}
}
