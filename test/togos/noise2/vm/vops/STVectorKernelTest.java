package togos.noise2.vm.vops;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import junit.framework.TestCase;
import togos.noise2.lang.CompileError;

public class STVectorKernelTest extends TestCase
{
	public void testThing() throws IOException, CompileError {
		String script =
			"var double x\n" +
			"var double y\n" +
			"var double res\n" +
			"# comment!\n" +
			"\n" + // Blank line!
			"res = y * y\n" +
			"res = x + res\n" +
			"res = res + 3\n";
		
		STVKScriptCompiler c = new STVKScriptCompiler();
		STVectorKernel k = c.compile(new BufferedReader(new StringReader(script)), "test", 16);
		double[] x = (double[])k.vars.get("x");
		double[] y = (double[])k.vars.get("y");
		double[] res = (double[])k.vars.get("res");
		x[0] = 1;
		y[0] = 1;
		x[1] = 3;
		y[1] = 3;
		x[2] = 5;
		y[2] = 5;
		k.invoke(2);
		assertEquals(  5, (int)res[0] );
		assertEquals( 15, (int)res[1] );
		assertEquals(  0, (int)res[2] );
	}
}
