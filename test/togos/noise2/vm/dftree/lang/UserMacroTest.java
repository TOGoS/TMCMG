package togos.noise2.vm.dftree.lang;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;
import togos.noise2.lang.CompileError;
import togos.noise2.lang.ScriptError;
import togos.noise2.rewrite.ConstantFolder;
import togos.noise2.vm.dftree.func.Constant_Da;
import togos.noise2.vm.dftree.func.FunctionDaDaDa_Da;
import togos.noise2.vm.dftree.lang.FunctionUtil;
import togos.noise2.vm.dftree.lang.TNLCompiler;
import togos.noise2.vm.dftree.lang.macro.LanguageMacros;
import togos.noise2.vm.dftree.lang.macro.NoiseMacros;

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
	
	public void testCurriedFunction() throws ScriptError {
		FunctionDaDaDa_Da v1 = FunctionUtil.toDaDaDa_Da(
			comp.compile(
				"callmyfunc(f,a) = f(a);\n" +
				"addTheseTwo(n,m) = n + m;\n" +
				"apply2(funk,arg1,arg2) = funk(arg1,arg2);\n" +
				"apply2(addTheseTwo,4,5);"
			), null
		);
		v1 = (FunctionDaDaDa_Da)ConstantFolder.instance.rewrite(v1);
		assertTrue( v1 instanceof Constant_Da );
		assertTrue( ((Constant_Da)v1).value == 9 );
	}
	
	public void testCurriedFunction2() throws ScriptError {
		FunctionDaDaDa_Da v1 = FunctionUtil.toDaDaDa_Da(
			comp.compile(
				"callmyfunc(f,a) = f(a);\n" +
				"addTheseTwo(n,m) = n + m;\n" +
				"apply2(funk,arg1,arg2) = funk(arg1,arg2);\n" +
				"apply1(funk,arg1) = funk(arg1);\n" +
				"funkyApply(funk,arg1,arg2) = (\n" +
				"  funkyApplyA(arg2A) = apply2(funk,arg1,arg2A);\n" +
				"  apply1(funkyApplyA,arg2)\n" +
				");\n" +
				"funkyApply(addTheseTwo,4,5);"
			), null
		);
		v1 = (FunctionDaDaDa_Da)ConstantFolder.instance.rewrite(v1);
		assertTrue( v1 instanceof Constant_Da );
		assertTrue( ((Constant_Da)v1).value == 9 );
	}
	
	public void testUnsupportedCurrying() throws ScriptError {
		try {
			comp.compile(
				"addTheseTwo(n,m) = n + m;\n" +
				"apply1(funk,arg1) = funk(arg1);\n" +
				"apply1(addTheseTwo(4),5);"
			);
			fail("'apply1(addTheseTwo(4),5)' should have generated a 'unsupported currying' compile error");
		} catch( CompileError e ) {
		}
	}
}
