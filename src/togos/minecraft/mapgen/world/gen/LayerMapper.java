package togos.minecraft.mapgen.world.gen;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import togos.minecraft.mapgen.noise.api.FunctionDaDa_Da;
import togos.minecraft.mapgen.noise.api.FunctionDaDa_DaIa;
import togos.minecraft.mapgen.noise.api.FunctionDaDa_Ia;
import togos.minecraft.mapgen.world.Blocks;
import togos.minecraft.mapgen.world.structure.ChunkData;

public class LayerMapper
{
	public static class Layer {
		Material material;
		FunctionDaDa_Da floorHeightFunction;
		FunctionDaDa_Da ceilingHeightFunction;
		
		public Layer( Material material,
			FunctionDaDa_Da floorHeightFunction,
			FunctionDaDa_Da ceilingHeightFunction
		) {
			this.material = material;
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
	
	public static class LayerColorFunction implements FunctionDaDa_Ia {
		List layers;
		public LayerColorFunction( List layers ) {
			this.layers = layers;
		}
		
		protected int mix( int oldColor, int newColor ) {
			return newColor;
			/*
			if( (newColor & 0xFF000000) != 0 ) {
				return newColor;
			} else {
				return oldColor;
			}
			*/
		}
		
		public void apply( int count, double[] inX, double[] inY, int[] out ) {
	    	double[] lCeil = new double[count];
	    	double[] lFloor = new double[count];
	    	double[] highest = new double[count];
	    	for( int j=0; j<count; ++j ) {
	    		highest[j] = Double.NEGATIVE_INFINITY;
	    		out[j] = 0xFF000000;
	    	}
		    for( Iterator li=layers.iterator(); li.hasNext(); ) {
		    	Layer l = (Layer)li.next();
		    	l.ceilingHeightFunction.apply(count, inX, inY, lCeil);
		    	l.floorHeightFunction.apply(count, inX, inY, lFloor);
		    	for( int j=0; j<count; ++j ) {
		    		if( Double.isNaN(lCeil[j]) ) {
		    			throw new RuntimeException("Ceiling height is NaN for layer "+l);
		    		}
		    		if( Double.isNaN(lFloor[j]) ) {
		    			throw new RuntimeException("Floor height is NaN for layer "+l);
		    		}
		    		if( lCeil[j] < highest[j] ) continue;
		    		if( lCeil[j] < lFloor[j] ) continue;
		    		out[j] = mix(out[j],l.material.color);
		    		highest[j] = lCeil[j];
		    	}
		    }
		}
	}
	
	class LayerChunkMunger implements ChunkMunger {
		/*
		static final int CHUNK_WIDTH = 16;
		static final int CHUNK_DEPTH = 16;
		static final int CHUNK_HEIGHT = 128;
		*/
		
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
			int i=0;
			for( int tx=0; tx<cd.width; ++tx ) {
				for( int tz=0; tz<cd.depth; ++tz ) {
					// See diagram in ChunkData to see why
					// z = x and x = -z
					z[i] = cwx+tx;
					x[i] = -cwz-tz;
					++i;
				}
			}
			for( Iterator li=layers.iterator(); li.hasNext();  ) {
				i=0;
				Layer l = (Layer)li.next();
				// mix up z and y:
				l.ceilingHeightFunction.apply(z.length, x, z, ceiling);
				l.floorHeightFunction.apply(z.length, x, z, floor);
				for( int tx=0; tx<cd.width; ++tx ) {
					for( int tz=0; tz<cd.depth; ++tz, ++i ) {
						double flo = floor[i];
						if( flo < 0 ) flo = 0;
						double cei = ceiling[i];
						if( cei > cd.height ) cei = cd.height; 
						for( int ty=(int)flo; ty<cei; ++ty ) {
							cd.setBlock(tx, ty, tz, (byte)l.material.blockNum);
						}
					}
				}
			}
		}
	}
	
	// TODO: should share everything but material.color with color function
	public static class LayerGroundFunction implements FunctionDaDa_DaIa {
		List layers;
		public LayerGroundFunction( List layers ) {
			this.layers = layers;
		}
		
		public void apply( int count, double[] inX, double[] inY, double[] outZ, int[] outT ) {
	    	double[] lCeil = new double[count];
	    	double[] lFloor = new double[count];
	    	double[] highest = outZ;
	    	for( int j=0; j<count; ++j ) {
	    		highest[j] = Double.NEGATIVE_INFINITY;
	    		outT[j] = Blocks.AIR;
	    	}
		    for( Iterator li=layers.iterator(); li.hasNext(); ) {
		    	Layer l = (Layer)li.next();
		    	l.ceilingHeightFunction.apply(count, inX, inY, lCeil);
		    	l.floorHeightFunction.apply(count, inX, inY, lFloor);
		    	for( int j=0; j<count; ++j ) {
		    		if( Double.isNaN(lCeil[j]) ) {
		    			throw new RuntimeException("Ceiling height is NaN for layer "+l);
		    		}
		    		if( Double.isNaN(lFloor[j]) ) {
		    			throw new RuntimeException("Floor height is NaN for layer "+l);
		    		}
		    		if( lCeil[j] < highest[j] ) continue;
		    		if( lCeil[j] < lFloor[j] ) continue;
		    		
		    		outT[j] = l.material.blockNum;
		    		highest[j] = lCeil[j];
		    	}
		    }
		}
	}
	
	public LayerColorFunction getLayerColorFunction() {
		return new LayerColorFunction(layers);
	}
	
	public LayerChunkMunger getLayerChunkMunger() {
		return new LayerChunkMunger(layers);
	}
	
	public FunctionDaDa_DaIa getGroundFunction() {
		return new LayerGroundFunction(layers);
	}
}
