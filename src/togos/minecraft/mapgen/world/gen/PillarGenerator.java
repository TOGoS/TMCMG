package togos.minecraft.mapgen.world.gen;

import java.util.Random;

import togos.minecraft.mapgen.world.Blocks;
import togos.minecraft.mapgen.world.Material;
import togos.minecraft.mapgen.world.Materials;
import togos.minecraft.mapgen.world.structure.Stamp;

public class PillarGenerator implements StampGenerator
{
	public static final int SHAPE_CUBOID = 1;
	public static final int SHAPE_CYLLINDER = 2;
	
	public Material mat = Materials.byBlockType[Blocks.COBBLESTONE];
	public double protoWidth = 2;
	public double protoDepth = 2;
	public double protoHeight = 4;
	public double minScale = 1;
	public double maxScale = 4;
	public int buriedness = 1;
	public int shape = SHAPE_CYLLINDER;
	
	public Stamp generateStamp( int seed ) {
		Random r = new Random(seed);
		r.nextInt(); r.nextInt(); r.nextInt(); r.nextInt();
		double scale = maxScale == minScale ? minScale : r.nextDouble()*(maxScale-minScale)+minScale;
		int height = (int)(protoHeight * scale);
		int width  = (int)(protoWidth * scale);
		int depth  = (int)(protoDepth * scale);
		Stamp s = new Stamp( width, height, depth, width/2, buriedness, depth/2 );
		
		double radX = width/2.0;
		double radZ = depth/2.0;
		
		for( int z=0; z<depth; ++z ) {
			for( int y=0; y<height; ++y ) {
				for( int x=0; x<width; ++x ) {
					boolean place;
					if( shape == SHAPE_CYLLINDER ) {
						double cx = (x+0.5-radX)/radX;
						double cz = (z+0.5-radZ)/radZ; 
						place = cx*cx+cz*cz < 1;
					} else {
						place = true;
					}
					
					if( place ) {
						s.setBlock( x, y, z, mat.blockType, mat.blockExtraBits );
					}
				}
			}
		}
		return s;
    }
}
