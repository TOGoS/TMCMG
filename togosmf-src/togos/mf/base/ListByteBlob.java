package togos.mf.base;

import java.util.Iterator;
import java.util.List;

import togos.mf.value.ByteBlob;
import togos.mf.value.ByteChunk;

public class ListByteBlob implements ByteBlob
{
	List<ByteChunk> l;
	long size;
	
	public ListByteBlob( List<ByteChunk> l, long size ) {
		this.l = l;
		this.size = size;
	}
	
	public Iterator<ByteChunk> iterator() {
		return l.iterator();
	}
	
	public long getSize() {
		return size;
	}
}
