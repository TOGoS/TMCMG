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
 * expression := "(" expression ")"
 * expression := expression "(" expression ("," expression)
 *            
 */


public class NewParser
{
	protected TNLTokenizer tokenizer;
	
	public NewParser( TNLTokenizer t ) {
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
	
}
