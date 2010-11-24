package togos.minecraft.mapgen.world.structure;

import java.util.Map;

import togos.minecraft.mapgen.world.gen.Material;

public class Stamp extends MiniChunkData
{
	public boolean[] mask;
	
	public int originX, originY, originZ;
	
	public Stamp( int width, int height, int depth, int originX, int originY, int originZ ) {
		super(width,height,depth);
		mask = new boolean[width*height*depth];
		this.originX = originX;
		this.originY = originY;
		this.originZ = originZ;
	}
	
	public boolean getMask( int x, int y, int z ) {
		return mask[blockIndex(x, y, z)];
	}
	
	public void setMask( int x, int y, int z, boolean v ) {
		mask[blockIndex(x, y, z)] = v;
	}
	
	public void setBlock( int x, int y, int z, byte blockNum, byte extraBits ) {
		super.setBlock(x, y, z, blockNum, extraBits);
		setMask(x,y,z,true);
	}
	
	
	public void populate( int x, int y, int z, int width, int height, int depth, String diagram, Map charMaterials ) {
		if( diagram.length() < width*height*depth ) {
			throw new RuntimeException("Diagram should be w*h*d ("+(width*height*depth)+" characters, but was only "+diagram.length());
		}
		int i=0;
		for( int ty=height-1; ty>=0; --ty ) {
			for( int tz=0; tz<depth; ++tz ) {
				for( int tx=0; tx<width; ++tx, ++i ) {
					char c = diagram.charAt(i);
					if( c != ' ' ) {
						Material m = (Material)charMaterials.get(Character.valueOf(c));
						if( m == null ) {
							throw new RuntimeException("No material specified for character: '"+c+"'");
						}
						setBlock( x+tx, y+ty, z+tz, m.blockNum );
					}
				}
			}
		}
	}
}
