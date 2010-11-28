package togos.noise2.lang;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TNLParser
{
	static HashMap operatorPrecedence = new HashMap();
	static int COMMA_PRECEDENCE = 10;
	static {
		operatorPrecedence.put("**", new Integer(50));
		operatorPrecedence.put("*",  new Integer(40));
		operatorPrecedence.put("/",  new Integer(30));
		operatorPrecedence.put("-",  new Integer(25));
		operatorPrecedence.put("+",  new Integer(20));
		operatorPrecedence.put("=",  new Integer(15));
		operatorPrecedence.put(",",  new Integer(COMMA_PRECEDENCE));
		operatorPrecedence.put(";",  new Integer( 5));
	}
	
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
	
	protected ASTNode readMacro() throws IOException {
		Token t0 = readToken();
		String macroName = t0.value;
		List arguments = new ArrayList();
		Token t = readToken();
		if( t != null && "(".equals(t.value) ) {
			t = readToken();
			while( t != null && !")".equals(t.value) ) {
				unreadToken(t);
				arguments.add(readNode(COMMA_PRECEDENCE+1));
				t = readToken();
				if( t != null && !",".equals(t.value) ) {
					unreadToken(t);
				}
				t = readToken(); // next token after the comma
			}
			if( t == null ) {
				throw new ParseError("Expected ')', but reached end of source.", t);
			} else if( !")".equals(t.value) ) {
				throw new ParseError("Expected ')', but got '"+t+"'.", t);
			}
		} else {
			unreadToken(t);
		}
		return new ASTNode(macroName, arguments, t0);
	}
	
	protected ASTNode readAtomicNode() throws IOException {
		Token t = readToken();
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
	
	public ASTNode readNode( ASTNode first, int gtPrecedence ) throws IOException {
		Token op = readToken();
		if( op == null || ")".equals(op.value) ) {
			unreadToken(op);
			return first;
		}
		Integer oPrec = (Integer)operatorPrecedence.get(op.value);
		if( oPrec == null ) {
			throw new ParseError("Invalid operator '"+op.value+"'", op);
		}
		if( oPrec.intValue() < gtPrecedence ) {
			unreadToken(op);
			return first;
		} else if( oPrec.intValue() == gtPrecedence ) {
			ArrayList arguments = new ArrayList();
			arguments.add(first);
			Token fop = op;
			while( oPrec.intValue() == gtPrecedence ) {
				arguments.add( readNode( gtPrecedence+1 ) );
				fop = readToken();
				if( fop == null || ")".equals(fop.value) ) {
					oPrec = new Integer(0);
				} else {
					oPrec = (Integer)operatorPrecedence.get(fop.value);
					if( oPrec == null ) {
						throw new ParseError("Invalid operator '"+fop.value+"'", fop);
					}
				}
			}
			unreadToken(fop);
			first = new ASTNode(op.value, arguments, op);
		} else {
			unreadToken(op);
			first = readNode( first, oPrec.intValue() );
			first = readNode( first, gtPrecedence );
		}
		return first;
	}
	
	public ASTNode readNode( int gtPrecedence ) throws IOException {
		ASTNode first = readAtomicNode();
		return readNode( first, gtPrecedence );
	}
}
