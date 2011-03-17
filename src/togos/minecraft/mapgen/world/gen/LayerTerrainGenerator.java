package togos.minecraft.mapgen.world.gen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import togos.minecraft.mapgen.world.Blocks;
import togos.minecraft.mapgen.world.ChunkUtil;
import togos.minecraft.mapgen.world.structure.ChunkData;
import togos.noise2.data.DataDaDa;
import togos.noise2.data.DataDaIa;
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
	
	public Map components = new HashMap();
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
			DataDaDa in = ChunkUtil.getTileXZCoordinates( cd );
			for( Iterator li=layers.iterator(); li.hasNext();  ) {
				Layer l = (Layer)li.next();
				double[] ceiling = l.ceilingHeightFunction.apply(in).v;
				double[] floor = l.floorHeightFunction.apply(in).v;
				int[] type = l.typeFunction.apply(in).v;
				for( int i=0, tz=0; tz<cd.depth; ++tz ) {
					for( int tx=0; tx<cd.width; ++tx, ++i ) {
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
		
		public DataDaIa apply( DataDaDa in ) {
	    	double[] highest = new double[in.getLength()];
	    	int[] outT = new int[in.getLength()];
	    	for( int j=in.getLength()-1; j>=0; --j ) {
	    		highest[j] = Double.NEGATIVE_INFINITY;
	    		outT[j] = Blocks.AIR;
	    	}
		    for( Iterator li=layers.iterator(); li.hasNext(); ) {
		    	Layer l = (Layer)li.next();
		    	double[] lCeil = l.ceilingHeightFunction.apply(in).v;
		    	double[] lFloor = l.floorHeightFunction.apply(in).v;
		    	int[] lType = l.typeFunction.apply(in).v;
		    	for( int j=in.getLength()-1; j>=0; --j ) {
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
		    return new DataDaIa( highest, outT );
		}
	}
	
	public ChunkMunger getChunkMunger() {
		return new LayerChunkMunger(layers);
	}
	
	public FunctionDaDa_DaIa getGroundFunction() {
		return new LayerGroundFunction(layers, LayerGroundFunction.AIR_SUBTRACT);
	}
	
	public Map getComponents() {
		return components;
	}
}
