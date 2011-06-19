package togos.noise2.vm.javac;

import java.util.Random;

import junit.framework.TestCase;

public class JavaCompilerTest extends TestCase
{
	protected String randName(int len) {
		String nameChars = "abcdefghijklmnopqrstuvwxyz1234567890";
		String name = "";
		Random r = new Random();
		for( int i=0; i<len; ++i ) {
			name += nameChars.charAt(r.nextInt(nameChars.length()));
		}
		return name;
	}
	
	public void testJavaCompiler() throws Exception {
		String np = randName(12);
		String packageName = "togos.noise2.vm.test"+np;
		String className = packageName + ".Hallo";
		String classSource =
			"package "+packageName+";\n" +
			"\n" +
			"public class Hallo {\n" +
			"\tpublic String toString() {\n" +
			"\t\treturn \"Hallo I em "+np+"\";" +
			"\t}\n" +
			"}\n";
		
		JavaCompiler comp = new JavaCompiler();
		comp.javac = "c:/Program Files (x86)/Java/jdk1.6.0_17/bin/javac.exe";
		comp.classRoot = new String[] { "bin" };
		comp.sourceRoot = "junk-src";
		comp.destClassRoot = "bin";
		
		comp.compile(className, classSource);
		
		Class c = Class.forName(className);
		assertEquals( "Hallo I em "+np, c.newInstance().toString() );
	}
}
