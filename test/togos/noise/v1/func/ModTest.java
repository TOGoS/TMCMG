package togos.noise.v1.func;

import togos.noise.v1.func.LFunctionDaDaDa_Da;
import togos.lang.ScriptError;

public class ModTest extends NoiseFunctionTest
{
	public void testMod() throws ScriptError {
		LFunctionDaDaDa_Da expr0 = compileLDDDF("x % y");
		
		double[] x = new double[]{  14, -14,  14, -14,  0,  2, -2, -2,  2 };
		double[] y = new double[]{  10,  10, -10, -10,  2,  2,  2, -2, -2 };
		double[] z = new double[]{   0,   0,   0,   0,  0,  0,  0,  0,  0 };
		expr0.apply( x.length, x, y, z, z );
		
		assertEquals(  4, z[0] );
		assertEquals(  6, z[1] );
		assertEquals( -6, z[2] );
		assertEquals( -4, z[3] );
		
		assertEquals( 0, z[4] );
		assertEquals( 0, z[5] );
		assertEquals( 0, z[6] );

		assertEquals( 0, z[7] );
		assertEquals( 0, z[8] );
	}
}
