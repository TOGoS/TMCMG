package togos.minecraft.mapgen.world.gen;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import togos.minecraft.mapgen.world.Blocks;
import togos.minecraft.mapgen.world.structure.ChunkData;
import togos.minecraft.mapgen.world.structure.Stamp;
import togos.noise2.function.FunctionDaDa_Da;
import togos.noise2.function.FunctionDaDa_DaIa;


public class TreeStampPopulator implements StampPopulator
{
	FunctionDaDa_Da densityFunction;
	FunctionDaDa_DaIa groundFunction;
	
	Stamp treeStamp = new TreeGenerator().generate(100);
	
	protected void collect( Collection instances, int cx, int cz ) {
		Random r = new Random(cx*1234+cz);
		double[] density = new double[1];
		
		long cwx = (long)cx*ChunkData.CHUNK_WIDTH;
		long cwz = (long)cz*ChunkData.CHUNK_DEPTH;
		
		densityFunction.apply(1,
			new double[]{cwx},
			new double[]{cwz},
			density
		);
		int count = (int)(density[0]*ChunkData.CHUNK_WIDTH*ChunkData.CHUNK_DEPTH);
		double[] x = new double[count];
		double[] z = new double[count];
		double[] groundHeight = new double[count];
		int[] groundType = new int[count];
		for( int i=0; i<count; ++i ) {
			x[i] = cwx + r.nextInt(ChunkData.CHUNK_WIDTH);
			z[i] = cwz + r.nextInt(ChunkData.CHUNK_DEPTH);
		}
		groundFunction.apply(count, x,z, groundHeight, groundType);
		for( int i=0; i<count; ++i ) {
			if( groundType[i] == Blocks.DIRT || groundType[i] == Blocks.GRASS ) {
				long wx = (long)x[i];
				long wz = (long)z[i];
				instances.add(new StampInstance( treeStamp, wx, (int)groundHeight[i], wz ));
				//System.err.println("Tree at "+wx+","+(int)groundHeight[i]+","+wz);
			}
		}
	}
	
	public Collection getStampInstances( int cx, int cz ) {
		ArrayList instances = new ArrayList();
		collect( instances, cx-1, cz-1 );
		collect( instances, cx  , cz-1 );
		collect( instances, cx+1, cz-1 );
		collect( instances, cx-1, cz   );
		collect( instances, cx  , cz   );
		collect( instances, cx+1, cz   );
		collect( instances, cx-1, cz+1 );
		collect( instances, cx  , cz+1 );
		collect( instances, cx+1, cz+1 );
		return instances;
	}
}
