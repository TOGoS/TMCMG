package togos.minecraft.mapgen.script;

import junit.framework.TestCase;
import togos.noise2.function.AddOutDaDaDa_Da;
import togos.noise2.function.Constant_Da;
import togos.noise2.function.FunctionDaDaDa_Da;
import togos.noise2.function.MaxOutDaDaDa_Da;
import togos.noise2.function.MultiplyOutDaDaDa_Da;
import togos.noise2.function.PerlinDaDaDa_Da;

public class ScriptCompilerTest extends TestCase
{
	public void testCompileInt() {
		ScriptCompiler comp = new ScriptCompiler();
		assertEquals( new Integer(0), comp.compile("0") );
		assertEquals( new Integer(-123), comp.compile("-123") );
		assertEquals( new Integer(0x4040), comp.compile("0x4040") );
		assertEquals( new Integer(-0x345), comp.compile("-0x345") );
	}

	public void testCompileDouble() {
		ScriptCompiler comp = new ScriptCompiler();
		assertEquals( new Double(0.125), comp.compile("0.125") );
		assertEquals( new Double(-100.25), comp.compile("-100.25") );
		assertEquals( new Double(1.5e-5), comp.compile("+1.5e-5") );
	}

	public void testCompileFunction() {
		ScriptCompiler comp = new ScriptCompiler();
		comp.macroTypes.put("*", new DaDaDa_DaArrayArgMacroType(MultiplyOutDaDaDa_Da.class));
		assertEquals( new MultiplyOutDaDaDa_Da(new FunctionDaDaDa_Da[] {
			new Constant_Da(2), new Constant_Da(3)
		}), comp.compile("2 * 3") );
	}

	public void testCompileFunction2() {
		PerlinDaDaDa_Da perlin = new PerlinDaDaDa_Da();
		ScriptCompiler comp = new ScriptCompiler();
		comp.macroTypes.put("*", new DaDaDa_DaArrayArgMacroType(MultiplyOutDaDaDa_Da.class));
		comp.macroTypes.put("+", new DaDaDa_DaArrayArgMacroType(AddOutDaDaDa_Da.class));
		comp.macroTypes.put("max", new DaDaDa_DaArrayArgMacroType(MaxOutDaDaDa_Da.class));
		comp.macroTypes.put("perlin", new ConstantMacroType(perlin));
		assertEquals(
			new AddOutDaDaDa_Da(new FunctionDaDaDa_Da[] {
				new MaxOutDaDaDa_Da(new FunctionDaDaDa_Da[] {
					perlin,
					new Constant_Da(0)
				}),
				new MultiplyOutDaDaDa_Da(new FunctionDaDaDa_Da[] {
					new Constant_Da(2),
					perlin
				})
			}),
			comp.compile("max(perlin,0) + 2 * perlin")
		);
	}
}
