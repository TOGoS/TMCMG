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
import togos.noise2.data.DataDaDaDa;
import togos.noise2.data.DataDaIa;
import togos.noise2.function.FunctionDaDa_DaIa;

public class LayerTerrainGenerator implements WorldGenerator
{	
	public Map components = new HashMap();
	public List layers = new ArrayList();
	protected HeightmapLayer[] layerArray( List layers ) {
		HeightmapLayer[] larr = new HeightmapLayer[layers.size()];
		int n=0;
		for( Iterator i=layers.iterator(); i.hasNext(); ) {
			larr[n++] = (HeightmapLayer)i.next();
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
				Object o = li.next();
				if( o instanceof HeightmapLayer ) {
					HeightmapLayer layer = (HeightmapLayer)o;
					double[] ceiling = layer.ceilingHeightFunction.apply(in).v;
					double[] floor = layer.floorHeightFunction.apply(in).v;
					for( int i=0, tz=0; tz<cd.depth; ++tz ) {
						for( int tx=0; tx<cd.width; ++tx, ++i ) {
							int flo = (int)floor[i];
							if( flo < 0 ) flo = 0;
							int cei = (int)ceiling[i];
							if( cei > cd.height ) cei = cd.height;
							if( flo >= cei ) continue;
							
							int colHeight = cei-flo;
							double[] colX = new double[colHeight];
							double[] colY = new double[colHeight];
							double[] colZ = new double[colHeight];
							for( int j=0; j<colHeight; ++j ) {
								colX[j] = in.x[i];
								colY[j] = flo+j;
								colZ[j] = in.y[i];
							}
							DataDaDaDa finput = new DataDaDaDa(colX,colY,colZ);
							int[] colTypes = layer.typeFunction.apply(finput).v;
							
							for( int j=0, ty=flo; ty<cei; ++ty, ++j ) {
								int blockType = colTypes[j];
								if( blockType != Blocks.NONE ) {
									cd.setBlock(tx, ty, tz, (byte)blockType);
								}
							}
						}
					}
				} else {
					throw new RuntimeException("Don't know how to apply layer: "+o.getClass());
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
			int count = in.getLength();
			
	    	double[] highest = new double[in.getLength()];
	    	int[] outT = new int[count];
	    	for( int j=in.getLength()-1; j>=0; --j ) {
	    		highest[j] = Double.NEGATIVE_INFINITY;
	    		outT[j] = Blocks.AIR;
	    	}
		    for( Iterator li=layers.iterator(); li.hasNext(); ) {
		    	HeightmapLayer l = (HeightmapLayer)li.next();
		    	double[] lCeil = l.ceilingHeightFunction.apply(in).v;
		    	double[] lFloor = l.floorHeightFunction.apply(in).v;
				double[] lMid = new double[count];
				for( int i=count-1; i>=0; --i ) lMid[i] = (lFloor[i] + lCeil[i]) / 2; 
				DataDaDaDa typeInput = new DataDaDaDa(in.x,lMid,in.y);
				int[] lType = l.typeFunction.apply(typeInput).v;
		    	for( int j=in.getLength()-1; j>=0; --j ) {
		    		boolean subtract = false;
		    		if( lType[j] == Blocks.NONE ) {
		    			continue;
		    		}
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
