package togos.noise.v3.parse.ast;

import togos.lang.SourceLocation;

public class VoidNode extends ASTNode {
	public VoidNode(SourceLocation sLoc) {
		super(sLoc);
	}
	
	public String toString() { return "()"; }
	public String toAtomicString() { return "()"; }
}
