package togos.minecraft.mapgen;

public interface LFunctionDaDa_Da_Ia
{
	/**
	 * Result will be stored
	 * [ y0,xz0, y1,xz0, yN,xz0...
	 *   y0,xz1, y1,xz1, yN,xz1...   
	 *   y0,xzN, y1,xzN, yN,xzN ]
	 */
	public void apply( int xzCount, double[] x, double[] z, int yCount, double[] y, int[] color );
}
