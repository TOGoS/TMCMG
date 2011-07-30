package togos.minecraft.mapgen.world.gen;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import togos.minecraft.mapgen.world.structure.Stamp;
import togos.noise2.vm.dftree.data.DataDaDa;
import togos.noise2.vm.dftree.data.DataDaIa;
import togos.noise2.vm.dftree.func.FunctionDaDa_Da;
import togos.noise2.vm.dftree.func.FunctionDaDa_DaIa;
import togos.noise2.vm.dftree.lang.FunctionUtil;

public class GroundStampPopulator implements StampPopulator
{
	FunctionDaDa_Da densityFunction;
	double maxDensity;
	FunctionDaDa_DaIa groundFunction;
	int[] allowedGroundTypes;
	Stamp[] stamps;
	int placementSeed = 0;
	
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
	
	protected void collect( Collection instances, long cwx, long cwz, int cw, int cd ) {
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
		DataDaIa ground = groundFunction.apply(new DataDaDa(x,z));
		for( int i=0; i<count; ++i ) {
			boolean allowPlacement = false;
			for( int j=0; j<allowedGroundTypes.length; ++j ) {
				if( allowedGroundTypes[j] == ground.i[i] ) {
					allowPlacement = true;
					break;
				}
			}
			if( allowPlacement ) {
				long wx = (long)x[i];
				long wz = (long)z[i];
				instances.add(new StampInstance( getStamp(wx,wz), wx, (int)ground.d[i], wz ));
			}
		}
	}
	
	public Collection getStampInstances( long cwx, long cwz, int cw, int cd ) {
		ArrayList instances = new ArrayList();
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
