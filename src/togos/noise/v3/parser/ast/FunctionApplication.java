package togos.noise.v3.parser.ast;

import togos.lang.SourceLocation;

public class FunctionApplication extends ASTNode
{
	public final ASTNode function;
	public final ArgumentList argumentList;
	
	public FunctionApplication( ASTNode function, ArgumentList args, SourceLocation sLoc ) {
		super(sLoc);
		this.function = function;
		this.argumentList = args;
	}
}
