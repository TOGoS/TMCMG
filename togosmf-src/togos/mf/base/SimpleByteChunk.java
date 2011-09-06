package togos.mf.base;

import togos.mf.value.ByteChunk;

public class SimpleByteChunk implements ByteChunk
{
	public static final SimpleByteChunk EMPTY = new SimpleByteChunk(new byte[0],0,0);
	
	public static SimpleByteChunk copyOf( byte[] buf, int offset, int size ) {
		byte[] bu2 = new byte[size];
		SimpleByteChunk sbc = new SimpleByteChunk( bu2, 0, size );
		for( size = size-1; size >= 0; --size ) {
			bu2[size] = buf[offset+size];
		}
		return sbc;
	}
	
	public final byte[] data;
	public final int offset;
	public final int length;
	
	public SimpleByteChunk( byte[] buf, int offset, int length ) {
		this.data = buf;
		this.offset = offset;
		this.length = length;
	}
	
	public SimpleByteChunk( byte[] buf ) {
		this( buf, 0, buf.length );
	}

	public byte[] getBuffer() { return data; }
	public int getOffset() { return offset; }
	public int getSize() { return length; }
	
	public int hashCode() {
		return Util.hashCode(data, offset, length);
	}
	
	public boolean equals( Object o ) {
		if( o instanceof ByteChunk ) return Util.equals( this, (ByteChunk)o );
		return false;
	}
}
