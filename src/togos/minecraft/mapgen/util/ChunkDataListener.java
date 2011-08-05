package togos.minecraft.mapgen.util;

public interface ChunkDataListener
{
	public void setChunkData( String worldId, long px, long py, long pz, int w, int h, int d, byte[] data, int format );
}
