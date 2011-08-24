package togos.minecraft.mapgen.util;

public interface ByteChunk
{
	public int getOffset();
	public int getSize();
	public byte[] getBuffer();
	
	/* equals and hashCode should always be defined as 
	public boolean equals( Object o ) {
		return o instanceof ByteChunk ? Util.equals(this, (ByteChunk)o) : false;
	}
	public int hashCode() {
		return Util.hashCode(this);
	}
	*/
}
