package togos.minecraft.mapgen.world;

import junit.framework.TestCase;

public class ChunkUtilTest extends TestCase
{
	public void testPutGetNybble() {
		byte[] data = new byte[8];
		
		ChunkUtil.putNybble( data, 0, 0x7 );
		assertEquals( 0x07, data[0] );
		assertEquals( 0x7, ChunkUtil.getNybble(data,0) );
		
		ChunkUtil.putNybble( data, 0, 0x4 );
		assertEquals( 0x04, data[0] );
		assertEquals( 0x4, ChunkUtil.getNybble(data,0) );
		
		ChunkUtil.putNybble( data, 1, 0x3 );
		assertEquals( 0x34, data[0] );
		assertEquals( 0x4, ChunkUtil.getNybble(data,0) );
		assertEquals( 0x3, ChunkUtil.getNybble(data,1) );

		ChunkUtil.putNybble( data, 1, 0xA );
		assertEquals( (byte)0xA4, data[0] );
		assertEquals( 0x4, ChunkUtil.getNybble(data,0) );
		assertEquals( 0xA, ChunkUtil.getNybble(data,1) );
		
		for( int i=0; i<8; ++i ) {
			ChunkUtil.putNybble( data, i, i );
		}
		for( int i=0; i<8; ++i ) {
			assertEquals( i, ChunkUtil.getNybble( data, i ) );
		}
	}
}
