package togos.noise2.vm.dftree.func;

import togos.noise2.lang.ScriptError;
import togos.noise2.vm.dftree.data.DataDa;
import togos.noise2.vm.dftree.data.DataDaDaDa;

public class SimplexTest extends NoiseFunctionTest
{
	public void testSimplex() throws ScriptError {
		DataDaDaDa in = new DataDaDaDa(
			new double[]{  -0.250,  0.750,  1.750,  2.750 },
			new double[]{   0.500,  1.500,  2.500,  3.500 },
			new double[]{  -2.125, -1.125, -0.125, +0.875 }
		);
		
		FunctionDaDaDa_Da expr1 = 
			(FunctionDaDaDa_Da) comp.compile("simplex(x + 1,y + 1,z + 1)","test-script");
		FunctionDaDaDa_Da expr2 = 
			(FunctionDaDaDa_Da) comp.compile("simplex","test-script");
		
		DataDa out1 = expr1.apply(in);
		DataDa out2 = expr2.apply(in);
		
		assertNotEquals( 0, out1.x[0] );
		assertNotEquals( 0, out1.x[1] );
		assertNotEquals( 0, out1.x[2] );
		assertNotEquals( 0, out1.x[3] );
		
		assertEquals( out1.x[0], out2.x[1] );
		assertEquals( out1.x[1], out2.x[2] );
		assertEquals( out1.x[2], out2.x[3] );
	}
}
