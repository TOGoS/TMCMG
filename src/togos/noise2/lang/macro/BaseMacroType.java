package togos.noise2.lang.macro;

import togos.noise2.lang.ASTNode;
import togos.noise2.lang.CompileError;
import togos.noise2.lang.TNLCompiler;

public abstract class BaseMacroType implements MacroType {
	protected abstract int getRequiredArgCount();
	
	protected abstract Object instantiate( ASTNode node, ASTNode[] argNodes, Object[] compiledArgs );
	
	public Object instantiate( TNLCompiler c, ASTNode sn ) {
		if( getRequiredArgCount() >= 0 && sn.arguments.size() != getRequiredArgCount() ) {
			throw new CompileError( sn.macroName + "requires "+getRequiredArgCount()+" arguments, given "+sn.arguments.size()+".", sn );
		}
		Object[] compiledArgs = new Object[sn.arguments.size()];
		ASTNode[] argNodes = new ASTNode[sn.arguments.size()];
		for( int i=0; i<compiledArgs.length; ++i ) {
			argNodes[i] = (ASTNode)sn.arguments.get(i);
			compiledArgs[i] = c.compile(argNodes[i]);
		}
		return instantiate( sn, argNodes, compiledArgs );
	}
}