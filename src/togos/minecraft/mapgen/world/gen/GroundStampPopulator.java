package togos.minecraft.mapgen.world.gen;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import togos.minecraft.mapgen.world.structure.Stamp;
import togos.noise.v1.data.DataDaDa;
import togos.noise.v1.data.DataDaIa;
import togos.noise.v1.func.FunctionDaDa_Da;
import togos.noise.v1.func.FunctionDaDa_DaIa;
import togos.noise.v1.func.LFunctionDaDa_DaIa;
import togos.noise.v1.lang.FunctionUtil;

public class GroundStampPopulator implements StampPopulator
{
	FunctionDaDa_Da densityFunction;
	double maxDensity;
	LFunctionDaDa_DaIa groundFunction;
	int[] allowedGroundTypes;
	Stamp[] stamps;
	int placementSeed = 0;
	
	long hashMultiplier = -573845504;
	StampGenerator stampGenerator = new RoundTreeGenerator();
	
	public GroundStampPopulator(
		StampGenerator stampGenerator, int instanceCount,
		FunctionDaDa_Da densityFunction, double maxDensity,
		LFunctionDaDa_DaIa groundFunction, int[] allowedGroundTypes
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
	
	protected void collect( Collection<StampInstance> instances, long cwx, long cwz, int cw, int cd ) {
		Random r = new Random((cwx*1234+cwz) ^ placementSeed);
		
		double density = FunctionUtil.getValue( densityFunction, cwx, cwz );
		int count = (int)(Math.min(maxDensity,density)*cw*cd);
		if( count < 0 ) return;
		double[] x = new double[count];
		double[] z = new double[count];
		for( int i=0; i<count; ++i ) {
			x[i] = cwx + r.nextInt(cw);
			z[i] = cwz + r.nextInt(cd);
		}
		double[] groundHeight = new double[count];
		int[] groundType = new int[count];
		groundFunction.apply(count, x, z, groundHeight, groundType );
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
	
	public Collection<StampInstance> getStampInstances( long cwx, long cwz, int cw, int cd ) {
		ArrayList<StampInstance> instances = new ArrayList<StampInstance>();
		collect( instances, cwx-cw, cwz-cd, cw, cd );
		collect( instances, cwx   , cwz-cd, cw, cd );
		collect( instances, cwx+cw, cwz-cd, cw, cd );
		collect( instances, cwx-cw, cwz   , cw, cd );
		collect( instances, cwx   , cwz   , cw, cd );
		collect( instances, cwx+cw, cwz   , cw, cd );
		collect( instances, cwx-cw, cwz+cd, cw, cd );
		collect( instances, cwx   , cwz+cd, cw, cd );
		collect( instances, cwx+cw, cwz+cd, cw, cd );
		return instances;
	}
}
