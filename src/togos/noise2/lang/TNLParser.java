package togos.noise2.lang;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TNLParser
{
	protected TNLTokenizer tokenizer;
	
	public TNLParser( TNLTokenizer t ) {
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
	
	protected String macroName( Token t ) {
		switch( t.quote ) {
		case( '"' ): return '"' + t.value;
		default: return t.value;
		}
	}
	
	protected ASTNode readMacro() throws IOException, ParseError {
		Token t0 = readToken();
		String macroName = macroName(t0);
		List arguments = new ArrayList();
		Token t = readToken();
		if( t != null && "(".equals(t.value) ) {
			t = readToken();
			while( t != null && !")".equals(t.value) ) {
				unreadToken(t);
				
				ASTNode argNode = readNode(Operators.COMMA_PRECEDENCE+1);
				// Argnode should never be null here?
				// But just in case I change something:
				if( argNode != null ) arguments.add(argNode);
				
				t = readToken();
				if( t != null && !",".equals(t.value) ) {
					unreadToken(t);
				}
				t = readToken(); // next token after the comma
			}
			if( t == null ) {
				throw new ParseError("Expected ')', but reached end of source.", new BaseSourceLocation(t0.getSourceFilename(), -1, -1));
			} else if( !")".equals(t.value) ) {
				throw new ParseError("Expected ')', but got '"+t+"'.", t);
			}
		} else {
			unreadToken(t);
		}
		return new ASTNode(macroName, arguments, t0);
	}
	
	protected ASTNode readAtomicNode() throws IOException, ParseError {
		Token t = readToken();
		if( ")".equals(t) ) {
			throw new RuntimeException("Tried to read an expression starting with ')'");
		}
		if( t == null ) return null;
		if( "(".equals(t.value) ) {
			ASTNode sn = readNode(0);
			t = readToken();
			if( t == null ) {
				throw new ParseError("Expected ')', but reached end of source.", null);
			} else if( !")".equals(t.value) ) {
				throw new ParseError("Expected ')', but got '"+t.value+"'", t);
			}
			return sn;
		}
		unreadToken(t);
		return readMacro();
	}
	
	public ASTNode readNode( ASTNode first, int gtPrecedence ) throws IOException, ParseError {
		Integer oPrec;
		Token op;
		
		readFirst: while( true ) {
			op = peekToken();
			if( op == null || ")".equals(op.value) ) {
				return first;
			}

			// Otherwise we must be encountering an operator!
			oPrec = (Integer)Operators.PRECEDENCE.get(op.value);
			if( oPrec == null ) {
				throw new ParseError("Invalid operator '"+op.value+"' (forget a semicolon?)", op);
			}
			
			if( oPrec.intValue() < gtPrecedence ) {
				return first;
			}
			
			if( oPrec.intValue() > gtPrecedence ) {
				// OK, read first node at higher precedence, then.
				first = readNode( first, oPrec.intValue() );
				// And then try again at this precedence.
				continue readFirst;
			}
			
			// If we get here, oPrec is exactly the precedence we are looking to gobble!
			break readFirst;
		}
		
		ArrayList arguments = new ArrayList();
		arguments.add(first);
		String name = op.value;
		SourceLocation sl = op;
		
		readArguments: while( op != null && name.equals(op.value) ) {
			readToken(); // skip over op
			Token nextExprToken = peekToken();
			if( nextExprToken == null || ")".equals(nextExprToken.value) ) {
				break readArguments;
			}
			arguments.add( readNode( gtPrecedence+1 ) );
			op = peekToken();
		}
		
		return new ASTNode(name, arguments, sl);
	}
	
	public ASTNode readNode( int gtPrecedence ) throws IOException, ParseError {
		ASTNode first = readAtomicNode();
		return readNode( first, gtPrecedence );
	}
}
