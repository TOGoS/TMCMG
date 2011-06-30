package togos.noise2.lang;

import java.util.HashMap;

public class Operators
{
	static HashMap PRECEDENCE = new HashMap();
	static int COMMA_PRECEDENCE = 10;
	static int EQUALS_PRECEDENCE = 15;
	static int APPLY_PRECEDENCE = 70;
	static {
		PRECEDENCE.put("**", new Integer(60));
		
		PRECEDENCE.put("*",  new Integer(50));
		PRECEDENCE.put("/",  new Integer(50));
		
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
		
		PRECEDENCE.put("->", new Integer(16));
		PRECEDENCE.put("=",  new Integer(EQUALS_PRECEDENCE));
		PRECEDENCE.put(",",  new Integer(COMMA_PRECEDENCE));
		PRECEDENCE.put(";",  new Integer( 5));
	}
}
