package togos.noise2.function;

import togos.noise2.data.DataDa;
import togos.noise2.data.DataDaDaDa;
import togos.noise2.lang.ScriptError;

public class SqrtTest extends NoiseFunctionTest {

	public void testSqrt() throws ScriptError {

		DataDaDaDa in = new DataDaDaDa(
			new double[]{ -1, 0, 1, 4 },
			new double[]{  0, 0, 0, 0 },
			new double[]{  0, 0, 0, 0 }
		);

		FunctionDaDaDa_Da expr = 
			(FunctionDaDaDa_Da) comp.compile("sqrt(x)","test-script");

		DataDa out = expr.apply(in);
		assertEquals(0, out.x[0] );
		assertEquals(0, out.x[1] );
		assertEquals(1, out.x[2] );
		assertEquals(2, out.x[3] );
	}
}
