package togos.minecraft.mapgen.util;

public interface ByteChunk
{
	public int getOffset();
	public int getSize();
	public byte[] getBuffer();
}
