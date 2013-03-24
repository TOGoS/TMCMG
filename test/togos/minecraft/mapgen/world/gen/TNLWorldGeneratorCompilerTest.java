package togos.minecraft.mapgen.world.gen;

import junit.framework.TestCase;
import togos.noise.v1.func.SimplexDaDaDa_Da;
import togos.lang.ScriptError;

public class TNLWorldGeneratorCompilerTest extends TestCase
{
	public void testCompileInt() throws ScriptError {
		TNLWorldGeneratorCompiler comp = new TNLWorldGeneratorCompiler();
		Object o = comp.compile(
			"layered-terrain(\n" +
			"  component(\"humidity\", simplex),\n" +
			"  component(\"temperature\", simplex),\n" +
			"  layer( materials.log, 0, 64 + simplex),\n" +
			"  tree-populator( tree-types.pine, 0.01 )\n" +
			")"
		);
		assertTrue( o instanceof SimpleWorldGenerator );
		
		SimpleWorldGenerator ltg = (SimpleWorldGenerator)o;
		// assertEquals( 1, ltg.g.layers.size() );
		assertEquals( 2, ltg.components.size() );
		assertTrue( ltg.components.get("humidity") instanceof SimplexDaDaDa_Da );
		assertTrue( ltg.components.get("temperature") instanceof SimplexDaDaDa_Da );
	}
}
