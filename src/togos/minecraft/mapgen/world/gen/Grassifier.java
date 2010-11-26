package togos.minecraft.mapgen.world.gen;

import togos.minecraft.mapgen.world.Blocks;
import togos.minecraft.mapgen.world.structure.ChunkData;

public class Grassifier implements ChunkMunger
{
	public void mungeChunk( ChunkData cd ) {
		for( int z=0; z<cd.depth; ++z ) {
			for( int x=0; x<cd.width; ++x ) {
				yLoop: for( int y=cd.height-1; y>0; --y ) {
					switch( cd.getBlock(x, y, z) ) {
					case( Blocks.AIR ): continue yLoop;
					case( Blocks.LEAVES ): continue yLoop;
					case( Blocks.DIRT ):
						cd.setBlock(x, y, z, Blocks.GRASS);
					default:
						break yLoop;
					}
				}
			}
		}
	}

}
