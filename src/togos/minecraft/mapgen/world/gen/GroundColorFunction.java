package togos.minecraft.mapgen.world.gen;

import togos.noise.v1.func.LFunctionDaDa_DaIa;
import togos.noise.v1.func.LFunctionIa_Ia;

public class GroundColorFunction implements LFunctionDaDa_DaIa
{
	protected final LFunctionDaDa_DaIa groundFunction;
	protected final LFunctionIa_Ia colorMap;
	public double heightShadeOrigin = 64;
	public double heightShadeAmount = 1.0/128;
	public boolean heightShadingEnabled = true;
	
	public GroundColorFunction( LFunctionDaDa_DaIa groundFunction, LFunctionIa_Ia colorMap ) {
		this.groundFunction = groundFunction;
		this.colorMap = colorMap;
	}
	
	//// Handy color-manipulation functions ////
	
	protected static final int clampByte( int component ) {
		if( component < 0 ) return 0;
		if( component > 255 ) return 255;
		return component;
	}
	
	protected static final int color( int r, int g, int b ) {
		return 0xFF000000 |
			(clampByte(r) << 16) |
			(clampByte(g) <<  8) |
			(clampByte(b) <<  0);
	}
	
	protected static final int component( int color, int shift ) {
		return (color >> shift) & 0xFF;
	}
	
	protected static final int shade( int color, int amt ) {
		return color(
			component( color, 16 ) + amt,
			component( color,  8 ) + amt,
			component( color,  0 ) + amt
		);
	}
	
	protected static final int blend( int color1, int color2 ) {
		return color(
			(component(color1,16) + component(color2,16)) >> 1,
			(component(color1, 8) + component(color2, 8)) >> 1,
			(component(color1, 0) + component(color2, 0)) >> 1
		);
	}
	
	////
	
	public void apply( int vectorSize, double[] x, double[] y, double[] height, int[] color ) {
		groundFunction.apply( vectorSize, x, y, height, color );
		colorMap.apply( vectorSize, color, color );
		for( int i=vectorSize-1; i>=0; --i ) {
			int col = color[i];
			if( heightShadingEnabled ) {
				col = shade(col,(int)((height[i]-heightShadeOrigin)*heightShadeAmount*255));
			}
			color[i] = col;
		}
	}
}
