package togos.minecraft.mapgen.world.gen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import togos.minecraft.mapgen.MaterialColumnFunction;
import togos.minecraft.mapgen.world.Blocks;
import togos.minecraft.mapgen.world.ChunkUtil;
import togos.minecraft.mapgen.world.LayerUtil;
import togos.minecraft.mapgen.world.Material;
import togos.minecraft.mapgen.world.Materials;
import togos.minecraft.mapgen.world.structure.ChunkData;
import togos.noise.v1.data.DataDaDa;
import togos.noise.v1.data.DataDaDaDa;
import togos.noise.v1.data.DataDaIa;
import togos.noise.v1.data.DataIa;
import togos.noise.v1.func.FunctionDaDa_DaIa;

// TODO: Implement MinecraftTerrainGenerator, instead
public class LayerTerrainGenerator implements WorldGenerator
{	
	public final HashMap<String,Object> components = new HashMap<String,Object>();
	public final ArrayList<HeightmapLayer> layers = new ArrayList<HeightmapLayer>();
	
	protected HeightmapLayer[] layerArray( List<HeightmapLayer> layers ) {
		HeightmapLayer[] larr = new HeightmapLayer[layers.size()];
		int n=0;
		for( HeightmapLayer l : layers ) larr[n++] = l;
		return larr;
	}
	
	class LayerChunkMunger implements ChunkMunger {
		protected List<HeightmapLayer> layers;
		
		public LayerChunkMunger( List<HeightmapLayer> layers ) {
			this.layers = layers;
		}
		
		public void mungeChunk( ChunkData cd ) {
			DataDaDa in = ChunkUtil.getTileXZCoordinates( cd );
			for( HeightmapLayer layer : layers ) {
				int[] ceiling = LayerUtil.roundHeights(layer.ceilingHeightFunction.apply(in).x);
				int[] floor   = LayerUtil.roundHeights(layer.floorHeightFunction.apply(in).x);
				for( int i=0, tz=0; tz<cd.depth; ++tz ) {
					for( int tx=0; tx<cd.width; ++tx, ++i ) {
						int flo = floor[i];
						if( flo < 0 ) flo = 0;
						int cei = ceiling[i];
						if( cei > cd.height ) cei = cd.height;
						if( cei <= flo ) continue;
						
						int colHeight = cei-flo;
						double[] colX = new double[colHeight];
						double[] colY = new double[colHeight];
						double[] colZ = new double[colHeight];
						for( int j=0; j<colHeight; ++j ) {
							colX[j] = in.x[i];
							colY[j] = flo+j;
							colZ[j] = in.y[i];
						}
						DataDaDaDa finput = new DataDaDaDa(colHeight,colX,colY,colZ);
						int[] colTypes = layer.typeFunction.apply(finput).v;
						
						for( int j=0, ty=flo; ty<cei; ++ty, ++j ) {
							int blockType = colTypes[j];
							if( blockType != Blocks.NONE ) {
								// Temporary solution for extra bits; see note in MaterialDaDaDa_Ia:
								cd.setBlock(tx, ty, tz, (byte)blockType, (byte)(blockType>>16));
							}
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
		
		public List<HeightmapLayer> layers;
		public int airTreatment;
		public LayerGroundFunction( List<HeightmapLayer> layers, int airTreatment ) {
			this.layers = layers;
			this.airTreatment = airTreatment;
		}
		
		public DataDaIa apply( final DataDaDa in ) {
			final int vectorSize = in.getLength();
			
			double[] highest = new double[vectorSize];
			int[] outT = new int[vectorSize];
			for( int j=in.getLength()-1; j>=0; --j ) {
				highest[j] = Double.NEGATIVE_INFINITY;
				outT[j] = Blocks.AIR;
			}
			for( HeightmapLayer l : layers ) {
				double[] lCeil  = l.ceilingHeightFunction.apply(in).x;
				double[] lFloor = l.floorHeightFunction.apply(in).x;
				double[] lTopY = LayerUtil.maxY(lCeil);
				DataDaDaDa typeInput = new DataDaDaDa(vectorSize,in.x,lTopY,in.y);
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
					if( lCeil[j] <= lFloor[j] ) continue;
					
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
			return new DataDaIa( vectorSize, highest, outT );
		}
	}
	
	public static class LayerColumnFunction implements MaterialColumnFunction {
		List<HeightmapLayer> layers;
		public LayerColumnFunction( List<HeightmapLayer> layers ) {
			this.layers = layers;
		}
		
		public void apply( int xzCount, double[] x, double[] z, int yCount, double[] y, Material[] dest ) {
			DataDaDa in = new DataDaDa(xzCount,x,z);
			
			for( int i=0; i<xzCount*yCount; ++i ) {
				dest[i] = Materials.getByBlockType(Blocks.AIR);
			}
			
			for( HeightmapLayer l : layers ) {
				double[] lCeil  = l.ceilingHeightFunction.apply(in).x;
				double[] lFloor = l.floorHeightFunction.apply(in).x;
				
				for( int i=0; i<xzCount; ++i ) {
					int[] colIdx = new int[yCount];
					double[] colX = new double[yCount];
					double[] colY = new double[yCount];
					double[] colZ = new double[yCount];
					int colSize=0;
					for( int j=0; j<yCount; ++j ) {
						if( y[j] >= lFloor[i] && y[j] < lCeil[i] ) {
							colIdx[colSize] = j;
							colX[colSize] = x[i];
							colY[colSize] = y[j];
							colZ[colSize] = z[i];
							++colSize;
						}
					}
					DataIa type = l.typeFunction.apply( new DataDaDaDa(colSize,colX,colY,colZ) );
					for( int j=0; j<colSize; ++j ) {
						int t = type.v[j];
						if( t == -1 ) continue;
						int yIdx = colIdx[j];
						dest[yIdx+i*yCount] = Materials.getByBlockType(t);
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
	
	public MaterialColumnFunction getColumnFunction() {
		return new LayerColumnFunction(layers);
    }
	
	public Map<String,Object> getComponents() {
		return components;
	}
}
