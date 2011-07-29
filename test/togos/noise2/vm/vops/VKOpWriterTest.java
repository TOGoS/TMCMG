package togos.noise2.vm.vops;

import java.io.StringWriter;

import junit.framework.TestCase;
import togos.noise2.rdf.TNLNamespace;

public class VKOpWriterTest extends TestCase
{
	public void testStvkOpWriter() throws Exception {
		StringWriter sw = new StringWriter(1024);
		VKOpWriter ow = new VKOpWriter(sw);
		String c1 = ow.writeConstant( null, 1.25 );
		String c2 = ow.writeConstant( null, 4.75 );
		String c3 = ow.writeConstant( null, 3.00 );
		ow.writeOp( null, TNLNamespace.ADD, new String[]{c1,c2,c3} );
		
		String script = sw.toString();
		
		assertEquals(
			"var double var1\n" +
			"var1 = 1.25 + 4.75\n" +
			"var double var2\n" +
			"var2 = var1 + 3.0\n", script );
		
		STVectorKernel stvk = new STVKScriptCompiler().compile(script, "test", 1);
		stvk.invoke(1);
		assertEquals( (int)9, (int)((double[])stvk.vars.get("var2"))[0] );
	}

	public void testStvkOpWriter2() throws Exception {
		StringWriter sw = new StringWriter(1024);
		VKOpWriter ow = new VKOpWriter(sw);
		String v1 = ow.declareInput("double", "x");
		String c1 = ow.writeConstant( null, 1.25 );
		String c2 = ow.writeConstant( null, 4.75 );
		String c3 = ow.writeConstant( null, 3.00 );
		ow.writeOp( null, TNLNamespace.MULTIPLY, new String[] {
			v1, ow.writeOp( null, TNLNamespace.ADD, new String[]{c1,c2,c3} )
		} );
		
		String script = sw.toString();
		
		assertEquals(
			"var double var1\n" +
			"var double var2\n" +
			"var2 = 1.25 + 4.75\n" +
			"var double var3\n" +
			"var3 = var2 + 3.0\n" +
			"var double var4\n" +
			"var4 = var1 * var3\n", script );
		
		STVectorKernel stvk = new STVKScriptCompiler().compile(script, "test", 1);
		((double[])stvk.vars.get("var1"))[0] = 2;
		stvk.invoke(1);
		assertEquals( (int)18, (int)((double[])stvk.vars.get("var4"))[0] );
	}

	public void testStvkOpWriter3() throws Exception {
		StringWriter sw = new StringWriter(1024);
		VKOpWriter ow = new VKOpWriter(sw);
		String v1 = ow.declareInput("double", "x");
		String v2 = ow.declareInput("double", "y");
		
		String plussed = ow.writeOp( null, TNLNamespace.ADD, new String[] {v1,v2} );
		String minused = ow.writeOp( null, TNLNamespace.SUBTRACT, new String[] {v1,v2} );
		String timesed = ow.writeOp( null, TNLNamespace.MULTIPLY, new String[] {v1,v2} );
		String divided = ow.writeOp( null, TNLNamespace.DIVIDE, new String[] {v1,v2} );
		
		String script = sw.toString();
		
		assertEquals(
			"var double "+v1+"\n" +
			"var double "+v2+"\n" +
			"var double "+plussed+"\n" +
			plussed +" = "+v1+" + "+v2+"\n" +
			"var double "+minused+"\n" +
			minused +" = "+v1+" - "+v2+"\n" +
			"var double "+timesed+"\n" +
			timesed +" = "+v1+" * "+v2+"\n" +
			"var double "+divided+"\n" +
			divided +" = "+v1+" / "+v2+"\n", script );
	}
}
