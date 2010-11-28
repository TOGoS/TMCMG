package togos.minecraft.mapgen.world.gen;

import java.util.Random;

import togos.minecraft.mapgen.world.Blocks;
import togos.minecraft.mapgen.world.structure.Stamp;
import togos.noise2.function.AddOutDaDaDa_Da;
import togos.noise2.function.DistanceDaDaDa_Da;
import togos.noise2.function.FunctionDaDaDa_Da;
import togos.noise2.function.PerlinDaDaDa_Da;
import togos.noise2.function.ScaleInDaDaDa_Da;
import togos.noise2.function.ScaleOutDaDaDa_Da;
import togos.noise2.function.TranslateInDaDaDa_Da;

public class TreeGenerator implements StampGenerator
{
	int minHeight = 3;
	int maxHeight = 7;
	
	public Stamp generateStamp( int seed ) {
		Random r = new Random(seed);
		int trunkHeight = minHeight+r.nextInt(maxHeight-minHeight);
		int girth = trunkHeight/2;
		
		int w = girth*2+1;
		int h = trunkHeight*2;
		int d = girth*2+1;
		
		FunctionDaDaDa_Da leafDensityFunction = new AddOutDaDaDa_Da(new FunctionDaDaDa_Da[]{
			new ScaleOutDaDaDa_Da( 0.5,
				new TranslateInDaDaDa_Da(r.nextDouble()*10, r.nextDouble()*10, r.nextDouble()*10,
					new ScaleInDaDaDa_Da(0.2, 0.2, 0.2, new PerlinDaDaDa_Da()))),
			new TranslateInDaDaDa_Da(-w/2d, -trunkHeight, -d/2d,
				new ScaleInDaDaDa_Da(1d/2.5, 2d/(1d*trunkHeight), 1d/2.5,
					new DistanceDaDaDa_Da()))
		});
		
		int volume = w*h*d;
		
		Stamp s = new Stamp( w, h, d, w/2, 0, d/2 );

		double[] x = new double[volume];
		double[] y = new double[volume];
		double[] z = new double[volume];
		int i=0;
		for( int iz=0; iz<d; ++iz ) {
			for( int ix=0; ix<w; ++ix ) {
				for( int iy=0; iy<h; ++iy, ++i ) {
					x[i] = ix+0.5;
					y[i] = iy+0.5;
					z[i] = iz+0.5;
				}
			}
		}
		double[] leafDensity = new double[volume];
		leafDensityFunction.apply(volume, x, y, z, leafDensity);
		for( i=0; i<volume; ++i ) {
			if( leafDensity[i] < 1 ) {
				s.setBlock((int)x[i],(int)y[i],(int)z[i], Blocks.LEAVES);
			}
		}
		for( int ty=0; ty<trunkHeight; ++ty ) {
			s.setBlock(w/2,ty,d/2, Blocks.LOG);
		}
		return s;
	}
}
