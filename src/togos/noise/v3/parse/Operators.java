package togos.noise.v3.parse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Operators
{
	public final static HashMap<String,Integer> PRECEDENCE = new HashMap<String,Integer>();
	
	public final static int COMMA_PRECEDENCE;
	public final static int AT_PRECEDENCE;
	public final static int EQUALS_PRECEDENCE;
	public final static int APPLY_PRECEDENCE = 80;
	
	/* 
	 * Trying to jive as much as possible with
	 * http://en.wikipedia.org/wiki/Order_of_operations#The_standard_order_of_operations
	 */
	
	static {
		PRECEDENCE.put(".",  new Integer(70));
		PRECEDENCE.put("**", new Integer(60));
		
		PRECEDENCE.put("*",  new Integer(50));
		PRECEDENCE.put("/",  new Integer(50));
		PRECEDENCE.put("%",  new Integer(50));
		
		PRECEDENCE.put("+",  new Integer(40));
		PRECEDENCE.put("-",  new Integer(40));
		
		PRECEDENCE.put("<<",  new Integer(39));
		PRECEDENCE.put(">>",  new Integer(39));
		
		/*
		 * In C these go below the comparison operators,
		 * but I never liked that because who really wants to
		 * do bitwise operations on results of comparisons??
		 *
		 * To validate this decision, it seems the Rust developers did it the same way:
		 * http://static.rust-lang.org/doc/rust.html#operator-precedence
		 * 
		 * Also as in Rust (and most other languages with infix operators),
		 * operators at the same precedence level are evaluated left-to-right. 
		 * e.g.  x <op> y <op> z == (x <op> y) <op> z 
		 */ 
		PRECEDENCE.put("&",  new Integer(36));
		PRECEDENCE.put("^",  new Integer(35));
		PRECEDENCE.put("|",  new Integer(34));
		
		PRECEDENCE.put(">",  new Integer(31));
		PRECEDENCE.put("<",  new Integer(31));
		PRECEDENCE.put(">=", new Integer(31));
		PRECEDENCE.put("<=", new Integer(31));
		PRECEDENCE.put("==", new Integer(30));
		PRECEDENCE.put("!=", new Integer(30));
		
		PRECEDENCE.put("&&", new Integer(25));
		PRECEDENCE.put("^^", new Integer(24));
		PRECEDENCE.put("||", new Integer(23));
		
		PRECEDENCE.put("and",new Integer(22));
		PRECEDENCE.put("xor",new Integer(21));
		PRECEDENCE.put("or", new Integer(20));
		
		PRECEDENCE.put("->", new Integer(                     16 ));
		PRECEDENCE.put("=",  new Integer( EQUALS_PRECEDENCE = 15 ));
		PRECEDENCE.put("@",  new Integer(     AT_PRECEDENCE = 12 ));
		PRECEDENCE.put(",",  new Integer(  COMMA_PRECEDENCE = 10 ));
		PRECEDENCE.put(";",  new Integer(                      5 ));
	}
	
	public static String dump( String indent ) {
		String operatorList = "";
		List<Map.Entry<String,Integer>> operatorPrecedence = new ArrayList<Map.Entry<String,Integer>>(Operators.PRECEDENCE.entrySet());
		Collections.sort( operatorPrecedence, new Comparator<Map.Entry<String,Integer>>() {
			@Override public int compare( Entry<String, Integer> a, Entry<String, Integer> b ) {
				int va = b.getValue(), vb = a.getValue(); // Sort in reverse!
				return va < vb ? -1 : va > vb ? 1 : 0; 
            }
		});
		int lastPrec = 999;
		for( Map.Entry<String,Integer> opPrec : operatorPrecedence ) {
			if( operatorList.length() == 0 ) {
				operatorList += "\t" + indent + opPrec.getKey();
			} else if( opPrec.getValue() == lastPrec ) {
				operatorList += " " + opPrec.getKey();
			} else {
				operatorList += "\n" + indent + opPrec.getKey();
			}
			lastPrec = opPrec.getValue();
		}
		return operatorList;
	}
}
