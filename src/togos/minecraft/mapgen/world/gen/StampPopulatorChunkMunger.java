package togos.minecraft.mapgen.world.gen;

import java.util.Collection;
import java.util.Iterator;

import togos.minecraft.mapgen.world.ChunkUtil;
import togos.minecraft.mapgen.world.structure.ChunkData;

public class StampPopulatorChunkMunger implements ChunkMunger
{
	protected StampPopulator popu;
	public StampPopulatorChunkMunger( StampPopulator popu ) {
		this.popu = popu;
	}
	
	public void mungeChunk( ChunkData cd ) {
		Collection inst = popu.getStampInstances( cd.getChunkPositionX(), cd.getChunkPositionZ(), cd.getChunkWidth(), cd.getChunkDepth() );
		for( Iterator i=inst.iterator(); i.hasNext(); ) {
			StampPopulator.StampInstance si = (StampPopulator.StampInstance)i.next();
			ChunkUtil.stamp( cd, si.stamp, (int)(si.wx-cd.getChunkPositionX()), (int)si.wy, (int)(si.wz-cd.getChunkPositionZ()));
		}
	}
}
