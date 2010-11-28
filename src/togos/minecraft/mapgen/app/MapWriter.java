package togos.minecraft.mapgen.app;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import org.jnbt.CompoundTag;
import org.jnbt.NBTOutputStream;

import togos.minecraft.mapgen.world.Blocks;
import togos.minecraft.mapgen.world.ChunkUtil;
import togos.minecraft.mapgen.world.gen.ChunkMunger;
import togos.minecraft.mapgen.world.gen.SimpleWorldGenerator;
import togos.minecraft.mapgen.world.structure.ChestData;
import togos.minecraft.mapgen.world.structure.ChunkData;
import togos.minecraft.mapgen.world.structure.InventoryItemData;
import togos.minecraft.mapgen.world.structure.Stamp;

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
		String fullPath = baseDir + "/" + chunkPath( cd.getChunkX(), cd.getChunkZ() );
		File f = new File(fullPath);
		File dir = f.getParentFile();
		if( dir != null && !dir.exists() ) dir.mkdirs();
		FileOutputStream os = new FileOutputStream(f);
		try {
			NBTOutputStream nbtos = new NBTOutputStream(os);
			
			HashMap levelRootTags = new HashMap();
			levelRootTags.put("Level",cd.toTag());
			CompoundTag fileRootTag = new CompoundTag("",levelRootTags);
			
			// System.out.println(fileRootTag);
			
			nbtos.writeTag(fileRootTag);
			nbtos.close();
		} finally {
			os.close();
		}
		// System.err.println("Wrote "+fullPath);
	}
	
	
	public static String USAGE =
		"Usage: mapwriter [options]\n" +
		"\n" +
		"Options:\n" +
		"  -map-dir <dir>  ; directory under which to store chunk data\n" +
		"  -x, -z, -width, -depth  ; bounds of area to generate";
	
	public static void main(String[] args) {
		int boundsX = 0;
		int boundsZ = 0;
		int boundsWidth = 1;
		int boundsDepth = 1;
		String mapDir = ".";
		for( int i=0; i<args.length; ++i ) {
			if( "-map-dir".equals(args[i]) ) {
				mapDir = args[++i];
			} else if( "-x".equals(args[i]) ) {
				boundsX = Integer.parseInt(args[++i]);
			} else if( "-z".equals(args[i]) ) {
				boundsZ = Integer.parseInt(args[++i]);
			} else if( "-width".equals(args[i]) ) {
				boundsWidth = Integer.parseInt(args[++i]);
			} else if( "-depth".equals(args[i]) ) {
				boundsDepth = Integer.parseInt(args[++i]);
			} else {
				System.err.println("Unrecognised argument: "+args[i]);
				System.err.println(USAGE);
				System.exit(1);
			}
		}
		
		try {
			/*
			ChunkData cd = new ChunkData();
			for( int z=0; z<16; ++z ) {
				for( int x=0; x<16; ++x ) {
					for( int y=0; y<128; ++y ) {
						if( y < 64+x+z ) {
							cd.setBlock(x, y, z, (byte)17, (byte)0 );
						} else {
							cd.setBlock(x, y, z, (byte)0, (byte)0 );
						}
					}
				}
			}
			cd.x = 23; 
			cd.z = -6;
			ChestData chest = new ChestData();
			chest.x = cd.x*cd.width+0;
			chest.y = 64;
			chest.z = cd.z*cd.depth+0;
			chest.inventoryItems.add( new InventoryItemData( BlockIDs.DIAMOND_AXE, 10, 1) );
			chest.inventoryItems.add( new InventoryItemData( BlockIDs.DIAMOND_PICKAXE, 10, 2) );
			chest.inventoryItems.add( new InventoryItemData( BlockIDs.DIAMOND_SPADE, 10, 3) );
			chest.inventoryItems.add( new InventoryItemData( BlockIDs.DIAMOND_SWORD, 10, 4) );
			//cd.tileEntityData.add( chest );
			//cd.setBlock(0,64,0, (byte)54);
			ChunkUtil.addTileEntity(chest, cd);
			ChunkUtil.calculateLighting(cd, 15);
			new MapWriter().writeChunkToFile(cd, mapDir);
			*/
			
			Stamp s = new Stamp(8,64,8,4,0,4);
			
			/*
			{
				String diagram =
					"CC  CC  "+
					"C...... "+
					" ......C"+
					" ......C"+
					"C...... "+
					"C...... "+
					" ......C"+
					"  CC  CC"+
					
					"CCCCCCCC"+
					"C......C"+
					"C......C"+
					"C......C"+
					"C......C"+
					"C......C"+
					"C......C"+
					"CCCCCCCC"+
					
					"CCCCCCCC"+
					"CCCCCCCC"+
					"CCDDDSCC"+
					"CCSSDSCC"+
					"CCSDDDCC"+
					"CCSDSSCC"+
					"CCCCCCCC"+
					"CCCCCCCC";
				s.populate(0,59,0, 8, 3, 8, diagram);
			}
			for( int sy=54; sy<59; ++sy ) {
				String diagram =
					" CCCCCC "+
					"CCCCCCCC"+
					"CCCCCCCC"+
					"CCCCCCCC"+
					"CCCCCCCC"+
					"CCCCCCCC"+
					"CCCCCCCC"+
					" CCCCCC ";
				s.populate(0,sy,0, 8, 1, 8, diagram);
			}
			{
				String diagram =
					" XXXXXX "+
					"XCCCCCCX"+
					"XCCCCCCX"+
					"XCCCCCCX"+
					"XCCCCCCX"+
					"XCCCCCCX"+
					"XCCCCCCX"+
					" XXXXXX "+
					
					" XXXXXX "+
					"XCCCCCCX"+
					"XC....CX"+
					"XC....CX"+
					"XC....CX"+
					"XC....CX"+
					"XCCCCCCX"+
					" XXXXXX "+

					"XXXXXXXX"+
					"XCCCCCCX"+
					"XC....CX"+
					"XC....CX"+
					"XC....CX"+
					"XC....CX"+
					"XCCCCCCX"+
					"XXXXXXXX"+

					" XXXXXX "+
					"XCCCCCCX"+
					"XC....CX"+
					"XC....CX"+
					"XC....CX"+
					"XC....CX"+
					"XCCCCCCX"+
					" XXXXXX "+
					
					" XXXXXX "+
					"XCCCCCCX"+
					"XCCCCCCX"+
					"XCCCCCCX"+
					"XCCCCCCX"+
					"XCCCCCCX"+
					"XCCCCCCX"+
					" XXXXXX "+

					" XXXXXX "+
					"XXXXXXXX"+
					"XXXXXXXX"+
					"XXXXXXXX"+
					"XXXXXXXX"+
					"XXXXXXXX"+
					"XXXXXXXX"+
					" XXXXXX ";
				s.populate(0,48,0, 8, 6, 8, diagram);
			}
			for( int sy=0; sy<48; ++sy ) {
				String diagram =
					"XXXXXXXX"+
					"X######X"+
					"X######X"+
					"X######X"+
					"X######X"+
					"X######M"+
					"X######X"+
					"XXXXXXXX";
				s.populate(0,sy,0, 8, 1, 8, diagram);
			}
			{
				String diagram =
					"XXXXXXXX"+
					"X######X"+
					"X#C...#X"+
					"X#C...#X"+
					"X#C...#X"+
					"X#C...#X"+
					"X######X"+
					"XXXXXXXX"+
					
					"XXXXXXXX"+
					"X######X"+
					"X#t...#X"+
					"X#....#X"+
					"X#....#X"+
					"X#....#X"+
					"X######X"+
					"XXXXXXXX"+
					
					"XXXXXXXX"+
					"X######X"+
					"X#C...#X"+
					"X#....#X"+
					"X#w...#X"+
					"X#C...#X"+
					"X######X"+
					"XXXXXXXX"+
					
					"XXXXXXXX"+
					"X######X"+
					"X#MMMM#X"+
					"X#MMMM#X"+
					"X#MMMM#X"+
					"X#MMMM#X"+
					"X######X"+
					"XXXXXXXX"+
					
					"XXXXXXXX"+
					"X######X"+
					"X#XXXX#X"+
					"X#XXXX#X"+
					"X#XXXX#X"+
					"X#XXXX#X"+
					"X######X"+
					"XXXXXXXX"+
					
					"XXXXXXXXXXXX"+
					"XX#########X"+
					"XX##XXXXXX#X"+
					"XX##XXXXXX#X"+
					"XX##XXXXXX#X"+
					"XX##XXXXXXXX"+
					"XX#########X"+
					"XXXXXXXXXXXX"+
					
					"XXXXXXXXXXXXXXXX"+
					"XX############XX"+
					"XX##XXXXXXXX##XX"+
					"XX##XXXXXXXX##XX"+
					"XX##XXXXXXXX##XX"+
					"XX##XXXXXXXXXXXX"+
					"XX############XX"+
					"XXXXXXXXXXXXXXXX";
				s.populate(0,6,0, 8, 7, 8, diagram);
			}
			*/
			
			ChestData chest = new ChestData();
			chest.x = 2;
			chest.y = 10;
			chest.z = 3;
			chest.inventoryItems.add( new InventoryItemData( Blocks.DIAMOND_AXE, 10, 0) );
			chest.inventoryItems.add( new InventoryItemData( Blocks.DIAMOND_PICKAXE, 10, 1) );
			chest.inventoryItems.add( new InventoryItemData( Blocks.DIAMOND_SPADE, 10, 2) );
			chest.inventoryItems.add( new InventoryItemData( Blocks.DIAMOND_SWORD, 10, 3) );
			
			chest.inventoryItems.add( new InventoryItemData( Blocks.LOG, 64, 9) );
			chest.inventoryItems.add( new InventoryItemData( Blocks.COAL, 64, 10) );
			chest.inventoryItems.add( new InventoryItemData( Blocks.SAPLING, 64, 11) );
			chest.inventoryItems.add( new InventoryItemData( Blocks.GRILLED_PORK, 64, 12) );
			chest.inventoryItems.add( new InventoryItemData( Blocks.FLINT, 64, 13) );
			chest.inventoryItems.add( new InventoryItemData( Blocks.IRON_INGOT, 64, 14) );
			
			chest.inventoryItems.add( new InventoryItemData( Blocks.DIRT, 64, 18) );
			chest.inventoryItems.add( new InventoryItemData( Blocks.STONE, 64, 19) );
			chest.inventoryItems.add( new InventoryItemData( Blocks.COBBLESTONE, 64, 20) );
			chest.inventoryItems.add( new InventoryItemData( Blocks.OBSIDIAN, 64, 21) );
			ChunkUtil.addTileEntityAndBlock(chest, s);
			
			MapWriter mapWriter = new MapWriter();
			ChunkMunger cfunc = SimpleWorldGenerator.DEFAULT.getChunkMunger();
			for( int z=0; z<boundsDepth; ++z ) {
				for( int x=0; x<boundsWidth; ++x ) {
					ChunkData cd = new ChunkData(boundsX+x,boundsZ+z);
					cfunc.mungeChunk(cd);
					if( x == 0 ) {
						ChunkUtil.stamp(cd, s,  4, 32,  4);
						ChunkUtil.stamp(cd, s,  4, 32, 12);
					}
					if( x == boundsWidth-1 ) {
						ChunkUtil.stamp(cd, s, 12, 32,  4);
						ChunkUtil.stamp(cd, s, 12, 32, 12);
					}
					if( z == 0 ) {
						ChunkUtil.stamp(cd, s,  4, 32,  4);
						ChunkUtil.stamp(cd, s, 12, 32,  4);
					}
					if( z == boundsDepth-1 ) {
						ChunkUtil.stamp(cd, s,  4, 32, 12);
						ChunkUtil.stamp(cd, s, 12, 32, 12);
					}
					ChunkUtil.calculateLighting(cd, 15);
					mapWriter.writeChunkToFile(cd, mapDir);
				}
			}
		} catch( IOException e ) {
			throw new RuntimeException(e);
		}
	}
}

