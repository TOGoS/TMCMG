package togos.noise.v3.parser.ast;

import togos.noise.v3.parser.Token;

public class OperatorApplication extends ASTNode
{
	public final Token operator;
	public final ASTNode n1, n2;
	
	public OperatorApplication( Token operator, ASTNode n1, ASTNode n2 ) {
		super(operator);
		this.operator = operator;
		this.n1 = n1; this.n2 = n2;
	}
	
	public String toString() {
		return "(" + n1.toString() + " " + operator.toString() + " " + n2.toString() + ")";
	}
}
