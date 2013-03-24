package togos.noise.v3.parser.ast;

import java.util.ArrayList;

import togos.lang.SourceLocation;

public class BlockNode extends ASTNode
{
	public final ArrayList<ASTNode> statements = new ArrayList<ASTNode>();
	
	public BlockNode(SourceLocation loc) {
		super(loc);
	}
}
