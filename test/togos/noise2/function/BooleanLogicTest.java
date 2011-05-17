package togos.noise2.function;

import togos.noise2.data.DataDa;
import togos.noise2.data.DataDaDaDa;
import togos.noise2.lang.ScriptError;

public class BooleanLogicTest extends NoiseFunctionTest
{
	DataDaDaDa in = new DataDaDaDa(
		new double[]{  0, 1, 2, 3, 4, 5 },
		new double[]{  0, 0, 4, 4, 8, 8 },
		new double[]{ -1, 0, 0, 0, 4, 5 }
	);
	
	public void testSomeWackyThing() throws ScriptError {
		FunctionDaDaDa_Da expr = (FunctionDaDaDa_Da)comp.compile(
			"if( x > 2 and x <= 4 and y == 4, +2," +
			"    x < 3 and z < 0 or z > 0, z," +
			"    +3 )","test-script");
		
		DataDa out = expr.apply(in);
		assertEquals( -1, out.x[0] );
		assertEquals( +3, out.x[1] );
		assertEquals( +3, out.x[2] );
		assertEquals( +2, out.x[3] );
		assertEquals( +4, out.x[4] );
		assertEquals( +5, out.x[5] );
	}
}
