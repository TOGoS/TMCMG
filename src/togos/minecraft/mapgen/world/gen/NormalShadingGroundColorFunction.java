package togos.minecraft.mapgen.world.gen;

import togos.minecraft.mapgen.world.Materials;
import togos.noise2.vm.dftree.data.DataDaDa;
import togos.noise2.vm.dftree.data.DataDaIa;
import togos.noise2.vm.dftree.data.DataIa;
import togos.noise2.vm.dftree.func.FunctionDaDa_DaIa;

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
	public NormalShadingGroundColorFunction( FunctionDaDa_DaIa groundFunction, double dx, double dy, double amt, double max ) {
		super( groundFunction );
		this.dx = dx;
		this.dy = dy;
		this.normalShadeScale = amt;
		this.normalShadeMax = max;
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
			int col1 = Materials.getByBlockType(ground.i[i]).color;
			int col2 = Materials.getByBlockType(sground.i[i]).color;
			double dz = sground.d[i] - ground.d[i];
			double alter = dz*normalShadeScale;
			if( alter < -normalShadeMax ) alter = -normalShadeMax;
			else if( alter > +normalShadeMax ) alter = +normalShadeMax;
			
			int col = blend(col1,col2);
			col = shade(col,(int)(alter*255));
			if( heightShadingEnabled ) {
				double height = (ground.d[i] + sground.d[i]) / 2;
				col = shade(col,(int)((height-heightShadeOrigin)*heightShadeAmount*255));
			}
			color[i] = col;
		}
		return new DataIa(color);
	}
}
