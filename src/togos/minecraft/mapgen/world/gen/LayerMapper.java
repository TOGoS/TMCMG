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
		FunctionDaDa_Ia typeFunction;
		FunctionDaDa_Da floorHeightFunction;
		FunctionDaDa_Da ceilingHeightFunction;
		
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
	
	public static class GroundColorFunction implements FunctionDaDa_Ia {
		FunctionDaDa_DaIa groundFunction;
		public GroundColorFunction( FunctionDaDa_DaIa groundFunction ) {
			this.groundFunction = groundFunction;
		}
		
		public void apply( int count, double[] inX, double[] inY, int[] out ) {
			double[] height = new double[count];
			groundFunction.apply(count, inX, inY, height, out);
			for( int i=0; i<count; ++i ) {
				out[i] = Material.forBlockType(out[i]).color;
			}
		}
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
	
	// TODO: should share everything but material.color with color function
	public static class LayerGroundFunction implements FunctionDaDa_DaIa {
		List layers;
		public LayerGroundFunction( List layers ) {
			this.layers = layers;
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
		    		if( Double.isNaN(lCeil[j]) ) {
		    			throw new RuntimeException("Ceiling height is NaN for layer "+l);
		    		}
		    		if( Double.isNaN(lFloor[j]) ) {
		    			throw new RuntimeException("Floor height is NaN for layer "+l);
		    		}
		    		if( lCeil[j] < highest[j] ) continue;
		    		if( lCeil[j] < lFloor[j] ) continue;
		    		
		    		outT[j] = lType[j];
		    		highest[j] = lCeil[j];
		    	}
		    }
		}
	}
	
	public GroundColorFunction getLayerColorFunction() {
		return new GroundColorFunction(getGroundFunction());
	}
	
	public LayerChunkMunger getLayerChunkMunger() {
		return new LayerChunkMunger(layers);
	}
	
	public FunctionDaDa_DaIa getGroundFunction() {
		return new LayerGroundFunction(layers);
	}
}
