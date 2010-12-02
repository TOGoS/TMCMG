package togos.minecraft.mapgen.world.gen;

import java.util.Random;

import togos.minecraft.mapgen.world.Blocks;
import togos.minecraft.mapgen.world.structure.Stamp;

public class PineTreeGenerator implements StampGenerator
{
	int minHeight = 7;
	int maxHeight = 14;
	
	public Stamp generateStamp( int seed ) {
		Random r = new Random(seed);
		double size = r.nextDouble();
		size = size * (1+r.nextDouble());
		int trunkHeight = minHeight+(int)(size*(maxHeight-minHeight));
		int leafRadius = 2+(int)(2*size);
		int leafBot = r.nextInt(trunkHeight/2);
		
		int w = leafRadius*2+1;
		int h = trunkHeight+1;
		int d = leafRadius*2+1;
		
		Stamp s = new Stamp( w, h, d, w/2, 0, d/2 );
		
		double rad = 1;
		double drad = 0.6;
		for( int y=trunkHeight; y>=0; --y ) {
			for( int x=0; x<w; ++x ) {
				for( int z=0; z<w; ++z ) {
					int dx = leafRadius-x;
					int dz = leafRadius-z;
					if( Math.sqrt(dx*dx+dz*dz)+(r.nextDouble()-0.5)*1.5 < rad ) {
						s.setBlock(x,y,z,Blocks.LEAVES);
					}
				}
			}
			rad += drad;
			if( rad > leafRadius ) {
				drad = -2.0;
				rad = leafRadius;
			}
			if( rad < 2*leafRadius/3 ) {
				if( y < leafBot ) break;
				drad = +0.6;
				rad = 2*leafRadius/3;
			}
		}
		for( int ty=0; ty<trunkHeight; ++ty ) {
			s.setBlock(w/2,ty,d/2, Blocks.LOG);
		}
		return s;
	}
}
