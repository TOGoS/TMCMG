package togos.minecraft.mapgen.world;

import java.util.HashMap;

import togos.minecraft.mapgen.world.gen.Material;

public class Materials
{
	static Material[] byBlockType = new Material[128];
	static HashMap byName = new HashMap();
	static HashMap byIcon = new HashMap();
	
	static String normalizeName(String name) {
		name = name.replace("state", "");
		name = name.replace(" ", "");
		name = name.replace("'", "");
		name = name.replace("-", "");
		name = name.replace("\"", "");
		name = name.replace("(", "");
		name = name.replace(")", "");
		return name;
	}
	
	static void add( Material m ) {
		byBlockType[m.blockType] = m;
		byName.put(normalizeName(m.name), m);
		byIcon.put(m.icon, m);
	}
	
	public static Material getByBlockType(int blockType) {
		if( blockType < 0 || blockType > 127 ) return null;
		return byBlockType[blockType];
	}
	
	public static Material getByName(String name) {
		return (Material)byName.get(normalizeName(name));
	}
	
	public static Material getByIcon(String icon) {
		return (Material)byIcon.get(icon);
	}
	
	static {
		add(new Material( (byte)0x00, (byte)0x00, 0x00000000, ". ", "Air" ));
		add(new Material( (byte)0x01, (byte)0x00, 0xFF888888, "XX", "Stone" ));
		add(new Material( (byte)0x02, (byte)0x00, 0xFF008800, "GD", "Grass" ));
		add(new Material( (byte)0x03, (byte)0x00, 0xFF884400, "DD", "Dirt" ));
		add(new Material( (byte)0x04, (byte)0x00, 0xFF666666, "CC", "Cobblestone" ));
		add(new Material( (byte)0x05, (byte)0x00, 0xFFFF00FF, "? ", "Wood" ));
		add(new Material( (byte)0x06, (byte)0x00, 0xFFFF00FF, "? ", "Sapling" ));
		add(new Material( (byte)0x07, (byte)0x00, 0xFF333333, "##", "Bedrock" ));
		add(new Material( (byte)0x08, (byte)0x00, 0xFFFF00FF, "? ", "Moving water" ));
		add(new Material( (byte)0x09, (byte)0x00, 0xFF000088, "WW", "Water" ));
		add(new Material( (byte)0x0A, (byte)0x00, 0xFFFF00FF, "? ", "Moving lava" ));
		add(new Material( (byte)0x0B, (byte)0x00, 0xFFBBAA00, "? ", "Lava" ));
		add(new Material( (byte)0x0C, (byte)0x00, 0xFFAAAA66, "SS", "Sand" ));
		add(new Material( (byte)0x0D, (byte)0x00, 0xFFFF00FF, "? ", "Gravel" ));
		add(new Material( (byte)0x0E, (byte)0x00, 0xFFFF00FF, "? ", "Gold ore" ));
		add(new Material( (byte)0x0F, (byte)0x00, 0xFFFF00FF, "? ", "Iron ore" ));
		add(new Material( (byte)0x10, (byte)0x00, 0xFFFF00FF, "? ", "Coal ore" ));
		add(new Material( (byte)0x11, (byte)0x00, 0xFFFF00FF, "? ", "Log" ));
		add(new Material( (byte)0x12, (byte)0x00, 0xFF006633, "? ", "Leaves" ));
		add(new Material( (byte)0x13, (byte)0x00, 0xFFFF00FF, "? ", "Sponge" ));
		add(new Material( (byte)0x14, (byte)0x00, 0xFFFF00FF, "? ", "Glass" ));
		add(new Material( (byte)0x15, (byte)0x00, 0xFFFF00FF, "? ", "Red Cloth" ));
		add(new Material( (byte)0x16, (byte)0x00, 0xFFFF00FF, "? ", "Orange Cloth" ));
		add(new Material( (byte)0x17, (byte)0x00, 0xFFFF00FF, "? ", "Yellow Cloth" ));
		add(new Material( (byte)0x18, (byte)0x00, 0xFFFF00FF, "? ", "Lime Cloth" ));
		add(new Material( (byte)0x19, (byte)0x00, 0xFFFF00FF, "? ", "Green Cloth" ));
		add(new Material( (byte)0x1A, (byte)0x00, 0xFFFF00FF, "? ", "Aqua green Cloth" ));
		add(new Material( (byte)0x1B, (byte)0x00, 0xFFFF00FF, "? ", "Cyan Cloth" ));
		add(new Material( (byte)0x1C, (byte)0x00, 0xFFFF00FF, "? ", "Blue Cloth" ));
		add(new Material( (byte)0x1D, (byte)0x00, 0xFFFF00FF, "? ", "Purple Cloth" ));
		add(new Material( (byte)0x1E, (byte)0x00, 0xFFFF00FF, "? ", "Indigo Cloth" ));
		add(new Material( (byte)0x1F, (byte)0x00, 0xFFFF00FF, "? ", "Violet Cloth" ));
		add(new Material( (byte)0x20, (byte)0x00, 0xFFFF00FF, "? ", "Magenta Cloth" ));
		add(new Material( (byte)0x21, (byte)0x00, 0xFFFF00FF, "? ", "Pink Cloth" ));
		add(new Material( (byte)0x22, (byte)0x00, 0xFFFF00FF, "? ", "Black Cloth" ));
		add(new Material( (byte)0x23, (byte)0x00, 0xFFFF00FF, "? ", "Gray Cloth" ));
		add(new Material( (byte)0x24, (byte)0x00, 0xFFFF00FF, "? ", "White Cloth" ));
		add(new Material( (byte)0x25, (byte)0x00, 0xFFFF00FF, "? ", "Yellow flower" ));
		add(new Material( (byte)0x26, (byte)0x00, 0xFFFF00FF, "? ", "Red rose" ));
		add(new Material( (byte)0x27, (byte)0x00, 0xFFFF00FF, "? ", "Brown Mushroom" ));
		add(new Material( (byte)0x28, (byte)0x00, 0xFFFF00FF, "? ", "Red Mushroom" ));
		add(new Material( (byte)0x29, (byte)0x00, 0xFFFF00FF, "? ", "Gold Block" ));
		add(new Material( (byte)0x2A, (byte)0x00, 0xFFFF00FF, "? ", "Iron Block" ));
		add(new Material( (byte)0x2B, (byte)0x00, 0xFFFF00FF, "? ", "Double Step" ));
		add(new Material( (byte)0x2C, (byte)0x00, 0xFFFF00FF, "? ", "Step" ));
		add(new Material( (byte)0x2D, (byte)0x00, 0xFF882222, "? ", "Brick" ));
		add(new Material( (byte)0x2E, (byte)0x00, 0xFFFF00FF, "? ", "TNT" ));
		add(new Material( (byte)0x2F, (byte)0x00, 0xFFFF00FF, "? ", "Bookshelf" ));
		add(new Material( (byte)0x30, (byte)0x00, 0xFFFF00FF, "MC", "Mossy Cobblestone" ));
		add(new Material( (byte)0x31, (byte)0x00, 0xFFFF00FF, "? ", "Obsidian" ));
		add(new Material( (byte)0x32, (byte)0x00, 0xFFFF00FF, "? ", "Torch" ));
		add(new Material( (byte)0x33, (byte)0x00, 0xFFFF00FF, "? ", "Fire" ));
		add(new Material( (byte)0x34, (byte)0x00, 0xFFFF00FF, "? ", "Mob Spawner" ));
		add(new Material( (byte)0x35, (byte)0x00, 0xFFFF00FF, "? ", "Wooden Stairs" ));
		add(new Material( (byte)0x36, (byte)0x00, 0xFFFF00FF, "? ", "Chest" ));
		add(new Material( (byte)0x37, (byte)0x00, 0xFFFF00FF, "? ", "Redstone Wire" ));
		add(new Material( (byte)0x38, (byte)0x00, 0xFFFF00FF, "? ", "Diamond Ore" ));
		add(new Material( (byte)0x39, (byte)0x00, 0xFFFF00FF, "? ", "Diamond Block" ));
		add(new Material( (byte)0x3A, (byte)0x00, 0xFFFF00FF, "? ", "Workbench" ));
		add(new Material( (byte)0x3B, (byte)0x00, 0xFFFF00FF, "? ", "Crops" ));
		add(new Material( (byte)0x3C, (byte)0x00, 0xFFFF00FF, "? ", "Soil" ));
		add(new Material( (byte)0x3D, (byte)0x00, 0xFFFF00FF, "? ", "Furnace" ));
		add(new Material( (byte)0x3E, (byte)0x00, 0xFFFF00FF, "? ", "Burning Furnace" ));
		add(new Material( (byte)0x3F, (byte)0x00, 0xFFFF00FF, "? ", "Sign Post" ));
		add(new Material( (byte)0x40, (byte)0x00, 0xFFFF00FF, "? ", "Wooden Door" ));
		add(new Material( (byte)0x41, (byte)0x00, 0xFFFF00FF, "? ", "Ladder" ));
		add(new Material( (byte)0x42, (byte)0x00, 0xFFFF00FF, "? ", "Minecart Tracks" ));
		add(new Material( (byte)0x43, (byte)0x00, 0xFFFF00FF, "? ", "Cobblestone Stairs" ));
		add(new Material( (byte)0x44, (byte)0x00, 0xFFFF00FF, "? ", "Wall Sign" ));
		add(new Material( (byte)0x45, (byte)0x00, 0xFFFF00FF, "? ", "Lever" ));
		add(new Material( (byte)0x46, (byte)0x00, 0xFFFF00FF, "? ", "Stone Pressure Plate" ));
		add(new Material( (byte)0x47, (byte)0x00, 0xFFFF00FF, "? ", "Iron Door" ));
		add(new Material( (byte)0x48, (byte)0x00, 0xFFFF00FF, "? ", "Wooden Pressure Plate" ));
		add(new Material( (byte)0x49, (byte)0x00, 0xFFFF00FF, "? ", "Redstone Ore" ));
		add(new Material( (byte)0x4A, (byte)0x00, 0xFFFF00FF, "? ", "Glowing Redstone Ore" ));
		add(new Material( (byte)0x4B, (byte)0x00, 0xFFFF00FF, "? ", "Redstone torch (\"off\" state)" ));
		add(new Material( (byte)0x4C, (byte)0x00, 0xFFFF00FF, "? ", "Redstone torch (\"on\" state)" ));
		add(new Material( (byte)0x4D, (byte)0x00, 0xFFFF00FF, "? ", "Stone Button" ));
		add(new Material( (byte)0x4E, (byte)0x00, 0xFFFF00FF, "? ", "Snow" ));
		add(new Material( (byte)0x4F, (byte)0x00, 0xFFFF00FF, "? ", "Ice" ));
		add(new Material( (byte)0x50, (byte)0x00, 0xFFFF00FF, "? ", "Snow Block" ));
		add(new Material( (byte)0x51, (byte)0x00, 0xFFFF00FF, "? ", "Cactus" ));
		add(new Material( (byte)0x52, (byte)0x00, 0xFFAAAAAA, "? ", "Clay" ));
		add(new Material( (byte)0x53, (byte)0x00, 0xFFFF00FF, "? ", "Reed" ));
		add(new Material( (byte)0x54, (byte)0x00, 0xFFFF00FF, "? ", "Jukebox" ));
		add(new Material( (byte)0x55, (byte)0x00, 0xFFFF00FF, "? ", "Fence" ));
		add(new Material( (byte)0x56, (byte)0x00, 0xFFFF00FF, "? ", "Pumpkin" ));
		add(new Material( (byte)0x57, (byte)0x00, 0xFFFF00FF, "? ", "Netherstone" ));
		add(new Material( (byte)0x58, (byte)0x00, 0xFFFF00FF, "? ", "Slow Sand" ));
		add(new Material( (byte)0x59, (byte)0x00, 0xFFFF00FF, "? ", "Lightstone" ));
		add(new Material( (byte)0x5A, (byte)0x00, 0xFFFF00FF, "? ", "Portal" ));
		add(new Material( (byte)0x5B, (byte)0x00, 0xFFFF00FF, "? ", "Jack-O-Lantern" ));
	}
}
