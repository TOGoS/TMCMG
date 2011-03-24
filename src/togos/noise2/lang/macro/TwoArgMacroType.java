package togos.noise2.lang.macro;

import java.lang.reflect.InvocationTargetException;

import togos.noise2.lang.ASTNode;
import togos.noise2.lang.CompileError;
import togos.noise2.lang.TNLCompiler;

public abstract class TwoArgMacroType implements MacroType
{
	Class compiledClass;
	Class argClass;
	
	public TwoArgMacroType( Class compiledClass, Class argClass ) {
		this.compiledClass = compiledClass;
		this.argClass = argClass;
	}
	
	protected abstract Object compileArgument( TNLCompiler c, ASTNode sn ) throws CompileError;	
	
	public Object instantiate(TNLCompiler c, ASTNode sn) throws CompileError {
		if( sn.arguments.size() != 2 ) {
			throw new CompileError( sn.macroName + " requires "+2+" arguments, given "+sn.arguments.size()+".", sn );
		}
		Object cArg0 = compileArgument(c, (ASTNode)sn.arguments.get(0));
		Object cArg1 = compileArgument(c, (ASTNode)sn.arguments.get(1));
		try {
			return compiledClass.getConstructor(new Class[]{argClass,argClass}).newInstance(new Object[]{cArg0,cArg1});
		} catch (IllegalArgumentException e) {
			throw new CompileError(e, sn);
		} catch (SecurityException e) {
			throw new CompileError(e, sn);
		} catch (InstantiationException e) {
			throw new CompileError(e, sn);
		} catch (IllegalAccessException e) {
			throw new CompileError(e, sn);
		} catch (InvocationTargetException e) {
			throw new CompileError(e, sn);
		} catch (NoSuchMethodException e) {
			throw new CompileError(e, sn);
		}
	}
}
