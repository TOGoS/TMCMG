package togos.noise2.vm.dftree.func;

import junit.framework.TestCase;
import togos.noise2.vm.dftree.lang.TNLCompiler;
import togos.noise2.vm.dftree.lang.macro.NoiseMacros;

public abstract class NoiseFunctionTest extends TestCase
{
	public TNLCompiler mkCompiler() {
		TNLCompiler comp = new TNLCompiler();
		comp.macroTypes.putAll(NoiseMacros.stdNoiseMacros);
		return comp;
	}
	
	protected void assertEquals( double a, double b ) {
		assertTrue( "Expected "+a+" but was "+b+".", a == b );
	}

	protected void assertNotEquals( double a, double b ) {
		assertTrue( "Expected not "+a+", but was.", a != b );
	}

	TNLCompiler comp = mkCompiler();
}
