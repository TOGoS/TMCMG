package togos.minecraft.mapgen;

import java.io.File;

import togos.minecraft.mapgen.world.gen.ChunkMunger;
import togos.minecraft.mapgen.world.gen.LayeredTerrainFunction;
import togos.minecraft.mapgen.world.gen.MinecraftWorldGenerator;
import togos.minecraft.mapgen.world.structure.ChunkData;

import junit.framework.TestCase;

/**
 * Run all the scripts!
 */
public class ScriptTest extends TestCase
{
	public void testAllTheScripts() throws Exception {
		File scriptDir = new File("tnl3-scripts");
		if( !scriptDir.exists() ) {
			fail("No scripts to test!");
		}
		
		final int vectorSize = 128;
		double[] xBuf = new double[vectorSize];
		double[] zBuf = new double[vectorSize];
		for( int i=0; i<vectorSize; ++i ) {
			xBuf[i] = Math.sin( i*0.01);
			zBuf[i] = Math.cos(-i*0.01);
		}
		
		LayeredTerrainFunction.TerrainBuffer tb = null;
		ChunkData cd = new ChunkData(0,0,0,16,256,16);
		
		int testedScriptCount = 0;
		for( File f : scriptDir.listFiles() ) {
			MinecraftWorldGenerator mwg = ScriptUtil.loadWorldGenerator(f);
			
			{
				LayeredTerrainFunction ltf = mwg.getTerrainFunction();
				assertNotNull( "Terrain function is null!", ltf );
				tb = ltf.apply( vectorSize, xBuf, zBuf, tb );
			}
			
			{
				ChunkMunger cm = mwg.getChunkMunger();
				assertNotNull( "Chunk munger is null!", cm );
				cm.mungeChunk(cd);
			}
			
			++testedScriptCount;
		}
		
		if( testedScriptCount == 0 ) {
			fail("Found no scripts to test!");
		}
	}
}
