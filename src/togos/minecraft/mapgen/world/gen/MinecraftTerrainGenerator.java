package togos.minecraft.mapgen.world.gen;

import togos.noise.v1.func.LFunctionDaDaDa_Da;

public interface MinecraftTerrainGenerator
{
	static class LayerBuffer {
		public final int vectorSize;
		public final double[] floor, ceiling;
		public boolean materialIsConstant;
		public int constantMaterial;
		public LFunctionDaDaDa_Da materialFunction;
		
		public LayerBuffer( int vectorSize ) {
			assert vectorSize >= 0;
			this.vectorSize = vectorSize;
			this.floor = new double[vectorSize];
			this.ceiling = new double[vectorSize];
		}
	}
	
	static class TerrainBuffer {
		public final int vectorSize;
		public final LayerBuffer[] layerData;
		public final int[] biomeData;
		public int layerCount;
		
		public TerrainBuffer( final int vectorSize, final int maxLayerCount ) {
			this.vectorSize = vectorSize;
			layerData = new LayerBuffer[maxLayerCount];
			for( int i=0; i<maxLayerCount; ++i ) {
				layerData[i] = new LayerBuffer(vectorSize);
			}
			biomeData = new int[vectorSize];
		}
	}
	
	/**
	 * Generate TerrainData for the given x,z coordinates into buffer, or into a new one if buffer
	 * doesn't have enough layers or if its vectorSize is < vectorSize.
	 * Return the TerrainData containing the generated data.
	 */
	public TerrainBuffer generate( double[] x, double[] z, int vectorSize, TerrainBuffer buffer );
}
