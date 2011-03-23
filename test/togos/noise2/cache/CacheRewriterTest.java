package togos.noise2.cache;

import junit.framework.TestCase;
import togos.minecraft.mapgen.world.gen.TNLWorldGeneratorCompiler;
import togos.noise2.function.AddOutDaDaDa_Da;
import togos.noise2.function.CacheDaDaDa_Da;
import togos.noise2.function.Constant_Da;
import togos.noise2.function.MultiplyOutDaDaDa_Da;
import togos.noise2.function.ReduceOutDaDaDa_Da;
import togos.noise2.lang.Expression;
import togos.noise2.lang.ScriptError;
import togos.noise2.lang.TNLCompiler;
import togos.noise2.rewrite.CacheRewriter;

public class CacheRewriterTest extends TestCase
{
	public void testRewrite() throws ScriptError {
		String src = "simplex + 2 * simplex + 2 + 3 + 4 * simplex + 4 * simplex";
		
		TNLCompiler comp = new TNLWorldGeneratorCompiler();
		
		Object o = comp.compile(src, "test source");
		CacheRewriter cw = new CacheRewriter(SoftCache.getInstance());
		cw.initCounts((Expression)o);
		// cw.dumpCounts(System.err);

		o = cw.rewrite(o);
		
		assertTrue( o instanceof AddOutDaDaDa_Da );
		Expression[] dse = ((ReduceOutDaDaDa_Da)o).directSubExpressions();
		assertTrue( dse[0] instanceof CacheDaDaDa_Da );
		assertTrue( dse[1] instanceof MultiplyOutDaDaDa_Da );
		assertTrue( dse[2] instanceof Constant_Da );
		assertTrue( dse[3] instanceof Constant_Da );
		assertTrue( dse[4] instanceof CacheDaDaDa_Da );
		assertTrue( dse[5] instanceof CacheDaDaDa_Da );
		// the simplex in the (4 * simplex)
		assertTrue( ((ReduceOutDaDaDa_Da)((CacheDaDaDa_Da)dse[4]).next).directSubExpressions()[1] instanceof CacheDaDaDa_Da );
	}
}
