package togos.minecraft.mapgen.world.gen;

import togos.minecraft.mapgen.world.Materials;
import togos.noise2.vm.dftree.data.DataDaDa;
import togos.noise2.vm.dftree.data.DataDaIa;
import togos.noise2.vm.dftree.data.DataIa;
import togos.noise2.vm.dftree.func.FunctionDaDa_DaIa;
import togos.noise2.vm.dftree.func.FunctionDaDa_Ia;

public class GroundColorFunction implements FunctionDaDa_Ia
{
	FunctionDaDa_DaIa groundFunction;
	public double heightShadeOrigin = 64;
	public double heightShadeAmount = 1.0/128;
	public boolean heightShadingEnabled = true;
	
	public GroundColorFunction( FunctionDaDa_DaIa groundFunction ) {
		this.groundFunction = groundFunction;
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
	
	public DataIa apply( final DataDaDa in ) {
		final int vectorSize = in.getLength();
		DataDaIa ground = groundFunction.apply(in);
		int[] type = ground.i;
		int[] color = new int[vectorSize];
		for( int i=0; i<color.length; ++i ) {
			int col = Materials.getByBlockType(type[i]).color;
			if( heightShadingEnabled ) {
				col = shade(col,(int)((ground.d[i]-heightShadeOrigin)*heightShadeAmount*255));
			}
			color[i] = col;
		}
		return new DataIa(vectorSize, color);
	}
}
