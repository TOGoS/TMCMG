package togos.noise2.lang.macro;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;

import togos.noise2.lang.ASTNode;
import togos.noise2.lang.CompileError;
import togos.noise2.lang.TNLCompiler;

public abstract class ArrayArgMacroType implements MacroType
{
	Class compiledClass;
	Class argClass;
	
	public ArrayArgMacroType( Class compiledClass, Class argClass ) {
		this.compiledClass = compiledClass;
		this.argClass = argClass;
	}
	
	protected abstract Object compileArgument( TNLCompiler c, ASTNode sn );	
	
	public Object instantiate(TNLCompiler c, ASTNode sn) {
		int count = sn.arguments.size();
		Object fargs = Array.newInstance(argClass, count);
		for( int i=0; i<count; ++i  ) {
			Array.set(fargs, i, compileArgument(c, (ASTNode)sn.arguments.get(i)));
		}
		try {
			return compiledClass.getConstructor(new Class[]{fargs.getClass()}).newInstance(new Object[]{fargs});
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
