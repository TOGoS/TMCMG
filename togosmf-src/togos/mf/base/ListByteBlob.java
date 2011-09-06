package togos.mf.base;

import java.util.Iterator;
import java.util.List;

import togos.mf.value.ByteBlob;

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
