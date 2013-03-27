package togos.noise.v3.parser.ast;

import togos.lang.SourceLocation;
import togos.noise.v3.parser.Token;

public class InfixNode extends ASTNode
{
	public final String operator;
	public final ASTNode n1, n2;
	
	public InfixNode( Token operator, ASTNode n1, ASTNode n2 ) {
		super(operator);
		assert operator.type == Token.Type.SYMBOL;
		this.operator = operator.text;
		this.n1 = n1; this.n2 = n2;
	}

	public InfixNode( String operator, ASTNode n1, ASTNode n2, SourceLocation sLoc ) {
		super(sLoc);
		this.operator = operator;
		this.n1 = n1; this.n2 = n2;
	}
	
	protected static String spaceInfixOperator( String operator ) {
		if( ",".equals(operator) || ";".equals(operator) ) {
			return operator + " ";
		} else if( ".".equals(operator) ) {
			return operator;
		} else {
			return " " + operator + " ";
		}
	}
	
	protected static boolean isCollapsibleOperator( String op ) {
		return ",".equals(op) || ";".equals(op);
	}
	
	/** Include subnodes if they have the same operator */
	protected String subnodeToString( ASTNode n1 ) {
		return isCollapsibleOperator(operator) && (n1 instanceof InfixNode) && operator.equals(((InfixNode)n1).operator) ?
			n1.toString() : n1.toAtomicString(); 
	}
	
	public String toString() {
		return subnodeToString(n1) + spaceInfixOperator(operator) + subnodeToString(n2);
	}
}
