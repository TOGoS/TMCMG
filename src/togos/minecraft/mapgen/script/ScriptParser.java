package togos.minecraft.mapgen.script;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ScriptParser
{
	static class ScriptNode {
		String macroName;
		List arguments;
		
		public ScriptNode( String macroName, List arguments ) {
			this.macroName = macroName;
			this.arguments = arguments;
		}
		
		public boolean equals( Object o ) {
			if( o instanceof ScriptNode ) {
				ScriptNode osn = (ScriptNode)o;
				return macroName.equals( osn.macroName ) &&
					arguments.equals( osn.arguments );
			}
			return false;
		}
	}
	
	static HashMap operatorPrecedence = new HashMap();
	static {
		operatorPrecedence.put("**", new Integer(50));
		operatorPrecedence.put("*",  new Integer(40));
		operatorPrecedence.put("/",  new Integer(30));
		operatorPrecedence.put("-",  new Integer(20));
		operatorPrecedence.put("+",  new Integer(10));
		operatorPrecedence.put(",",  new Integer( 5));
	}
	
	protected ScriptTokenizer tokenizer;
	
	protected String lastToken = null;
	
	protected String readToken() throws IOException {
		if( lastToken == null ) {
			return tokenizer.readToken();
		}
		String t = lastToken;
		lastToken = t;
		return t;
	}
	
	protected void unreadToken( String t ) {
		this.lastToken = t;
	}
	
	protected ScriptNode readMacro() throws IOException, ParseException {
		String t = readToken();
		String macroName = t;
		List arguments = new ArrayList();
		t = readToken();
		if( "(".equals(t) ) {
			t = readToken();
			while( !")".equals(t) ) {
				unreadToken(t);
				arguments.add(readNode(5));
				t = readToken();
				if( !",".equals(t) ) {
					unreadToken(t);
				}
			}
			if( !")".equals(t) ) {
				throw new ParseException("Expected ')', but got '"+t+"'", 0);
			}
		} else {
			unreadToken(t);
		}
		return new ScriptNode(macroName, arguments);
	}
	
	protected ScriptNode readAtomicNode() throws IOException, ParseException {
		String t = readToken();
		if( t == null ) return null;
		if( "(".equals(t) ) {
			ScriptNode sn = readNode(0);
			t = readToken();
			if( !")".equals(t) ) {
				throw new ParseException("Expected ')', but got '"+t+"'", 0);
			}
			return sn;
		}
		unreadToken(t);
		return readMacro();
	}
	
	public ScriptNode readNode( ScriptNode first, int gtPrecedence ) throws IOException, ParseException {
		String op = readToken();
		if( ")".equals(op) || op == null ) {
			unreadToken(op);
			return first;
		}
		Integer oPrec = (Integer)operatorPrecedence.get(op);
		if( oPrec == null ) {
			throw new ParseException("Invalid operator '"+op+"'", 0);
		}
		while( oPrec.intValue() >= gtPrecedence ) {
			if( oPrec.intValue() > gtPrecedence ) {
				first = readNode( first, oPrec.intValue() );
			}
			// todo: yaddah yaddah not sure
		}
		unreadToken(op);
		return first;
	}
	
	public ScriptNode readNode( int gtPrecedence ) throws IOException, ParseException {
		ScriptNode first = readAtomicNode();
		return readNode( first, gtPrecedence );
	}
}
