package togos.noise2.lang;

import java.util.Map;

public class NodeWithScope
{
	public ASTNode node;
	public Map scope;
	
	public NodeWithScope( ASTNode node, Map scope ) {
		this.node = node;
		this.scope = scope;
	}
}
