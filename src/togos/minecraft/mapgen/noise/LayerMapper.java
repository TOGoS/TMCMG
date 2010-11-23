package togos.minecraft.mapgen.noise;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import togos.minecraft.mapgen.noise.api.FunctionDaDaDa_Da;
import togos.minecraft.mapgen.noise.api.FunctionDaDa_Ia;
import togos.minecraft.mapgen.world.BlockIDs;

public class LayerMapper
{
	public static class Material {
		public int color;
		public int blockId;
		
		public Material( int color, int blockId ) {
			this.color = color;
			this.blockId = blockId;
		}
		
		public static Material AIR = new Material( 0, BlockIDs.AIR );
		public static Material STONE = new Material( 0xFF888888, BlockIDs.STONE );
		public static Material DIRT = new Material( 0xFF664400, BlockIDs.DIRT );
		public static Material SAND = new Material( 0xFF888844, BlockIDs.SAND );
		public static Material GRASS = new Material( 0xFF00AA00, BlockIDs.GRASS );
		public static Material WATER = new Material( 0xFF000055, BlockIDs.WATER );
		public static Material BEDROCK = new Material( 0xFF333333, BlockIDs.BEDROCK );
	}
	
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
	
	public static class LayerColorFunction implements FunctionDaDa_Ia {
		Layer[] layers;
		public LayerColorFunction( List layers ) {
			this.layers = new Layer[layers.size()];
			int n=0;
			for( Iterator i=layers.iterator(); i.hasNext(); ) {
				this.layers[n++] = (Layer)i.next();
			}
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
	
	public LayerColorFunction getLayerColorFunction() {
		return new LayerColorFunction(layers);
	}
}
