package togos.minecraft.mapgen.world.gen;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import togos.minecraft.mapgen.world.structure.ChunkData;

public class ChunkMungeList implements ChunkMunger
{
	protected List chunkMungers = new ArrayList();
	
	public void addMunger( ChunkMunger m ) {
		chunkMungers.add(m);
	}
	
	public void mungeChunk( ChunkData cd ) {
		for( Iterator mi=chunkMungers.iterator(); mi.hasNext(); ) {
			((ChunkMunger)mi.next()).mungeChunk(cd);
		}
	}
}
