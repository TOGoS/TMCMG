package togos.minecraft.mapgen.util;

import togos.mf.value.ByteChunk;

public interface ChunkDataListener
{
	public void setChunkData( long px, long py, long pz, int w, int h, int d, ByteChunk data, int format );
}
