package togos.noise2.function;

import junit.framework.TestCase;
import togos.noise2.lang.TNLCompiler;
import togos.noise2.lang.macro.NoiseMacros;

public abstract class NoiseFunctionTest extends TestCase
{
	public TNLCompiler mkCompiler() {
		TNLCompiler comp = new TNLCompiler();
		comp.macroTypes.putAll(NoiseMacros.stdNoiseMacros);
		return comp;
	}
	
	protected void assertEquals( double a, double b ) {
		assertEquals( "Expected "+a+" but was "+b+";", (long)(a*1024), (long)(b*1024) );
	}

	TNLCompiler comp = mkCompiler();
}
