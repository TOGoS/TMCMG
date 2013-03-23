package togos.noise.v1.lang.macro;

import togos.noise.v1.lang.ASTNode;
import togos.noise.v1.lang.CompileError;
import togos.noise.v1.lang.TNLCompiler;

/**
 * A macro type that takes no arguments and always expands to the same
 * expression.  (The resulting expression does not necessarily represent
 * a constant.)
 */
public class ConstantMacroType implements MacroType
{
	protected Object value;
	public ConstantMacroType( Object value ) {
		this.value = value;
	}
	public Object instantiate(TNLCompiler c, ASTNode sn) throws CompileError {
		if( sn.arguments.size() > 0 ) {
			throw new CompileError(sn.macroName + " takes no arguments, "+sn.arguments.size()+" given", sn);
		}
		return value;
	}
}
