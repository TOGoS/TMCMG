package togos.minecraft.mapgen.io;

import java.io.ByteArrayOutputStream;

import togos.minecraft.mapgen.util.ByteChunk;

/**
 * Extends ByteArrayOutputStream to provides direct access to the backing buffer.
 */
public class BetterByteArrayOutputStream extends ByteArrayOutputStream implements ByteChunk
{
	public BetterByteArrayOutputStream( byte[] buf ) {
		this.buf = buf;
	}
	
	public BetterByteArrayOutputStream( int size ) {
		super(size);
	}
	
	public BetterByteArrayOutputStream() {
		super();
	}
	
	public byte[] getBuffer() {  return buf;  }
	public int getOffset() {  return 0;  }
	public int getSize() {  return count;  }
}
