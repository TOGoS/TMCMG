package togos.minecraft.mapgen.world.gen;

import togos.minecraft.mapgen.world.Blocks;
import togos.minecraft.mapgen.world.ChunkUtil;
import togos.minecraft.mapgen.world.structure.ChunkData;
import togos.noise2.function.FunctionDaDa_Da;

public class Winterizer implements ChunkMunger
{
	FunctionDaDa_Da winternessFunction;
	
	public Winterizer( FunctionDaDa_Da winternessFunction ) {
		this.winternessFunction = winternessFunction;
	}
	
	public void mungeChunk( ChunkData cd ) {
		final int count = cd.width*cd.depth;
		double[] tx = new double[count];
		double[] tz = new double[count];
		double[] winterness = new double[count];
		ChunkUtil.getTileXZCoordinates( cd, tx, tz );
		winternessFunction.apply(count,tx,tz,winterness);
		for( int i=0, z=0; z<cd.depth; ++z ) {
			for( int x=0; x<cd.width; ++x, ++i ) {
				if( winterness[i] <= 0 ) continue;
				
				yLoop: for( int y=cd.height-1; y>0; --y ) {
					switch( cd.getBlock(x, y, z) ) {
					case( Blocks.AIR ): continue yLoop;
					case( Blocks.WATER ):
						cd.setBlock(x, y, z, Blocks.ICE);
						break yLoop;
					default:
						cd.setBlock(x, y+1, z, Blocks.SNOW);
						break yLoop;
					}
				}
			}
		}
	}
}
