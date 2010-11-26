package togos.minecraft.mapgen.world.gen;

import java.util.Random;

import togos.minecraft.mapgen.world.Blocks;
import togos.minecraft.mapgen.world.structure.Stamp;

public class TreeGenerator
{
	public Stamp generate( int seed ) {
		Random r = new Random(seed);
		Stamp s = new Stamp( 7, 14, 7, 7, 0, 7 );
		for( int y=0; y<8; ++y ) {
			s.setBlock(3,y,3, Blocks.LOG);
		}
		for( int i=0; i<500; ++i ) {
			int x = r.nextInt(7);
			int y = 3+r.nextInt(14-3);
			int z = r.nextInt(7);
			s.setBlock(x,y,z, Blocks.LEAVES);
		}
		return s;
	}
}
