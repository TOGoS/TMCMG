package togos.minecraft.mapgen.util;

public class SimpleByteChunk implements ByteChunk
{
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
	
	public boolean equals( Object o ) {
		return o instanceof ByteChunk ? Util.equals(this, (ByteChunk)o) : false;
	}
	public int hashCode() {
		return Util.hashCode(this);
	}
}
