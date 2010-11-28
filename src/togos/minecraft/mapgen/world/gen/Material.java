package togos.minecraft.mapgen.world.gen;

import java.lang.reflect.Field;
import java.util.HashMap;

import togos.minecraft.mapgen.world.Blocks;

public class Material {
	public int color;
	public byte blockNum;
	public byte blockExtraBits;
	
	public Material( int color, byte blockType ) {
		this.color = color;
		this.blockNum = blockType;
	}
	
	public Material( int color, byte blockType, byte blockExtraBits ) {
		this.color = color;
		this.blockNum = blockType;
		this.blockExtraBits = blockExtraBits;
	}
	
	protected static HashMap byName = new HashMap();
	protected static Material[] byBlockType = new Material[256];
	static void add( String name, int color, byte blockType, byte blockExtraBits ) {
		Material m = new Material( color, blockType, blockExtraBits );
		byBlockType[blockType] = m;
		byName.put(name, m);
	}

	static {
		byBlockType[Blocks.AIR              ] = new Material( 0x00000000, Blocks.AIR );
		byBlockType[Blocks.SAND             ] = new Material( 0xFF888844, Blocks.SAND );
		byBlockType[Blocks.STONE            ] = new Material( 0xFF888888, Blocks.STONE );
		byBlockType[Blocks.DIRT             ] = new Material( 0xFF664400, Blocks.DIRT );
		byBlockType[Blocks.GRASS            ] = new Material( 0xFF00AA00, Blocks.GRASS );
		byBlockType[Blocks.WATER            ] = new Material( 0xFF000055, Blocks.WATER );
		byBlockType[Blocks.WORKBENCH        ] = new Material( 0xFF000055, Blocks.WORKBENCH );
		byBlockType[Blocks.BEDROCK          ] = new Material( 0xFF333333, Blocks.BEDROCK );
		byBlockType[Blocks.COBBLESTONE      ] = new Material( 0xFF666666, Blocks.COBBLESTONE );
		byBlockType[Blocks.MOSSY_COBBLESTONE] = new Material( 0xFF558855, Blocks.MOSSY_COBBLESTONE );
	}
	
	static {
		Field[] fields = Blocks.class.getDeclaredFields();
		for( int i=0; i<fields.length; ++i ) {
			//add( fields[i].getName(), 0, (byte)fields[i].getInt(null), (byte)0 );
		}
	}
	
	public static Material forBlockType(int btId) {
		if( btId < 0 ) btId = 0;
		if( btId >= byBlockType.length ) btId = byBlockType.length-1;
		if( byBlockType[btId] == null ) {
			byBlockType[btId] = new Material( 0xFFFF00FF, (byte)btId );
		}
		return byBlockType[btId];
	}
}