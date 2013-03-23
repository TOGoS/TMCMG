package togos.noise.v1.lang.macro;

import togos.noise.v1.func.FunctionDaDaDa_Da;
import togos.noise.v1.lang.ASTNode;
import togos.noise.v1.lang.CompileError;
import togos.noise.v1.lang.FunctionUtil;
import togos.noise.v1.lang.TNLCompiler;

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
