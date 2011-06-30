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

/*
 * Semantics:
 * 
 * A symbol by itself will be interpreted as an application with no arguments
 * after all substitutions are done.  So:
 * 
 *   foo
 * 
 * is equivalent to
 * 
 *   foo()
 * 
 * but
 * 
 *   foo = bar()
 *   foo()
 *   
 * is not allowed, since it is equivalent to
 * 
 *   bar()()
 * 
 * This MAY be allowed in the future if I decide to support currying.
 * 
 * Other function questions:
 * 
 * What does this compile to?
 * 
 *   get-function(a) = if( a > 0, b -> b + 1, b -> b - 1 );
 *   
 *   get-function(x)(y)
 *   
 * Good question, let's see...
 * 
 *   ( a -> if( a > 0, b -> b + 1, b -> b - 1 ) )(x)(y)
 *   
 * becomes
 * 
 *   if( y > 0, b -> b + 1, b -> b - 1 )(y)
 *   
 * And since if(..) doesn't itself take any more arguments, this is INVALID.
 * Which makes sense because otherwise you could have different branches of
 * the if return functions with different argument lists!
 * 
 * Instead, write it like this:
 *  
 *   get-function(a) = b -> if( a > 0, b + 1, b - 1 );
 * 
 * Though in that case you might as well just write a function that takes 2 arguments.
 * The reason I don't allow this to be called like:
 * 
 *   get-function(1,2)
 * 
 * is because I don't want to have to figure out what to do with that
 * extra parameter (the 2) while evaluating the application of get-function.
 * 
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
	
	protected boolean isDelimiter( Token t, String d ) {
		return t.quote == 0 && d.equals(t.value);
	}
	
	protected boolean isSymbol( Token t ) {
		return t.quote == '`' || t.quote == 0;
	}
	
	public TNLExpression readAtomicExpression( TNLExpression parent ) throws IOException, ParseError {
		Token t = readToken();
		if( t == null || isDelimiter(t, ";") || isDelimiter(t, ")") ) {
			throw new ParseError("Expected expression but found "+t, tokenizer.getCurrentLocation());
		} else if( t.quote == '"' ) {
			return new TNLLiteralExpression(t.value, t, parent);
		} else if( isDelimiter(t,"(") ) {
			TNLExpression expr = readBlock( t, parent );
			Token endToken = readToken();
			if( endToken == null ) {
				throw new ParseError("Encountered end of file before end of expression started at "+ParseUtil.formatLocation(t), tokenizer.getCurrentLocation());
			}
			if( !isDelimiter(endToken, ")") ) {
				throw new ParseError("Expected ')', but encountered "+endToken.toSource(), t);
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
	
	public TNLExpression readBlock( SourceLocation begin, TNLExpression parent ) throws IOException, ParseError {
		TNLBlockExpression block = new TNLBlockExpression(begin, parent);
		
		Token t = peekToken();
		while( t != null && !isDelimiter(t, ")") ) {
			if( isDelimiter(t,";") ) { readToken(); t = peekToken(); continue; } // skip dem semicolons! 
			
			TNLExpression e1 = readExpression( Operators.EQUALS_PRECEDENCE, block );
			t = peekToken();
			if( t == null || isDelimiter(t, ";") || isDelimiter(t, ")") ) {
				if( block.value == null ) {
					block.value = e1;
				} else {
					throw new ParseError("Block seems to have more than one value", e1);
				}
			} else if( isDelimiter(t, "=") ) {
				t = readToken(); // skip past the =
				TNLExpression key = e1;
				TNLExpression value = readExpression(Operators.EQUALS_PRECEDENCE, block);
				String keyName;
				if( key instanceof TNLSymbolExpression ) {
					keyName = ((TNLSymbolExpression)key).symbol;
				} else {
					throw new ParseError("Complex lvalues not yest supported", key);
				}
				block.definitions.put(keyName, value);
			} else {
				throw new ParseError("Unexpected token "+t.toSource()+" during block parsing", t);
			}
			t = peekToken();
		}
		return block;
	}
}
