package togos.noise2.lang.macro;

import togos.noise2.lang.ASTNode;
import togos.noise2.lang.CompileError;
import togos.noise2.lang.FunctionUtil;
import togos.noise2.lang.TNLCompiler;
import togos.noise2.vm.dftree.func.FunctionDaDaDa_Da;

public class DaDaDa_DaArrayArgMacroType extends ArrayArgMacroType
{
	public DaDaDa_DaArrayArgMacroType( Class functionClass ) {
		super( functionClass, FunctionDaDaDa_Da.class );
	}
	
	protected Object compileArgument(TNLCompiler c, ASTNode sn) throws CompileError {
		Object r = c.compile(sn);
		return FunctionUtil.toDaDaDa_Da(r, sn);
	}
}
