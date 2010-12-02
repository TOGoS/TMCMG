package togos.minecraft.mapgen.world.gen;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import togos.minecraft.mapgen.world.structure.ChunkData;
import togos.minecraft.mapgen.world.structure.Stamp;
import togos.noise2.function.FunctionDaDa_Da;
import togos.noise2.function.FunctionDaDa_DaIa;

public class GroundStampPopulator implements StampPopulator
{
	FunctionDaDa_Da densityFunction;
	double maxDensity;
	FunctionDaDa_DaIa groundFunction;
	int[] allowedGroundTypes;
	Stamp[] stamps;
	
	long hashMultiplier = -573845504;
	StampGenerator stampGenerator = new RoundTreeGenerator();
	
	public GroundStampPopulator(
		StampGenerator stampGenerator, int instanceCount,
		FunctionDaDa_Da densityFunction, double maxDensity,
		FunctionDaDa_DaIa groundFunction, int[] allowedGroundTypes
	) {
		this.stampGenerator = stampGenerator;
		this.densityFunction = densityFunction;
		this.maxDensity = maxDensity;
		this.groundFunction = groundFunction;
		this.stamps = new Stamp[instanceCount];
		this.allowedGroundTypes = allowedGroundTypes;
	}
	
	protected Stamp getStamp(long wx, long wy) {
		int seed = (int)((wx*hashMultiplier + wy)%stamps.length);
		if( seed < 0 ) seed = -seed;
		if( stamps[seed] == null ) stamps[seed] = stampGenerator.generateStamp(seed);
		return stamps[seed];
	}
	
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
		int count = (int)(Math.min(maxDensity,density[0])*ChunkData.CHUNK_WIDTH*ChunkData.CHUNK_DEPTH);
		if( count < 0 ) return;
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
			boolean allowPlacement = false;
			for( int j=0; j<allowedGroundTypes.length; ++j ) {
				if( allowedGroundTypes[j] == groundType[i] ) {
					allowPlacement = true;
					break;
				}
			}
			if( allowPlacement ) {
				long wx = (long)x[i];
				long wz = (long)z[i];
				instances.add(new StampInstance( getStamp(wx,wz), wx, (int)groundHeight[i], wz ));
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
