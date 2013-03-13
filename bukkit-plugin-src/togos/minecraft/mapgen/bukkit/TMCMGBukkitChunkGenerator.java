package togos.minecraft.mapgen.bukkit;

import java.io.PrintStream;
import java.util.Random;

import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;

import togos.minecraft.mapgen.world.Blocks;
import togos.minecraft.mapgen.world.gen.ChunkMunger;
import togos.minecraft.mapgen.world.gen.WorldGenerator;
import togos.minecraft.mapgen.world.structure.ChunkData;
import togos.noise2.vm.dftree.data.DataDaDa;
import togos.noise2.vm.dftree.data.DataDaIa;
import togos.noise2.vm.dftree.func.FunctionDaDa_DaIa;

public class TMCMGBukkitChunkGenerator extends ChunkGenerator
{
	protected String worldName;
	protected FunctionDaDa_DaIa groundFunction;
	protected ChunkMunger chunkMunger;
	public PrintStream debugStream;
	
	public TMCMGBukkitChunkGenerator( String worldName, WorldGenerator wg ) {
		this.worldName = worldName;
		this.groundFunction = wg.getGroundFunction();
		this.chunkMunger = wg.getChunkMunger();
	}
	
	public boolean canSpawn( World world, int x, int z ) {
		DataDaDa coords = new DataDaDa( 1, new double[] { x }, new double[] { z } );
		DataDaIa gnd = groundFunction.apply(coords);
		boolean cs = gnd.d[0] > 32 && gnd.d[0] < 96 && gnd.i[0] != Blocks.WATER && gnd.i[0] != Blocks.LAVA;
		System.out.println("Can spawn at "+worldName+":"+x+","+z+"?    "+cs);
		return cs;
	}
	
	public byte[] generate( World world, Random r, int cx, int cz ) {
		if( debugStream != null ) {
			debugStream.println("Generating chunk "+worldName+"/"+cx+","+cz+"...");
		}
		ChunkData cd = ChunkData.forChunkCoords( cx, cz );
		long beginTime = System.currentTimeMillis();
		chunkMunger.mungeChunk( cd );
		long endTime = System.currentTimeMillis();
		if( debugStream != null ) {
			debugStream.println("Generated chunk "+worldName+"/"+cx+","+cz+" in "+(endTime-beginTime)+"ms");
		}
		return cd.blockIds;
    }
};
