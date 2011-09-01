package togos.minecraft.mapgen.util;

import junit.framework.TestCase;
import togos.mf.base.SimpleByteChunk;
import togos.mf.value.ByteChunk;

public class ByteChunkTest extends TestCase
{
	byte[] buf4 = new byte[] { 1, 2, 3, 4 };
	byte[] buf5a = new byte[] { 1, 2, 3, 4, 5 };
	byte[] buf5c = new byte[] { 2, 3, 4, 5, 6 };
	
	ByteChunk chunk4a = new SimpleByteChunk(buf4);
	ByteChunk chunk4b = new SimpleByteChunk(buf5a,0,4);
	ByteChunk chunk4c = new SimpleByteChunk(buf5a,1,4);
	ByteChunk chunk4d = new SimpleByteChunk(buf5c,0,4);
	ByteChunk chunk5a = new SimpleByteChunk(buf5a);
	ByteChunk chunk5b = new SimpleByteChunk(buf5c);
	
	protected void assertEquals( ByteChunk c1, ByteChunk c2 ) {
		assertEquals( (Object)c1, (Object)c2 );
		assertEquals( c1.hashCode(), c2.hashCode() );
	}
	
	protected void assertNotEquals( Object o1, Object o2 ) {
		assertFalse( o1.equals(o2) );
	}
	
	public void testEquals() {
		assertEquals( chunk4a, chunk4b );
		assertEquals( chunk4c, chunk4d );
	}
	
	public void testNotEquals() {
		assertNotEquals( chunk4a, chunk4c );
		assertNotEquals( chunk4a, chunk5a );
		assertNotEquals( chunk5a, chunk5b );
	}
}
