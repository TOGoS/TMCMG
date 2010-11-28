package togos.noise2.lang.macro;

import togos.noise2.function.FunctionDaDaDa_Da;
import togos.noise2.lang.ASTNode;
import togos.noise2.lang.FunctionUtil;
import togos.noise2.lang.TNLCompiler;

public class DaDaDa_DaArrayArgMacroType extends ArrayArgMacroType
{
	public DaDaDa_DaArrayArgMacroType( Class functionClass ) {
		super( functionClass, FunctionDaDaDa_Da.class );
	}
	
	protected Object compileArgument(TNLCompiler c, ASTNode sn) {
		Object r = c.compile(sn);
		return FunctionUtil.toDaDaDa_Da(r, sn);
	}
}
