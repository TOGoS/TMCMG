package togos.minecraft.mapgen.data;

import junit.framework.TestCase;

public class ChunkTest extends TestCase
{
	Chunk c1 = new Chunk( new byte[]{0,1,2,3,4,5}, 2, 4 );
	Chunk c2 = new Chunk( new byte[]{2,3,4,5,6,7}, 0, 4 );
	Chunk c3 = new Chunk( new byte[]{4,5,6,7,8,9}, 0, 4 );
	Chunk c4 = new Chunk( new byte[]{4,5,6,7,8,9}, 0, 3 );
	
	public void testChunkEquals() {
		assertEquals( c1.hashCode(), c2.hashCode() );
		assertEquals( c1, c2 );
		assertEquals( c2, c1 );
		
		assertFalse( c2.equals(c3) );
		assertFalse( c3.equals(c2) );
		assertFalse( c3.equals(c4) );
		assertFalse( c4.equals(c3) );
	}
}
