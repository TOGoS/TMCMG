package togos.minecraft.mapgen.world.gen;

import togos.minecraft.mapgen.world.Materials;
import togos.noise2.data.DataDaDa;
import togos.noise2.data.DataDaIa;
import togos.noise2.data.DataIa;
import togos.noise2.function.FunctionDaDa_DaIa;
import togos.noise2.function.FunctionDaDa_Ia;

public class ShadingGroundColorFunction implements FunctionDaDa_Ia
{
	FunctionDaDa_DaIa groundFunction;
	double dx, dy, amt, max;
	
	/**
	 * 
	 * @param groundFunction
	 * @param dx sample x offset
	 * @param dy sample y offset
	 * @param amt How much to alter color per dh/dz
	 * @param max Maximum amount to change color by
	 */
	public ShadingGroundColorFunction( FunctionDaDa_DaIa groundFunction, double dx, double dy, double amt, double max ) {
		this.groundFunction = groundFunction;
		this.dx = dx;
		this.dy = dy;
		this.amt = amt;
		this.max = max;
	}
	
	protected int clampByte( int component ) {
		if( component < 0 ) return 0;
		if( component > 255 ) return 255;
		return component;
	}
	
	protected int color( int r, int g, int b ) {
		return 0xFF000000 |
			(clampByte(r) << 16) |
			(clampByte(g) <<  8) |
			(clampByte(b) <<  0);
	}
	
	protected int component( int color, int shift ) {
		return (color >> shift) & 0xFF;
	}
	
	protected int shade( int color, int amt ) {
		return color(
			component( color, 16 ) + amt,
			component( color,  8 ) + amt,
			component( color,  0 ) + amt
		);
	}
	
	protected int blend( int color1, int color2 ) {
		return color(
			(component(color1,16) + component(color2,16)) >> 1,
			(component(color1, 8) + component(color2, 8)) >> 1,
			(component(color1, 0) + component(color2, 0)) >> 1
		);
	}
	
	public DataIa apply( DataDaDa in ) {
		double[] sx = new double[in.getLength()];
		double[] sy = new double[in.getLength()];
		for( int i=in.getLength()-1; i>=0; --i ) {
			sx[i] = in.x[i] + dx;
			sy[i] = in.y[i] + dy;
		}

		DataDaIa ground = groundFunction.apply(in);
		DataDaDa sin = new DataDaDa(sx,sy);
		DataDaIa sground = groundFunction.apply(sin);
		
		int[] color = new int[ground.getLength()];
		
		for( int i=0; i<color.length; ++i ) {
			int color1 = Materials.getByBlockType(ground.i[i]).color;
			int color2 = Materials.getByBlockType(sground.i[i]).color;
			double dz = sground.d[i] - ground.d[i];
			double alter = dz*amt;
			if( alter < -max ) alter = -max;
			else if( alter > +max ) alter = +max;
			color[i] = shade(blend(color1,color2),(int)(alter*255));
		}
		return new DataIa(color);
	}
}
