package togos.minecraft.mapgen.world.gen;

import togos.minecraft.mapgen.world.Blocks;
import togos.minecraft.mapgen.world.ChunkUtil;
import togos.minecraft.mapgen.world.Material;
import togos.minecraft.mapgen.world.structure.ChunkData;
import togos.noise2.vm.dftree.data.DataDa;
import togos.noise2.vm.dftree.data.DataDaDa;
import togos.noise2.vm.dftree.data.DataDaDaDa;
import togos.noise2.vm.dftree.data.DataIa;
import togos.noise2.vm.dftree.func.FunctionDaDaDa_Da;
import togos.noise2.vm.dftree.func.FunctionDaDaDa_Ia;

public class Depositor implements ChunkMunger
{
	Material material;
	FunctionDaDaDa_Ia materialFunction;
	FunctionDaDaDa_Da thicknessFunction;
	
	public Depositor( FunctionDaDaDa_Ia materialFunction, FunctionDaDaDa_Da thicknessFunction ) {
		this.materialFunction = materialFunction;
		this.thicknessFunction = thicknessFunction;
	}
	
	public void mungeChunk( ChunkData cd ) {
		int sampleCount = cd.getChunkWidth() * cd.getChunkDepth();
		int maxHeight = cd.getChunkHeight();
		double[] baseY = new double[sampleCount]; 
		
		for( int z=0, i=0; z<cd.depth; ++z ) {
			for( int x=0; x<cd.width; ++x, ++i ) {
				baseY[i] = Double.NEGATIVE_INFINITY;
				yLoop: for( int y=cd.height-1; y>0; --y ) {
					switch( cd.getBlockId(x, y, z) ) {
					case( Blocks.AIR ): case( Blocks.WATER ):
						continue yLoop;
					default:
						baseY[i] = y+1;
						break yLoop;
					}
				}
			}
		}
		
		DataDaDa xz = ChunkUtil.getTileXZCoordinates( cd );
		DataDaDaDa in = new DataDaDaDa( sampleCount, xz.x, baseY, xz.y );
		DataDa thickness = thicknessFunction.apply( in );
		DataIa material = materialFunction.apply( in );
		
		for( int z=0, i=0; z<cd.depth; ++z ) {
			for( int x=0; x<cd.width; ++x, ++i ) {
				if( baseY[i] == Double.NEGATIVE_INFINITY ) continue;
				int blockType = material.v[i];
				int floorY = baseY[i] > maxHeight ? maxHeight : (int)baseY[i];
				int ceilY = (int)baseY[i] + (int)Math.round(thickness.x[i]);
				ceilY = ceilY > maxHeight ? maxHeight : ceilY;
				for( int y=floorY; y<ceilY; ++y ) {
					cd.setBlock( x, y, z, (byte)blockType, (byte)(blockType>>16) );
				}
			}
		}
	}
}
