package togos.noise2.vm.stkernel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import togos.noise2.lang.CompileError;

import junit.framework.TestCase;

public class STVectorKernelTest extends TestCase
{
	public void testThing() throws IOException, CompileError {
		String script =
			"vector double x\n" +
			"vector double y\n" +
			"vector double res1\n" +
			"vector double res2\n" +
			"# comment!\n" +
			"\n" + // Blank line!
			"res1 = y * y\n" +
			"res2 = x + res1\n";
		
		Compiler c = new Compiler();
		STVectorKernel k = c.compile(new BufferedReader(new StringReader(script)), "test", 16);
		double[] x = (double[])k.vars.get("x");
		double[] y = (double[])k.vars.get("y");
		double[] res = (double[])k.vars.get("res2");
		x[0] = 1;
		y[0] = 1;
		x[1] = 3;
		y[1] = 3;
		x[2] = 5;
		y[2] = 5;
		k.invoke(2);
		assertEquals(  2, (int)res[0] );
		assertEquals( 12, (int)res[1] );
		assertEquals(  0, (int)res[2] );
	}
}
