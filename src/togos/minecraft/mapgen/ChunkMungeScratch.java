package togos.minecraft.mapgen;

import togos.minecraft.mapgen.world.Materials;
import togos.minecraft.mapgen.world.gen.LayeredTerrainFunction.TerrainBuffer;
import togos.minecraft.mapgen.world.structure.ChunkData;
import togos.noise.v3.vector.function.LFunctionDaDaDa_Ia;
import togos.noise.v3.vector.util.HasMaxVectorSize;

class ChunkMungeScratch implements HasMaxVectorSize
{
	public final int columnVectorSize;
	public final int vectorSize;
	public final double[] x;
	public final double[] z;
	public final double[] floor;
	public final double[] ceiling;
	public final TerrainBuffer terrainBuffer;
	public final double[] columnX;
	public final double[] columnY;
	public final double[] columnZ;
	public final int[] columnMaterial;
	
	public ChunkMungeScratch( int vectorSize, int columnVectorSize, int layerDefCount ) {
		this.vectorSize = vectorSize;
		this.columnVectorSize = columnVectorSize;
		this.x = new double[vectorSize];
		this.z = new double[vectorSize];
		this.floor = new double[vectorSize];
		this.ceiling = new double[vectorSize];
		this.terrainBuffer = new TerrainBuffer( vectorSize, layerDefCount );
		this.columnX = new double[columnVectorSize];
		this.columnY = new double[columnVectorSize];
		this.columnZ = new double[columnVectorSize];
		this.columnMaterial = new int[columnVectorSize];
	}
	
	public int getMaxVectorSize() { return vectorSize; }
	
	public void initXZRect( long px, long pz, int w, int d ) {
		for( int i=0, z=0; z<d; ++z ) {
			for( int x=0; x<w; ++x, ++i ) {
				this.x[i] = px + x + 0.5;
				this.z[i] = pz + z + 0.5;
			}
		}
	}
	
	protected int clampHeight( int h ) {
		return h < 0 ? 0 : h > columnVectorSize ? columnVectorSize : h;
	}
	
	/**
	 * Calculate values for typeFunction at x, y, z for y in [floor,ceiling) + 0.5
	 * into the first (ceiling-floor) slots in columnMaterial
	 */
	public void calculateColumnMaterials( LFunctionDaDaDa_Ia typeFunction, int floor, int ceiling, double x, double z ) {
		floor = clampHeight(floor);
		ceiling = clampHeight(ceiling);
		
		int columnHeight = ceiling - floor;
		if( columnHeight <= 0 ) return;
		
		for( int j=0, y=floor; y<ceiling; ++y, ++j ) {
			columnX[j] = x;
			columnY[j] = y + 0.5;
			columnZ[j] = z;
		}
		
		typeFunction.apply(
			columnHeight, columnX, columnY, columnZ, columnMaterial
		);
    }
	
	public void generateAndWriteColumn( LFunctionDaDaDa_Ia typeFunction, int floor, int ceiling, ChunkData cd, int cx, int cz ) {
		floor = clampHeight(floor);
		ceiling = clampHeight(ceiling);
		
		calculateColumnMaterials( typeFunction, floor, ceiling, cd.posX + cx + 0.5, cd.posZ + cz + 0.5 );
		
		for( int j=0, y=floor; y<ceiling; ++y, ++j ) {
			if( columnMaterial[j] != Materials.NONE ) {
				cd.setBlock( cx, y, cz, columnMaterial[j] );
			}
		}
    }
}
