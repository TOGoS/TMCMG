package togos.minecraft.mapgen;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import org.jnbt.CompoundTag;
import org.jnbt.NBTOutputStream;

import togos.minecraft.mapgen.world.ChunkUtil;
import togos.minecraft.mapgen.world.structure.ChestData;
import togos.minecraft.mapgen.world.structure.ChunkData;
import togos.minecraft.mapgen.world.structure.InventoryItemData;

public class MapWriter
{
	public int tmod( int num, int modby ) {
		if( num < 0 ) {
			num = -num;
			num %= modby;
			num = modby - num;
			return num;
		} else {
			return num % modby;
		}
	}
	
	public String chunkPath( int x, int z ) {
		return Integer.toString( tmod(x,64), 36 ) + "/" +
			Integer.toString( tmod(z,64), 36 ) + "/" +
			"c." + Integer.toString(x,36) + "." + Integer.toString(z,36) + ".dat";
	}
	
	public void writeChunkToFile( ChunkData cd, String baseDir ) throws IOException {
		String fullPath = baseDir + "/" + chunkPath( cd.x, cd.z );
		File f = new File(fullPath);
		File dir = f.getParentFile();
		if( dir != null && !dir.exists() ) dir.mkdirs();
		FileOutputStream os = new FileOutputStream(f);
		try {
			NBTOutputStream nbtos = new NBTOutputStream(os);
			
			HashMap levelRootTags = new HashMap();
			levelRootTags.put("Level",cd.toTag());
			CompoundTag fileRootTag = new CompoundTag("",levelRootTags);
			nbtos.writeTag(fileRootTag);
			nbtos.close();
		} finally {
			os.close();
		}
	}
	
	public static void main(String[] args) {
		try {
			ChunkData cd = new ChunkData();
			for( int z=0; z<16; ++z ) {
				for( int x=0; x<16; ++x ) {
					for( int y=0; y<128; ++y ) {
						if( y < 64+x+z ) {
							cd.setBlock(x, y, z, (byte)1, (byte)0 );
						} else {
							cd.setBlock(x, y, z, (byte)0, (byte)0 );
						}
					}
				}
			}
			InventoryItemData item = new InventoryItemData();
			item.itemTypeId = 278;
			item.count = 64;
			ChestData chest = new ChestData();
			chest.x = 2;
			chest.y = 66;
			chest.z = 0;
			//chest.inventoryItems.add( item );
			cd.tileEntityData.add( chest );
			ChunkUtil.calculateLighting(cd, 15);
			System.out.println(cd.toTag());
			new MapWriter().writeChunkToFile(cd, ".");
		} catch( IOException e ) {
			throw new RuntimeException(e);
		}
	}
}

