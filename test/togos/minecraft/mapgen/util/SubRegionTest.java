package togos.minecraft.mapgen.util;

import junit.framework.TestCase;
import togos.mf.base.SimpleByteChunk;
import togos.mf.value.ByteChunk;

public class SubRegionTest extends TestCase
{
	SubRegion empty1 = new SubRegion( (long)0, (long)0, (long)0, (short)0, (short)0, (short)0 );
	SubRegion empty2 = new SubRegion( (long)0, (long)0, (long)0, (short)0, (short)0, (short)0 );
	SubRegion empty3 = new SubRegion( (long)0, (long)0, (long)1, (short)0, (short)0, (short)0 );
	SubRegion nonEmpty1 = new SubRegion( (long)0, (long)0, (long)0, (short)1, (short)1, (short)1 );
	SubRegion nonEmpty2 = new SubRegion( (long)0, (long)0, (long)0, (short)1, (short)1, (short)1 );
	SubRegion nonEmpty3 = new SubRegion( (long)0, (long)0, (long)0, (short)1, (short)1, (short)1 );
	SubRegion nonEmpty4 = new SubRegion( (long)0, (long)0, (long)0, (short)1, (short)1, (short)1 );
	
	ByteChunk bc1 = new SimpleByteChunk( new byte[]{1,2,3,4} );
	ByteChunk bc2 = new SimpleByteChunk( new byte[]{1,2,3,4} );
	ByteChunk bc3 = new SimpleByteChunk( new byte[]{4,3,2,1} );
	
	public SubRegionTest() {
		nonEmpty1.chunkData[0] = bc1;
		nonEmpty2.chunkData[0] = bc2;
		nonEmpty3.chunkData[0] = bc3;
		nonEmpty4.chunkData[0] = bc3;
		nonEmpty4.chunkMtime[0] = 123;
	}
	
	protected void assertNotEquals( Object o1, Object o2 ) {
		assertFalse( o1.equals(o2) );
	}
	
	public void testEquals() {
		assertEquals( empty1, empty2 );
		assertNotEquals( empty1, empty3 );
		
		assertEquals( nonEmpty1, nonEmpty2 );
		assertNotEquals( nonEmpty1, nonEmpty3 );
		
		assertNotEquals( empty1, nonEmpty1 );
		
		assertNotEquals( nonEmpty3, nonEmpty4 );
	}
}
