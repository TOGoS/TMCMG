package togos.noise.v3.parser.ast;

import togos.noise.v3.parser.Token;

public class SymbolNode extends ASTNode
{
	public final String text;
	
	public SymbolNode( Token t ) {
		super(t);
		this.text = t.text;
	}
	
	public String toString() {
		return this.text;
	}
}
