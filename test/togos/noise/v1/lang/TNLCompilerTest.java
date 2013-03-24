package togos.noise.v1.lang;

import junit.framework.TestCase;
import togos.noise.v1.func.AddOutDaDaDa_Da;
import togos.noise.v1.func.Constant_Da;
import togos.noise.v1.func.MaxOutDaDaDa_Da;
import togos.noise.v1.func.MultiplyOutDaDaDa_Da;
import togos.noise.v1.func.PerlinDaDaDa_Da;
import togos.noise.v1.func.TNLFunctionDaDaDa_Da;
import togos.lang.ScriptError;
import togos.noise.v1.lang.macro.ConstantMacroType;
import togos.noise.v1.lang.macro.DaDaDa_DaArrayArgMacroType;

public class TNLCompilerTest extends TestCase
{
	public void testCompileInt() throws ScriptError {
		TNLCompiler comp = new TNLCompiler();
		assertEquals( new Integer(0), comp.compile("0") );
		assertEquals( new Integer(-123), comp.compile("-123") );
		assertEquals( new Integer(0x4040), comp.compile("0x4040") );
		assertEquals( new Integer(-0x345), comp.compile("-0x345") );
	}

	public void testCompileDouble() throws ScriptError {
		TNLCompiler comp = new TNLCompiler();
		assertEquals( new Double(0.125), comp.compile("0.125") );
		assertEquals( new Double(-100.25), comp.compile("-100.25") );
		assertEquals( new Double(1.5e-5), comp.compile("+1.5e-5") );
	}
	
	public void testCompileString() throws ScriptError {
		TNLCompiler comp = new TNLCompiler();
		assertEquals( "Hello, world!\n", comp.compile("\"Hello, world!\\n\""));
	}

	public void testCompileFunction() throws ScriptError {
		TNLCompiler comp = new TNLCompiler();
		comp.macroTypes.put("*", new DaDaDa_DaArrayArgMacroType(MultiplyOutDaDaDa_Da.class));
		assertEquals( new MultiplyOutDaDaDa_Da(new TNLFunctionDaDaDa_Da[] {
			new Constant_Da(2), new Constant_Da(3)
		}), comp.compile("2 * 3") );
	}

	public void testCompileFunction2() throws ScriptError {
		PerlinDaDaDa_Da perlin = new PerlinDaDaDa_Da();
		TNLCompiler comp = new TNLCompiler();
		comp.macroTypes.put("*", new DaDaDa_DaArrayArgMacroType(MultiplyOutDaDaDa_Da.class));
		comp.macroTypes.put("+", new DaDaDa_DaArrayArgMacroType(AddOutDaDaDa_Da.class));
		comp.macroTypes.put("max", new DaDaDa_DaArrayArgMacroType(MaxOutDaDaDa_Da.class));
		comp.macroTypes.put("perlin", new ConstantMacroType(perlin));
		assertEquals(
			new AddOutDaDaDa_Da(new TNLFunctionDaDaDa_Da[] {
				new MaxOutDaDaDa_Da(new TNLFunctionDaDaDa_Da[] {
					perlin,
					new Constant_Da(0)
				}),
				new MultiplyOutDaDaDa_Da(new TNLFunctionDaDaDa_Da[] {
					new Constant_Da(2),
					perlin
				})
			}),
			comp.compile("max(perlin,0) + 2 * perlin")
		);
	}
}
