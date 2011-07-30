package togos.noise2.vm.dftree.func;

import togos.noise2.lang.ScriptError;
import togos.noise2.rewrite.ConstantFolder;
import togos.noise2.vm.dftree.data.DataDa;
import togos.noise2.vm.dftree.data.DataDaDaDa;
import togos.noise2.vm.dftree.lang.FunctionUtil;

public class ArcTanTest extends NoiseFunctionTest
{
	public void testSqrt() throws ScriptError {
		DataDaDaDa in = new DataDaDaDa(
			new double[]{ -2, -1, 0, 3 },
			new double[]{ -1,  0, 1, 4 },
			new double[]{  0,  0, 0, 0 }
		);
		
		FunctionDaDaDa_Da expr = 
			(FunctionDaDaDa_Da) comp.compile("atan(y - 1)","test-script");
		assertFalse( FunctionUtil.isConstant(expr) );
		
		DataDa out1 = expr.apply(in);
		assertTrue( (out1.x[0] < -1.10) && (out1.x[1] > -1.11) );
		assertTrue( (out1.x[1] < -0.78) && (out1.x[1] > -0.79) );
		assertEquals( 0, out1.x[2] );
		assertTrue( (out1.x[3] > 1.24) && (out1.x[3] < 1.25) );
		
		FunctionDaDaDa_Da expr2 = 
			(FunctionDaDaDa_Da) comp.compile("atan","test-script");
		assertFalse( FunctionUtil.isConstant(expr2) );
		assertFalse( ConstantFolder.instance.rewrite(expr2) instanceof Constant_Da );
		DataDa out2 = expr2.apply(in);
		
		assertEquals( out1.x[0], out2.x[0] );
		assertEquals( out1.x[1], out2.x[1] );
		assertEquals( out1.x[2], out2.x[2] );
		assertEquals( out1.x[3], out2.x[3] );
		
		FunctionDaDaDa_Da expr3 = 
			(FunctionDaDaDa_Da) comp.compile("atan(12)","test-script");
		assertTrue( FunctionUtil.isConstant(expr3) );
		assertTrue( ConstantFolder.instance.rewrite(expr3) instanceof Constant_Da );
	}
}
