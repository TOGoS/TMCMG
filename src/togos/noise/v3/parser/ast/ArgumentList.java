package togos.noise.v3.parser.ast;

import togos.lang.SourceLocation;

public class ArgumentList extends ASTNode
{
	static final class Argument {
		String name;
		ASTNode value;
		public Argument( String name, ASTNode value ) {
			this.name = name;
			this.value = value;
		}
	}
	
	final Argument[] arguments;
	
	public ArgumentList( Argument[] arguments, SourceLocation sLoc ) {
		super(sLoc);
		this.arguments = arguments;
	}
}
