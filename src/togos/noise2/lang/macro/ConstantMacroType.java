package togos.noise2.lang.macro;

import togos.noise2.lang.ASTNode;
import togos.noise2.lang.CompileError;
import togos.noise2.lang.TNLCompiler;

public class ConstantMacroType implements MacroType
{
	protected Object value;
	public ConstantMacroType( Object value ) {
		this.value = value;
	}
	public Object instantiate(TNLCompiler c, ASTNode sn) {
		if( sn.arguments.size() > 0 ) {
			throw new CompileError(sn.macroName + " takes no arguments, "+sn.arguments.size()+" given", sn);
		}
		return value;
	}
}
