package togos.noise2.lang;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import togos.noise2.function.Constant_Da;
import togos.noise2.function.FunctionDaDaDa_Da;
import togos.noise2.lang.macro.LanguageMacros;
import togos.noise2.lang.macro.NoiseMacros;
import togos.noise2.rewrite.ConstantFolder;

public class UserMacroTest extends TestCase
{
	TNLCompiler comp;
	
	public void setUp() {
		Map macroTypes = new HashMap();
		macroTypes.putAll(NoiseMacros.stdNoiseMacros);
		macroTypes.putAll(LanguageMacros.stdLanguageMacros);
		comp = new TNLCompiler(macroTypes);
	}
	
	public void testUserMacro() throws ScriptError {
		Object v1 = FunctionUtil.toDaDaDa_Da(comp.compile("foo = 12; foo"), null);
		assertTrue( v1 instanceof Constant_Da );
		assertTrue( ((Constant_Da)v1).value == 12 );
	}
	
	public void testDuplicateError() throws ScriptError {
		try {
			FunctionUtil.toDaDaDa_Da(comp.compile("foo = 12; foo = 13; foo"), null);
			fail("Duplicate foo def should have thrown CompileError");
		} catch( CompileError e ) {
		}
	}
	
	public void testNestedScope() throws ScriptError {
		Object v1 = FunctionUtil.toDaDaDa_Da(comp.compile("foo = (bar = 13; bar); foo"), null);
		assertTrue( v1 instanceof Constant_Da );
		assertTrue( ((Constant_Da)v1).value == 13 );
	}

	public void testUndefinedInDifferentScopeError() throws ScriptError {
		try {
			FunctionUtil.toDaDa_Da(comp.compile("foo = (bar = 13; bar); bar"), null);
			fail("Duplicate foo def should have thrown CompileError");
		} catch( CompileError e ) {
		}
	}
	
	public void testDuplicateDifferentScopeError() throws ScriptError {
		try {
			FunctionUtil.toDaDa_Da(comp.compile("foo = 12; (foo = 13; foo)"), null);
			fail("Duplicate foo def should have thrown CompileError");
		} catch( CompileError e ) {
		}
	}
	
	public void testUserMacroWithArgs() throws ScriptError {
		FunctionDaDaDa_Da v1 = FunctionUtil.toDaDaDa_Da(
			comp.compile("square(bar) = bar * bar; square(3)"), null
		);
		v1 = (FunctionDaDaDa_Da)ConstantFolder.instance.rewrite(v1);
		assertTrue( v1 instanceof Constant_Da );
		assertTrue( ((Constant_Da)v1).value == 9 );
	}
	
	public void testLexicalScope() throws ScriptError {
		FunctionDaDaDa_Da v1 = FunctionUtil.toDaDaDa_Da(
			comp.compile("add3(n) = n + 3; add5(n) = n + 5; add3(add5( (n = 1; n) ))"), null
		);
		v1 = (FunctionDaDaDa_Da)ConstantFolder.instance.rewrite(v1);
		assertTrue( v1 instanceof Constant_Da );
		assertTrue( ((Constant_Da)v1).value == 9 );
	}
}
