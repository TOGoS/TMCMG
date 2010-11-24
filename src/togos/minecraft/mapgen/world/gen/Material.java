/**
 * 
 */
package togos.minecraft.mapgen.world.gen;

import togos.minecraft.mapgen.world.Blocks;

public class Material {
	public int color;
	public byte blockNum;
	public byte blockExtraBits;
	
	public Material( int color, byte blockNum ) {
		this.color = color;
		this.blockNum = blockNum;
	}
	
	public Material( int color, byte blockNum, byte blockExtraBits ) {
		this.color = color;
		this.blockNum = blockNum;
		this.blockExtraBits = blockExtraBits;
	}
	
	public static Material AIR = new Material( 0, Blocks.AIR );
	public static Material STONE = new Material( 0xFF888888, Blocks.STONE );
	public static Material DIRT = new Material( 0xFF664400, Blocks.DIRT );
	public static Material SAND = new Material( 0xFF888844, Blocks.SAND );
	public static Material GRASS = new Material( 0xFF00AA00, Blocks.GRASS );
	public static Material WATER = new Material( 0xFF000055, Blocks.WATER );
	public static Material WORKBENCH = new Material( 0xFF000055, Blocks.WORKBENCH );
	public static Material BEDROCK = new Material( 0xFF333333, Blocks.BEDROCK );
	public static Material COBBLESTONE = new Material( 0xFF666666, Blocks.COBBLESTONE );
	public static Material MOSSY_COBBLESTONE = new Material( 0xFF558855, Blocks.MOSSY_COBBLESTONE );
}