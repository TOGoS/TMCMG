package togos.noise.v1.lang.macro;

import togos.lang.CompileError;
import togos.noise.v1.lang.ASTNode;
import togos.noise.v1.lang.TNLCompiler;

/**
 * A type of expression (can be thought of as a function)
 * that can appear in a TNL program and that can instantiate
 * itself for each usage in the program given the ASTNode
 * that triggered it (that ASTNode will include the 'name'
 * used and the arguments, themselves ASTNodes).
 */
public interface MacroType
{
	public Object instantiate( TNLCompiler c, ASTNode sn ) throws CompileError;
}
