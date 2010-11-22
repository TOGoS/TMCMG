package togos.minecraft.mapgen.world;

import togos.minecraft.mapgen.world.structure.ChunkData;

public class ChunkUtil
{
	public static void calculateLighting( ChunkData cd, int maxLight ) {
		for( int z=0; z<cd.depth; ++z ) {
			for( int x=0; x<cd.width; ++x ) {
				int y;
				int light = maxLight;
				int groundHeight = 127;
				for( y = 127; y>=0; --y ) {
					byte block = cd.getBlock(x,y,z);
					switch( block ) {
					case(0): case(8): case(9):
						groundHeight = y;
						break;
					default:
						light = 0;
					}
					cd.setSkyLight(x, y, z, light);
				}
				cd.setLightHeight(x, z, groundHeight);
			}
		}
	}
}
