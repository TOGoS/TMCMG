package togos.minecraft.mapgen.util;

import java.util.Iterator;

public interface ByteBufferList
{
	public Iterator bufferIterator();
	
	/**
	 * Total number of bytes in this region list.
	 * 
	 * May return -1 if the list is endless or if
	 * the size is unknown without iterating through
	 * the buffers.
	 */
	public long getSize();
}
