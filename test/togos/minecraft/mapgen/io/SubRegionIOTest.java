package togos.minecraft.mapgen.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import togos.minecraft.mapgen.util.ByteChunk;
import togos.minecraft.mapgen.util.SimpleByteChunk;
import togos.minecraft.mapgen.util.SubRegion;
import junit.framework.TestCase;

public class SubRegionIOTest extends TestCase
{
	ByteChunk someData1 = new SimpleByteChunk( new byte[]{ 1,2,3,4,5 } );
	ByteChunk someData2 = new SimpleByteChunk( new byte[]{ 2,1,5,3,4 } );
	
	SubRegion sr = new SubRegion( (long)1, (long)2, (long)-3, (short)2, (short)2, (short)2, (short)16, (short)128, (short)256 );
	
	public SubRegionIOTest() {
		sr.chunkData[0] = someData1;
		sr.chunkData[1] = someData1;
		sr.chunkData[2] = someData2;
		sr.chunkData[3] = someData1;
		sr.chunkData[4] = someData1;
		sr.chunkData[5] = someData2;
		sr.chunkData[6] = someData1;
		sr.chunkData[7] = someData2;
		
		sr.chunkMtime[5] = 0x7FEEDDCC;
	}
	
	public void testCodec() throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		SubRegionIO.wrtie(sr, baos);
		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		assertEquals( sr, SubRegionIO.read(bais) );
	}
}
