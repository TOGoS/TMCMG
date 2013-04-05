package togos.minecraft.mapgen.world.gen;

import togos.noise.v1.func.LFunctionDaDaDa_Ia;

public interface LayeredTerrainFunction
{
	static class LayerBuffer {
		public final int maxVectorSize;
		public final double[] floor, ceiling;
		public LFunctionDaDaDa_Ia materialFunction;
		
		public LayerBuffer( int maxVectorSize ) {
			assert maxVectorSize >= 0;
			this.maxVectorSize = maxVectorSize;
			this.floor = new double[maxVectorSize];
			this.ceiling = new double[maxVectorSize];
		}
	}
	
	static class TerrainBuffer {
		/**
		 * Returns a terrain buffer with at least the specified number of
		 * samples and layers.  If the provided buffer is non-null and large
		 * enough, it is returned.  Otherwise a new one is returned.
		 * The returned terrain buffer will have the given values of
		 * vectorSize and layerCount.
		 */
		public static TerrainBuffer getInstance( TerrainBuffer b, int vectorSize, int layerCount ) {
			if( b == null || vectorSize > b.maxVectorSize || layerCount > b.layerData.length ) {
				return new TerrainBuffer( vectorSize, layerCount );
			} else {
				b.vectorSize = vectorSize;
				b.layerCount = layerCount;
				return b;
			}
		}
		
		public final int maxVectorSize;
		public final LayerBuffer[] layerData;
		public final int[] biomeData;
		public int vectorSize, layerCount;
		
		public TerrainBuffer( final int maxVectorSize, final int maxLayerCount ) {
			assert maxVectorSize >= 0;
			this.maxVectorSize = maxVectorSize;
			this.vectorSize = maxVectorSize;
			this.layerCount = maxLayerCount;
			layerData = new LayerBuffer[maxLayerCount];
			for( int i=0; i<maxLayerCount; ++i ) {
				layerData[i] = new LayerBuffer(maxVectorSize);
			}
			biomeData = new int[maxVectorSize];
		}
	}
	
	/**
	 * Generate TerrainData for the given x,z coordinates into buffer, or into a new one if buffer
	 * doesn't have enough layers or if its vectorSize is < vectorSize.
	 * Return the TerrainData containing the generated data.
	 */
	public TerrainBuffer apply( int vectorSize, double[] x, double[] z, TerrainBuffer buffer );
}
