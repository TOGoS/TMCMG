package togos.minecraft.mapgen;

import togos.minecraft.mapgen.world.Material;

public interface MaterialColumnFunction
{
	/**
	 * Result will be stored
	 * x0,y0,z0, x0,y1,z0, x0,y2,z0 ... x0,yM,z0 ... x1,y0,z0 ... x0,y0,z1 ... xN,yM,zN 
	 */
	public void apply( int xzCount, double[] x, double[] z, int yCount, double[] y, Material[] dest );
}
