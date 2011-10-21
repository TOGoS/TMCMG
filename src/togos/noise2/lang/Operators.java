package togos.noise2.lang;

import java.util.HashMap;

public class Operators
{
	public final static HashMap PRECEDENCE = new HashMap();
	
	public final static int COMMA_PRECEDENCE;
	public final static int AT_PRECEDENCE;
	public final static int EQUALS_PRECEDENCE;
	public final static int APPLY_PRECEDENCE = 70;
	
	/* 
	 * Trying to jive as much as possible with
	 * http://en.wikipedia.org/wiki/Order_of_operations#The_standard_order_of_operations
	 */
	
	static {
		PRECEDENCE.put("**", new Integer(60));
		
		PRECEDENCE.put("*",  new Integer(50));
		PRECEDENCE.put("/",  new Integer(50));
		PRECEDENCE.put("%",  new Integer(50));
		
		PRECEDENCE.put("+",  new Integer(40));
		PRECEDENCE.put("-",  new Integer(40));
		
		PRECEDENCE.put(">",  new Integer(31));
		PRECEDENCE.put("<",  new Integer(31));
		PRECEDENCE.put(">=", new Integer(31));
		PRECEDENCE.put("<=", new Integer(31));
		PRECEDENCE.put("==", new Integer(30));
		PRECEDENCE.put("!=", new Integer(30));
		
		PRECEDENCE.put("and",new Integer(21));
		PRECEDENCE.put("or", new Integer(20));
		
		PRECEDENCE.put("->", new Integer(                     16 ));
		PRECEDENCE.put("=",  new Integer( EQUALS_PRECEDENCE = 15 ));
		PRECEDENCE.put("@",  new Integer(     AT_PRECEDENCE = 12 ));
		PRECEDENCE.put(",",  new Integer(  COMMA_PRECEDENCE = 10 ));
		PRECEDENCE.put(";",  new Integer(                      5 ));
	}
}
