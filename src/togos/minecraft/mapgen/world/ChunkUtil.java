package togos.minecraft.mapgen.world;

import togos.minecraft.mapgen.world.structure.ChunkData;
import togos.minecraft.mapgen.world.structure.TileEntityData;

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
	
	public static void addTileEntity( TileEntityData ted, ChunkData cd ) {
		int blockX = ted.x % cd.width;
		int blockY = ted.y;
		int blockZ = ted.z % cd.depth;
		if( blockX < 0 || blockX >= cd.width || blockZ < 0 || blockZ >= cd.depth || blockY < 0 || blockY >= cd.height ) {
			throw new RuntimeException("TileEntity "+ted.x+","+ted.z+" out of bounds of chunk "+
				cd.x+","+cd.z );
		}
		cd.setBlock(blockX, ted.y, blockZ, ted.getBlockId());
		cd.tileEntityData.add(ted);
	}
}
