package togos.noise2.vm.dftree.lang;

import junit.framework.TestCase;
import togos.lang.SourceLocation;
import togos.noise2.lang.ScriptError;
import togos.noise2.rewrite.ConstantFolder;
import togos.noise2.vm.dftree.func.TNLFunctionDaDaDa_Da;
import togos.noise2.vm.dftree.lang.macro.ConstantMacroType;
import togos.noise2.vm.dftree.lang.macro.NoiseMacros;

public class TNLFunctionTest extends TestCase
{
	SourceLocation ZSL = new SourceLocation() {
		public int getSourceLineNumber() {
			return 0;
		}
		public int getSourceColumnNumber() {
			return 0;
		}
		public String getSourceFilename() {
			return "<test input>";
		}
	};
	
	public Object compile( String source ) throws ScriptError {
		TNLCompiler comp = new TNLCompiler();
		comp.macroTypes.putAll(NoiseMacros.stdNoiseMacros);
		comp.macroTypes.put("p2", new ConstantMacroType(comp.compile("perlin * 2")));
		return comp.compile(source);
	}
	
	protected void testCompile( String simplified, String original, boolean simplify ) {
		try {
			Object c = compile(original);
			TNLFunctionDaDaDa_Da fd = (TNLFunctionDaDaDa_Da)FunctionUtil.toDaDaDa_Da(c, ZSL);
			if( simplify ) fd = (TNLFunctionDaDaDa_Da)ConstantFolder.instance.rewrite(fd);
			assertEquals( simplified, fd.toTnl() );
		} catch( ScriptError e ) {
			throw new RuntimeException(e);
		}
	}

	protected void testCompile( String simplified, String original ) {
		testCompile(simplified, original, false);
	}

	protected void testSimplify( String simplified, String original ) {
		testCompile(simplified, original, true);
	}
	
	public void testCompile() {
		testCompile( "1.0", "1.0" );
		testCompile( "(perlin(x, y, z) + (perlin(x, y, z) * 2.0))", "perlin(x, y, z) + p2" );
		testCompile( "simplex(x, y, z)", "cache(simplex(x, y, z))" ); // cache(...).toTnl -> ...
	}
	
	public void testSimplify() {
		testSimplify( "3.0", "1.0 + 2.0" );
		testSimplify( "8.0", "1 * 4 + 2 * 2" );
		testSimplify( "ridge(0.5, 2.5, perlin(x, (y + 1.0), z))", "ridge(1 / 2, 5 / 2, perlin(x, y + 1, z))" );
		testSimplify( "1.0", "ridge(1 / 2, 5 / 2, 0.0)" );
		testSimplify( "0.5", "clamp(1 / 2, 5 / 2, 0.0)" );
		testSimplify( "0.5", "cache(0.5)" );
	}
}
