package togos.minecraft.mapgen.world;

import java.util.Iterator;

import togos.minecraft.mapgen.world.structure.ChunkData;
import togos.minecraft.mapgen.world.structure.MiniChunkData;
import togos.minecraft.mapgen.world.structure.Stamp;
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
					case(Blocks.AIR):
						groundHeight = y;
						break;
					case(Blocks.LEAVES):
						if( light > 13 ) light = 13;
						break;
					case(Blocks.WATER): case(Blocks.MOVING_WATER):
						if( light > 12 ) light = 12;
						--light;
						if( light < 0 ) light = 0;
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
	
	public static void addTileEntity( TileEntityData ted, MiniChunkData cd, boolean errorOnOutOfBounds ) {
		int blockX = ted.x-cd.width*cd.getChunkX();
		int blockY = ted.y;
		int blockZ = ted.z-cd.depth*cd.getChunkZ();
		if( blockX < 0 || blockX >= cd.width || blockZ < 0 || blockZ >= cd.depth || blockY < 0 || blockY >= cd.height ) {
			if( errorOnOutOfBounds ) {
				throw new RuntimeException("TileEntity "+ted.x+","+ted.z+" out of bounds of chunk "+
					cd.getChunkX()+","+cd.getChunkZ() );
			} else {
				return;
			}
		}
		cd.tileEntityData.add(ted);
	}
	
	public static void addTileEntity( TileEntityData ted, MiniChunkData cd ) {
		addTileEntity(ted, cd, true);
	}
	
	public static void addTileEntityAndBlock( TileEntityData ted, MiniChunkData cd ) {
		addTileEntity(ted, cd);
		int blockX = ted.x % cd.width;
		int blockZ = ted.z % cd.depth;
		cd.setBlock(blockX, ted.y, blockZ, ted.getBlockId());
	}
	
	public static void stamp( MiniChunkData dest, Stamp s, int sx, int sy, int sz ) {
		if( sx-s.originX+s.width < 0 ) return;
		if( sx-s.originX >= dest.width ) return;
		if( sy-s.originY+s.height < 0 ) return;
		if( sy-s.originY >= dest.height ) return;
		if( sz-s.originZ+s.depth < 0 ) return;
		if( sz-s.originZ >= dest.depth ) return;
		
		for( int z=0; z<s.depth; ++z ) {
			int dz = sz+z-s.originZ;
			if( dz < 0 || dz >= dest.depth ) continue;
			for( int x=0; x<s.width; ++x ) {
				int dx = sx+x-s.originX;
				if( dx < 0 || dx >= dest.width ) continue;
				for( int y=0; y<s.height; ++y ) {
					int dy = sy+y-s.originY;
					if( dy < 0 || dy >= dest.height ) continue;
					if( !s.getMask(x,y,z) ) continue;
					
					dest.setBlock(dx, dy, dz, s.getBlock(x,y,z), s.getBlockExtraBits(x,y,z));
				}
			}
		}
		for( Iterator tei=s.tileEntityData.iterator(); tei.hasNext(); ) {
			TileEntityData ted = (TileEntityData)tei.next();
			ted = ted.duplicate(
				ted.x + sx-s.originX - s.getChunkX()*s.width + dest.getChunkX()*dest.width,
				ted.y + sy-s.originY,
				ted.z + sz-s.originZ - s.getChunkZ()*s.depth + dest.getChunkZ()*dest.depth
			);
			addTileEntity(ted, dest, false);
		}
	}
}
