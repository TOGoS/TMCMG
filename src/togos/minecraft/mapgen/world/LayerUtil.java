package togos.minecraft.mapgen.world;

public class LayerUtil
{
	public static final int[] roundHeights( double[] values ) {
		int[] h = new int[values.length];
		for( int i=0; i<values.length; ++i ) {
			h[i] = (int)Math.round(values[i]);
		}
		return h;
	}
	
	public static final double[] maxY( int[] topHeight ) {
		double[] maxY = new double[topHeight.length];
		for( int i=0; i<topHeight.length; ++i ) {
			maxY[i] = topHeight[i]-1;
		}
		return maxY;
	}
	
	public static final double[] maxY( double[] topHeight ) {
		return maxY( roundHeights(topHeight) );
	}
}
