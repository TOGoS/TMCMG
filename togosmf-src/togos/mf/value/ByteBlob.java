package togos.mf.value;

/**
 * A blob is a series of chunks.
 */
public interface ByteBlob extends Iterable<ByteChunk>
{
	/**
	 * Total number of bytes in this blob.
	 * 
	 * May return -1 if the list is endless or if
	 * the size is unknown without iterating through
	 * the buffers.
	 */
	public long getSize();
}
