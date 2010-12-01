package togos.minecraft.mapgen.world.gen;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import togos.minecraft.mapgen.world.Blocks;
import togos.minecraft.mapgen.world.structure.ChunkData;
import togos.noise2.function.FunctionDaDa_Da;
import togos.noise2.function.FunctionDaDa_DaIa;
import togos.noise2.function.FunctionDaDa_Ia;

public class LayerTerrainGenerator implements WorldGenerator
{	
	public static class Layer {
		public FunctionDaDa_Ia typeFunction;
		public FunctionDaDa_Da floorHeightFunction;
		public FunctionDaDa_Da ceilingHeightFunction;
		
		public Layer(
			FunctionDaDa_Ia typeFunction,
			FunctionDaDa_Da floorHeightFunction,
			FunctionDaDa_Da ceilingHeightFunction
		) {
			this.typeFunction = typeFunction;
			this.floorHeightFunction = floorHeightFunction;
			this.ceilingHeightFunction = ceilingHeightFunction;
		}
	}
	
	public List layers = new ArrayList();
	protected Layer[] layerArray( List layers ) {
		Layer[] larr = new Layer[layers.size()];
		int n=0;
		for( Iterator i=layers.iterator(); i.hasNext(); ) {
			larr[n++] = (Layer)i.next();
		}
		return larr;
	}
	
	class LayerChunkMunger implements ChunkMunger {
		protected List layers;
		
		public LayerChunkMunger( List layers ) {
			this.layers = layers;
		}
		
		public void mungeChunk( ChunkData cd ) {
			int cwx = cd.x*cd.width;
			int cwz = cd.z*cd.depth;
			int count = cd.width*cd.depth;
			double[] x = new double[count];
			double[] z = new double[count];
			double[] ceiling = new double[count];
			double[] floor = new double[count];
			int[] type = new int[count];
			int i=0;
			for( int tx=0; tx<cd.width; ++tx ) {
				for( int tz=0; tz<cd.depth; ++tz ) {
					x[i] = cwx+tx;
					z[i] = cwz+tz;
					++i;
				}
			}
			for( Iterator li=layers.iterator(); li.hasNext();  ) {
				i=0;
				Layer l = (Layer)li.next();
				// mix up z and y:
				l.ceilingHeightFunction.apply(z.length, x, z, ceiling);
				l.floorHeightFunction.apply(z.length, x, z, floor);
				l.typeFunction.apply(z.length, x, z, type);
				for( int tx=0; tx<cd.width; ++tx ) {
					for( int tz=0; tz<cd.depth; ++tz, ++i ) {
						double flo = floor[i];
						if( flo < 0 ) flo = 0;
						double cei = ceiling[i];
						if( cei > cd.height ) cei = cd.height; 
						for( int ty=(int)flo; ty<cei; ++ty ) {
							cd.setBlock(tx, ty, tz, (byte)type[i]);
						}
					}
				}
			}
		}
	}
	
	public static class LayerGroundFunction implements FunctionDaDa_DaIa {
		public static final int AIR_IGNORE = 0;
		public static final int AIR_SUBTRACT = 1;
		public static final int AIR_NORMAL = 2;
		
		public List layers;
		public int airTreatment;
		public LayerGroundFunction( List layers, int airTreatment ) {
			this.layers = layers;
			this.airTreatment = airTreatment;
		}
		
		public void apply( int count, double[] inX, double[] inY, double[] outZ, int[] outT ) {
	    	double[] lCeil = new double[count];
	    	double[] lFloor = new double[count];
	    	int[] lType = new int[count];
	    	double[] highest = outZ;
	    	for( int j=0; j<count; ++j ) {
	    		highest[j] = Double.NEGATIVE_INFINITY;
	    		outT[j] = Blocks.AIR;
	    	}
		    for( Iterator li=layers.iterator(); li.hasNext(); ) {
		    	Layer l = (Layer)li.next();
		    	l.ceilingHeightFunction.apply(count, inX, inY, lCeil);
		    	l.floorHeightFunction.apply(count, inX, inY, lFloor);
		    	l.typeFunction.apply(count, inX, inY, lType);
		    	for( int j=0; j<count; ++j ) {
		    		boolean subtract = false;
		    		if( lType[j] == Blocks.AIR ) {
		    			switch( airTreatment ) {
		    			case( AIR_IGNORE ):
		    				continue;
		    			case( AIR_NORMAL ):
		    				break;
		    			case( AIR_SUBTRACT ):
		    				subtract = true;
		    				break;
		    			}
		    		}
		    		if( Double.isNaN(lCeil[j]) ) {
		    			throw new RuntimeException("Ceiling height is NaN for layer "+l);
		    		}
		    		if( Double.isNaN(lFloor[j]) ) {
		    			throw new RuntimeException("Floor height is NaN for layer "+l);
		    		}
		    		if( lCeil[j] < highest[j] ) continue;
		    		if( lCeil[j] < lFloor[j] ) continue;
		    		
		    		if( subtract ) {
		    			if( highest[j] <= lCeil[j] && highest[j] >= lFloor[j] ) {
		    				highest[j] = lFloor[j];
		    			}
		    		} else {
			    		outT[j] = lType[j];
		    			highest[j] = lCeil[j];
		    		}
		    	}
		    }
		}
	}
	
	public ChunkMunger getChunkMunger() {
		return new LayerChunkMunger(layers);
	}
	
	public FunctionDaDa_DaIa getGroundFunction() {
		return new LayerGroundFunction(layers, LayerGroundFunction.AIR_SUBTRACT);
	}
}
