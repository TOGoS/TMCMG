package togos.minecraft.mapgen.script;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ScriptParser
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
	
	protected ScriptTokenizer tokenizer;
	
	public ScriptParser( Reader r ) {
		this.tokenizer = new ScriptTokenizer(r);
	}
	
	protected String lastToken = null;
	
	protected String readToken() throws IOException {
		if( lastToken == null ) {
			return tokenizer.readToken();
		}
		String t = lastToken;
		lastToken = null;
		return t;
	}
	
	protected void unreadToken( String t ) {
		this.lastToken = t;
	}
	
	protected ScriptNode readMacro() throws IOException {
		String t = readToken();
		String macroName = t;
		List arguments = new ArrayList();
		t = readToken();
		if( "(".equals(t) ) {
			t = readToken();
			while( !")".equals(t) ) {
				unreadToken(t);
				arguments.add(readNode(COMMA_PRECEDENCE+1));
				t = readToken();
				if( !",".equals(t) ) {
					unreadToken(t);
				}
				t = readToken(); // next token after the comma
			}
			if( !")".equals(t) ) {
				throw new ParseError("Expected ')', but got '"+t+"'", new Token("","",0,0));
			}
		} else {
			unreadToken(t);
		}
		return new ScriptNode(macroName, arguments);
	}
	
	protected ScriptNode readAtomicNode() throws IOException {
		String t = readToken();
		if( t == null ) return null;
		if( "(".equals(t) ) {
			ScriptNode sn = readNode(0);
			t = readToken();
			if( !")".equals(t) ) {
				throw new ParseError("Expected ')', but got '"+t+"'", null);
			}
			return sn;
		}
		unreadToken(t);
		return readMacro();
	}
	
	public ScriptNode readNode( ScriptNode first, int gtPrecedence ) throws IOException {
		String op = readToken();
		if( ")".equals(op) || op == null ) {
			unreadToken(op);
			return first;
		}
		Integer oPrec = (Integer)operatorPrecedence.get(op);
		if( oPrec == null ) {
			throw new ParseError("Invalid operator '"+op+"'", new Token(op,op,0,0)); // TODO: fix token
		}
		if( oPrec.intValue() < gtPrecedence ) {
			unreadToken(op);
			return first;
		} else if( oPrec.intValue() == gtPrecedence ) {
			ArrayList arguments = new ArrayList();
			arguments.add(first);
			String fop = op;
			while( oPrec.intValue() == gtPrecedence ) {
				arguments.add( readNode( gtPrecedence+1 ) );
				fop = readToken();
				if( ")".equals(fop) || fop == null ) {
					oPrec = new Integer(0);
				} else {
					oPrec = (Integer)operatorPrecedence.get(fop);
					if( oPrec == null ) {
						throw new ParseError("Invalid operator '"+fop+"'", new Token(op,op,0,0));
					}
				}
			}
			unreadToken(fop);
			first = new ScriptNode(op, arguments);
		} else {
			unreadToken(op);
			first = readNode( first, oPrec.intValue() );
			first = readNode( first, gtPrecedence );
		}
		return first;
	}
	
	public ScriptNode readNode( int gtPrecedence ) throws IOException {
		ScriptNode first = readAtomicNode();
		return readNode( first, gtPrecedence );
	}
}
