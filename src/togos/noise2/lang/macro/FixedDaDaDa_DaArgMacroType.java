package togos.noise2.lang.macro;

import togos.noise2.function.FunctionDaDaDa_Da;
import togos.noise2.lang.ASTNode;
import togos.noise2.lang.CompileError;
import togos.noise2.lang.FunctionUtil;
import togos.noise2.lang.TNLCompiler;

public class FixedDaDaDa_DaArgMacroType extends FixedArgMacroType
{
	FunctionDaDaDa_Da[] defaultArgs;
	
	/**
	 * @param functionClass class of expression that this macro will instantiate
	 * @param argCount how many arguments functionClass's constructor expects
	 * @param defaultArgs if ZERO arguments are provided, but argCount > 0, these will be used
	 */
	public FixedDaDaDa_DaArgMacroType( Class functionClass, int argCount, FunctionDaDaDa_Da[] defaultArgs ) {
		super( functionClass, FunctionDaDaDa_Da.class, argCount );
		if( defaultArgs != null && defaultArgs.length < argCount ) {
			throw new RuntimeException("Length of default args array must be at least the number of required args ("+argCount+")");
		}
		this.defaultArgs = defaultArgs;
	}
	
	protected Object compileArgument(TNLCompiler c, ASTNode sn) throws CompileError {
		Object r = c.compile(sn);
		return FunctionUtil.toDaDaDa_Da(r, sn);
	}
	
	protected Object[] compileArguments( TNLCompiler c, ASTNode sn ) throws CompileError {
		if( argCount > 0 && sn.arguments.size() == 0 && defaultArgs != null ) {
			return defaultArgs;
		} else {
			return super.compileArguments(c, sn);
		}
	}
}
