package togos.minecraft.mapgen.world.gen;

import java.util.Random;

import togos.minecraft.mapgen.world.Blocks;
import togos.minecraft.mapgen.world.structure.Stamp;
import togos.noise2.vm.dftree.data.DataDaDaDa;
import togos.noise2.vm.dftree.func.AddOutDaDaDa_Da;
import togos.noise2.vm.dftree.func.DistanceDaDaDa_Da;
import togos.noise2.vm.dftree.func.PerlinDaDaDa_Da;
import togos.noise2.vm.dftree.func.ScaleInDaDaDa_Da;
import togos.noise2.vm.dftree.func.ScaleOutDaDaDa_Da;
import togos.noise2.vm.dftree.func.TNLFunctionDaDaDa_Da;
import togos.noise2.vm.dftree.func.TranslateInDaDaDa_Da;

public class RoundTreeGenerator implements StampGenerator
{
	public int minHeight = 5;
	public int maxHeight = 9;
	public double girthRat = 0.5;
	
	public Stamp generateStamp( int seed ) {
		Random r = new Random(seed);
		r.nextInt(); r.nextInt(); r.nextInt();
		int trunkHeight = minHeight+r.nextInt(maxHeight-minHeight);
		int girth = (int)(trunkHeight*girthRat);
		
		int w = girth*2+3;
		int h = trunkHeight*2;
		int d = girth*2+3;
		
		TNLFunctionDaDaDa_Da leafDensityFunction = new AddOutDaDaDa_Da(new TNLFunctionDaDaDa_Da[]{
			new ScaleOutDaDaDa_Da( 0.5,
				new TranslateInDaDaDa_Da(r.nextDouble()*10, r.nextDouble()*10, r.nextDouble()*10,
					new ScaleInDaDaDa_Da(0.2, 0.2, 0.2, new PerlinDaDaDa_Da()))),
			new TranslateInDaDaDa_Da(-w/2d, -trunkHeight, -d/2d,
				new ScaleInDaDaDa_Da(1.0/girth, 2.0/(1d*trunkHeight), 1.0/girth,
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
		double[] leafDensity = leafDensityFunction.apply(new DataDaDaDa(volume,x,y,z)).x;
		for( i=0; i<volume; ++i ) {
			if( leafDensity[i] < 1 ) {
				s.setBlock((int)x[i],(int)y[i],(int)z[i], Blocks.LEAVES);
			}
		}
		/*
		for( int tz=0; tz<d; ++tz ) {
			for( int tx=0; tx<w; ++tx ) {
				s.setBlock(tx,0,tz,Blocks.COBBLESTONE);
			}
		}
		*/
		for( int ty=0; ty<trunkHeight; ++ty ) {
			s.setBlock(w/2,ty,d/2, Blocks.LOG);
		}
		return s;
	}
}
