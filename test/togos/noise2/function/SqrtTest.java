package togos.noise2.function;

import togos.noise2.data.DataDa;
import togos.noise2.data.DataDaDaDa;
import togos.noise2.lang.FunctionUtil;
import togos.noise2.lang.ScriptError;
import togos.noise2.rewrite.ConstantFolder;

public class SqrtTest extends NoiseFunctionTest
{
	public void testSqrt() throws ScriptError {
		DataDaDaDa in = new DataDaDaDa(
			new double[]{ -1, 0, 1, 4 },
			new double[]{  0, 1, 2, 5 },
			new double[]{  0, 1, 4, 9 }
		);
		
		FunctionDaDaDa_Da expr0 = 
			(FunctionDaDaDa_Da) comp.compile("sqrt","test-script");
		assertFalse( FunctionUtil.isConstant(expr0) );
		
		DataDa out0 = expr0.apply(in);
		assertEquals(0, out0.x[0] );
		assertEquals(0, out0.x[1] );
		assertEquals(1, out0.x[2] );
		assertEquals(2, out0.x[3] );
		
		FunctionDaDaDa_Da expr1 = 
			(FunctionDaDaDa_Da) comp.compile("sqrt(y - 1)","test-script");
		assertFalse( FunctionUtil.isConstant(expr1) );
		
		DataDa out1 = expr1.apply(in);
		assertEquals(0, out1.x[0] );
		assertEquals(0, out1.x[1] );
		assertEquals(1, out1.x[2] );
		assertEquals(2, out1.x[3] );
		
		FunctionDaDaDa_Da expr2 = 
			(FunctionDaDaDa_Da) comp.compile("sqrt(z)","test-script");
		assertFalse( FunctionUtil.isConstant(expr2) );
		
		DataDa out2 = expr2.apply(in);
		assertEquals(0, out2.x[0] );
		assertEquals(1, out2.x[1] );
		assertEquals(2, out2.x[2] );
		assertEquals(3, out2.x[3] );
		
		FunctionDaDaDa_Da expr3 = 
			(FunctionDaDaDa_Da) comp.compile("sqrt(4)","test-script");
		assertTrue( FunctionUtil.isConstant(expr3) );
		assertTrue( ConstantFolder.instance.rewrite(expr3) instanceof Constant_Da );
	}
}
