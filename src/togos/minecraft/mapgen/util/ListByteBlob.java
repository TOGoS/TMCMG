package togos.minecraft.mapgen.util;

import java.util.Iterator;
import java.util.List;

public class ListByteBlob implements ByteBlob
{
	List l;
	long size;
	
	public ListByteBlob( List l, long size ) {
		this.l = l;
		this.size = size;
	}
	
	public Iterator chunkIterator() {
		return l.iterator();
	}
	
	public long getSize() {
		return size;
	}
}
