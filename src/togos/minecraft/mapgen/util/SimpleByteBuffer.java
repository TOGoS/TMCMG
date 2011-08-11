package togos.minecraft.mapgen.util;

public class SimpleByteBuffer implements ByteBuffer
{
	public final byte[] data;
	public final int offset;
	public final int length;
	
	public SimpleByteBuffer( byte[] buf, int offset, int length ) {
		this.data = buf;
		this.offset = offset;
		this.length = length;
	}
	
	public SimpleByteBuffer( byte[] buf ) {
		this( buf, 0, buf.length );
	}

	public byte[] getData() { return data; }
	public int getOffset() { return offset; }
	public int getSize() { return length; }
}
