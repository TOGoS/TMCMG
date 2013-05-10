package togos.minecraft.mapgen;

import java.io.File;

import junit.framework.TestCase;
import togos.minecraft.mapgen.world.Materials;
import togos.minecraft.mapgen.world.gen.ChunkMunger;
import togos.minecraft.mapgen.world.gen.LayeredTerrainFunction;
import togos.minecraft.mapgen.world.gen.MinecraftWorldGenerator;
import togos.minecraft.mapgen.world.structure.ChunkData;
import togos.noise.v3.parser.TokenizerSettings;

/**
 * Run all the scripts!
 */
public class ScriptTest extends TestCase
{
	static final int TNL_TAB_WIDTH = 4;
	static final TokenizerSettings TEST_LOC = new TokenizerSettings(ScriptTest.class.getName(), 0, 0, TNL_TAB_WIDTH);
	
	protected static int intValue( Object o ) {
		return ((Number)o).intValue();
	}
	
	public void testMaterialFunction() throws Exception {
		int[] materials = new int[] {
			0, 0,
			5, 5,
			123, 4,
			123, 0xF,
			0xFFF, 8,
			0xFFF, 0xF
		};
		
		for( int i=0; i<materials.length; i += 2 ) {
			assertEquals( Materials.encodeMaterial(materials[i], materials[i+1]), intValue(ScriptUtil.eval("material("+materials[i]+", "+materials[i+1]+")", ScriptUtil.STD_CONTEXT, TEST_LOC)) );
		}
	}
	
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
			// For now I don't expect that one to work:
			if( "sixfootwavesworld.tnl".equals(f.getName()) ) continue;
			
			MinecraftWorldGenerator mwg = ScriptUtil.loadWorldGenerator(f, TNL_TAB_WIDTH);
			
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
