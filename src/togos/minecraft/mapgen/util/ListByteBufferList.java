package togos.minecraft.mapgen.util;

import java.util.Iterator;
import java.util.List;

public class ListByteBufferList implements ByteBlob
{
	List l;
	long size;
	
	public ListByteBufferList( List l, long size ) {
		this.l = l;
		this.size = size;
	}
	
	public Iterator bufferIterator() {
		return l.iterator();
	}
	
	public long getSize() {
		return size;
	}
}
