package togos.noise2.lang;

import java.util.HashMap;

public class Operators
{
	static HashMap PRECEDENCE = new HashMap();
	static int COMMA_PRECEDENCE = 10;
	static {
		PRECEDENCE.put("**", new Integer(60));
		PRECEDENCE.put("*",  new Integer(50));
		PRECEDENCE.put("/",  new Integer(40));
		PRECEDENCE.put("+",  new Integer(30));
		PRECEDENCE.put("-",  new Integer(25));
		PRECEDENCE.put(">",  new Integer(20));
		PRECEDENCE.put("<",  new Integer(20));
		PRECEDENCE.put(">=", new Integer(20));
		PRECEDENCE.put("<=", new Integer(20));
		PRECEDENCE.put("==", new Integer(18));
		PRECEDENCE.put("!=", new Integer(18));
		PRECEDENCE.put("and", new Integer(17));
		PRECEDENCE.put("or",  new Integer(16));
		PRECEDENCE.put("=",  new Integer(15));
		PRECEDENCE.put(",",  new Integer(COMMA_PRECEDENCE));
		PRECEDENCE.put(";",  new Integer( 5));
	}
}
