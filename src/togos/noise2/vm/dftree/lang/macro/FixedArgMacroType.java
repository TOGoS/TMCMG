package togos.noise2.vm.dftree.lang.macro;

import java.lang.reflect.InvocationTargetException;

import togos.noise2.lang.CompileError;
import togos.noise2.vm.dftree.lang.ASTNode;
import togos.noise2.vm.dftree.lang.TNLCompiler;

public abstract class FixedArgMacroType implements MacroType
{
	Class compiledClass;
	Class argClass;
	int argCount;
	
	public FixedArgMacroType( Class compiledClass, Class argClass, int argCount ) {
		this.compiledClass = compiledClass;
		this.argClass = argClass;
		this.argCount = argCount;
	}
	
	protected abstract Object compileArgument( TNLCompiler c, ASTNode sn ) throws CompileError;	
	
	protected Object[] compileArguments( TNLCompiler c, ASTNode sn ) throws CompileError {
		if( sn.arguments.size() != argCount ) {
			throw new CompileError( sn.macroName + " requires "+argCount+" arguments, given "+sn.arguments.size()+".", sn );
		}
		
		Object[] args = new Object[argCount];
		for( int i=0; i<argCount; ++i ) {
			args[i] = compileArgument(c, (ASTNode)sn.arguments.get(i) );
		}
		return args;
	}
	
	public Object instantiate(TNLCompiler c, ASTNode sn) throws CompileError {
		Object[] args = compileArguments(c, sn);
		
		Class[] argClasses = new Class[argCount];
		for( int i=0; i<argCount; ++i ) {
			argClasses[i] = argClass;
		}
		
		try {
			return compiledClass.getConstructor(argClasses).newInstance(args);
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
