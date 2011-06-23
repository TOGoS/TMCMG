package togos.noise2.lang;

import java.io.IOException;


/*
 * Syntax:
 * 
 * symbol := bareword
 * symbol := "`" text "`"
 * 
 * block := (definition ";")* expression
 * 
 * definition := symbol["(" symbol ("," symbol)* ")"] "=" expression
 * 
 * expression := symbol
 * expression := literal-string
 * expression := literal-number
 * expression := expression (operator expression)+
 * expression := "(" block ")"
 * expression := expression "(" argument ("," argument)* ")"
 * 
 * argument := symbol "@" expression
 * argument := expression "@" expression // maybe future support 
 * argument := expression
 */

/**
 * Replacement for TNLParser.
 * This and TNLExpressions should obsolete ASTNode and TNLParser and
 * the old macro system (will need to create a new macro system).
 */
public class TNLExpressionParser
{
	protected TNLTokenizer tokenizer;
	
	public TNLExpressionParser( TNLTokenizer t ) {
		this.tokenizer = t;
	}
	
	protected Token lastToken = null;
	
	protected Token readToken() throws IOException {
		if( lastToken == null ) {
			return tokenizer.readToken();
		}
		Token t = lastToken;
		lastToken = null;
		return t;
	}
	
	protected void unreadToken( Token t ) {
		this.lastToken = t;
	}
	
	protected Token peekToken() throws IOException {
		if( lastToken == null ) {
			return lastToken = tokenizer.readToken();
		} else {
			return lastToken;
		}
	}
	
	protected boolean isSymbol( Token t ) {
		return t.quote == '`' || t.quote == 0;
	}
	
	public TNLExpression readAtomicExpression( TNLExpression parent ) throws IOException, ParseError {
		Token t = readToken();
		if( t.quote == '"' ) {
			return new TNLLiteralExpression(t.value, t, parent);
		} else if( t.quote == 0 && "(".equals(t.value) ) {
			TNLExpression expr = readBlock( parent );
			Token endToken = readToken();
			if( endToken == null ) {
				throw new ParseError("Encountered end of file before end of expression started at "+ParseUtil.formatLocation(t), tokenizer.getCurrentLocation());
			}
			if( endToken.quote != 0 || !")".equals(endToken.value) ) {
				throw new ParseError("Expected ')', but encountered "+t.toSource(), t);
			}
			return expr;
		} else {
			return new TNLSymbolExpression(t.value, t, parent);
		}
	}
	
	public TNLExpression readExpression( int gtPrecedence, TNLExpression parent ) throws IOException, ParseError {
		TNLExpression first = readAtomicExpression( parent );
		return first;
	}
	
	public TNLExpression readBlock( TNLExpression parent ) throws IOException, ParseError {
		TNLExpression e1 = readExpression( Operators.EQUALS_PRECEDENCE, parent );
		Token t = peekToken();
		if( t == null || (isSymbol(t) && t.value == ")") ) return e1;
		return null;
	}
}
