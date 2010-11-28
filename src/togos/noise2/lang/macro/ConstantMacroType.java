package togos.noise2.lang.macro;

import togos.noise2.lang.ASTNode;
import togos.noise2.lang.MacroType;
import togos.noise2.lang.TNLCompiler;

public class ConstantMacroType implements MacroType
{
	protected Object value;
	public ConstantMacroType( Object value ) {
		this.value = value;
	}
	public Object instantiate(TNLCompiler c, ASTNode sn) {
		return value;
	}
}
