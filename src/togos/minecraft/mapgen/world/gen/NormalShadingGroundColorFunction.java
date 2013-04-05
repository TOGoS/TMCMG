package togos.minecraft.mapgen.world.gen;

import togos.noise.v1.func.LFunctionDaDa_DaIa;
import togos.noise.v1.func.LFunctionIa_Ia;

public class NormalShadingGroundColorFunction extends GroundColorFunction
{
	double dx, dy;
	public double normalShadeScale;
	public double normalShadeMax;
	
	/**
	 * 
	 * @param groundFunction
	 * @param dx sample x offset
	 * @param dy sample y offset
	 * @param amt How much to alter color per dh/dz
	 * @param max Maximum amount to change color by
	 */
	public NormalShadingGroundColorFunction( LFunctionDaDa_DaIa groundFunction, LFunctionIa_Ia colorMap, double dx, double dy, double amt, double max ) {
		super( groundFunction, colorMap );
		this.dx = dx;
		this.dy = dy;
		this.normalShadeScale = amt;
		this.normalShadeMax = max;
	}
		
	public void apply( int vectorSize, double[] x1, double[] y1, double[] height1, int[] color1 ) {
		double[] x2 = new double[vectorSize];
		double[] y2 = new double[vectorSize];
		double[] height2 = new double[vectorSize];
		int[] color2 = new int[vectorSize];
		
		for( int i=vectorSize-1; i>=0; --i ) {
			x2[i] = x1[i] + dx;
			y2[i] = y1[i] + dy;
		}
		
		groundFunction.apply( vectorSize, x1, y1, height1, color1 );
		groundFunction.apply( vectorSize, x2, y2, height2, color2 );
		
		colorMap.apply( vectorSize, color1, color1 );
		colorMap.apply( vectorSize, color2, color2 );
		
		for( int i=vectorSize-1; i>=0; --i ) {
			int col1 = color1[i];
			int col2 = color2[i];
			double dz = height2[i] - height1[i];
			double alter = dz*normalShadeScale;
			if( alter < -normalShadeMax ) alter = -normalShadeMax;
			else if( alter > +normalShadeMax ) alter = +normalShadeMax;
			
			int col = blend(col1,col2);
			col = shade(col,(int)(alter*255));
			if( heightShadingEnabled ) {
				double height = (height1[i] + height2[i]) / 2;
				col = shade(col,(int)((height-heightShadeOrigin)*heightShadeAmount*255));
			}
			color1[i] = col;
		}
	}
}
