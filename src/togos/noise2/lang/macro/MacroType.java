package togos.noise2.lang.macro;

import togos.noise2.lang.ASTNode;
import togos.noise2.lang.CompileError;
import togos.noise2.lang.TNLCompiler;

public interface MacroType {
	public Object instantiate( TNLCompiler c, ASTNode sn ) throws CompileError;
}
