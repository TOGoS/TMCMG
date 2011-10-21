package togos.noise2.vm.dftree.func;

import togos.noise2.lang.ScriptError;

public class ModTest extends NoiseFunctionTest
{
	public void testMod() throws ScriptError {
		LFunctionDaDaDa_Da expr0 = compileLDDDF("x % y");
		
		double[] x = new double[]{  14, -14,  14, -14 };
		double[] y = new double[]{  10,  10, -10, -10 };
		double[] z = new double[]{   0,   0,   0,   0 };
		expr0.apply( 4, x, y, z, z );
		
		assertEquals(  4, z[0] );
		assertEquals(  6, z[1] );
		assertEquals( -6, z[2] );
		assertEquals( -4, z[3] );
	}
}
