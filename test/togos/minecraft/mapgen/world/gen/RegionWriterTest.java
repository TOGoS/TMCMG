package togos.minecraft.mapgen.world.gen;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import junit.framework.TestCase;

import org.jnbt.CompoundTag;
import org.jnbt.NBTInputStream;

import togos.mf.value.ByteBlob;
import togos.minecraft.mapgen.io.RegionFile;
import togos.minecraft.mapgen.io.RegionWriter;
import togos.minecraft.mapgen.util.Util;
import togos.minecraft.mapgen.world.Blocks;
import togos.minecraft.mapgen.world.structure.ChunkData;

public class RegionWriterTest extends TestCase
{
	protected ChunkData getChunkData( RegionFile rf, int cx, int cz ) throws IOException {
		DataInputStream dis = rf.getChunkDataInputStream( cx, cz );
		NBTInputStream nis = new NBTInputStream(dis);
		CompoundTag rootTag = (CompoundTag)nis.readTag();
		nis.close();
		CompoundTag levelTag = (CompoundTag)rootTag.getComponents().get("Level");
		return ChunkData.fromTag( levelTag );
	}
	
	protected void encodeInt( ChunkData cd, int x, int y, int z, int number, int bits, byte b0, byte b1 ) {
		for( ; bits>0; ++x ) {
			cd.setBlock(x,y,z,((number >> --bits)&1) == 0 ? b0 : b1);
		}
	}
	
	protected int decodeInt( ChunkData cd, int x, int y, int z, int bits, byte b0, byte b1 ) {
		int number = 0;
		for( ; bits>0; ++x ) {
			number |= ((cd.getBlock(x,y,z) == b1 ? 1 : 0) << --bits);
		}
		return number;
	}
	
	protected void assertEncodedPosition( ChunkData cd ) {
		int cx = (int)(cd.posX / cd.width );
		int cy = (int)(cd.posY / cd.height);
		int cz = (int)(cd.posZ / cd.depth );
		assertEquals( cx&0xFF, decodeInt(cd,1,cd.height/2,1,8,Blocks.AIR,Blocks.LOG) );
		assertEquals( cy&0xFF, decodeInt(cd,1,cd.height/2,2,8,Blocks.AIR,Blocks.LOG) );
		assertEquals( cz&0xFF, decodeInt(cd,1,cd.height/2,3,8,Blocks.AIR,Blocks.LOG) );
	}
	
	public void testRegionGenerator() throws IOException {
		ChunkMunger cm = new ChunkMunger() {
			public void mungeChunk( ChunkData cd ) {
	            for( int z=0; z<cd.depth; ++z ) {
	            	for( int x=0; x<cd.width; ++x ) {
	            		for( int y=0; y<cd.height/2; ++y ) {
	            			cd.setBlock(x,y,z,Blocks.STONE);
	            		}
	            	}
	            }
				int cx = (int)(cd.posX / cd.width ); 
				int cy = (int)(cd.posY / cd.height);
				int cz = (int)(cd.posZ / cd.depth );
				encodeInt( cd, 1, cd.height/2, 1, cx, 8, Blocks.AIR, Blocks.LOG );
				encodeInt( cd, 1, cd.height/2, 2, cy, 8, Blocks.AIR, Blocks.LOG );
				encodeInt( cd, 1, cd.height/2, 3, cz, 8, Blocks.AIR, Blocks.LOG );
            }
		};
		ChunkGenerator cg = new ChunkGenerator(cm);
		RegionWriter rw = new RegionWriter();
		
		ByteBlob bbl = rw.generateRegion( cg, 10, 14, (int)(System.currentTimeMillis()/1000) );
		
		assertEquals( 1026*4096, bbl.getSize() );
		
		File f = File.createTempFile("test-generated-region","mcr");
		f.deleteOnExit();
		FileOutputStream fos = new FileOutputStream(f);
		Util.write( bbl, fos );
		fos.close();
		
		DataInputStream dis = new DataInputStream(new FileInputStream(f));
		assertEquals( 513, dis.readInt() );
		dis.close();
		
		RegionFile rf = new RegionFile(f);
		assertEncodedPosition( getChunkData(rf, 0, 0) );
		assertEncodedPosition( getChunkData(rf, 0, 5) );
		assertEncodedPosition( getChunkData(rf, 7, 5) );
		assertEncodedPosition( getChunkData(rf,24,29) );
		assertEncodedPosition( getChunkData(rf,31,31) );
	}
}
