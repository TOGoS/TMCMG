package togos.minecraft.mapgen.world.gen;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import togos.minecraft.mapgen.noise.api.FunctionDaDaDa_Da;
import togos.minecraft.mapgen.noise.api.FunctionDaDa_Ia;
import togos.minecraft.mapgen.world.structure.ChunkData;

public class LayerMapper
{
	public static class Layer {
		Material material;
		FunctionDaDaDa_Da floorHeightFunction;
		FunctionDaDaDa_Da ceilingHeightFunction;
		
		public Layer( Material material,
			FunctionDaDaDa_Da floorHeightFunction,
			FunctionDaDaDa_Da ceilingHeightFunction
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
		Layer[] layers;
		public LayerColorFunction( Layer[] layers ) {
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
	    	double[] inZ = new double[count];
	    	double[] highest = new double[count];
	    	for( int j=0; j<count; ++j ) {
	    		highest[j] = Double.NEGATIVE_INFINITY;
	    		out[j] = 0xFF000000;
	    	}
		    for( int i=0; i<layers.length; ++i ) {
		    	Layer l = layers[i];
		    	l.ceilingHeightFunction.apply(count, inX, inY, inZ, lCeil);
		    	l.floorHeightFunction.apply(count, inX, inY, inZ, lFloor);
		    	for( int j=0; j<count; ++j ) {
		    		if( Double.isNaN(lCeil[j]) ) {
		    			throw new RuntimeException("Ceiling height is NaN for layer "+i);
		    		}
		    		if( Double.isNaN(lFloor[j]) ) {
		    			throw new RuntimeException("Floor height is NaN for layer "+i);
		    		}
		    		if( lCeil[j] < highest[j] ) continue;
		    		if( lCeil[j] < lFloor[j] ) continue;
		    		out[j] = mix(out[j],l.material.color);
		    		highest[j] = lCeil[j];
		    	}
		    }
		}
	}
	
	class LayerChunkFunction implements ChunkFunction {
		/*
		static final int CHUNK_WIDTH = 16;
		static final int CHUNK_DEPTH = 16;
		static final int CHUNK_HEIGHT = 128;
		*/
		
		protected Layer[] layers;
		
		public LayerChunkFunction( Layer[] layers ) {
			this.layers = layers;
		}
		
		public ChunkData getChunk( int cx, int cz ) {
			ChunkData cd = new ChunkData( cx, cz );
			int cwx = cx*cd.width;
			int cwz = cz*cd.depth;
			int count = cd.width*cd.depth;
			double[] x = new double[count];
			double[] z = new double[count];
			double[] y = new double[count]; // zero!
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
			for( int li=0; li<layers.length; ++li ) {
				i=0;
				Layer l = layers[li];
				// mix up z and y:
				l.ceilingHeightFunction.apply(z.length, x, z, y, ceiling);
				l.floorHeightFunction.apply(z.length, x, z, y, floor);
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
			return cd;
		}
	}
	
	public LayerColorFunction getLayerColorFunction() {
		return new LayerColorFunction(layerArray(layers));
	}
	
	public LayerChunkFunction getLayerChunkFunction() {
		return new LayerChunkFunction(layerArray(layers));
	}
}
