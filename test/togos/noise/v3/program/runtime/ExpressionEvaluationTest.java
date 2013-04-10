package togos.noise.v3.program.runtime;

import junit.framework.TestCase;
import togos.lang.BaseSourceLocation;
import togos.noise.v3.functions.MathFunctions;
import togos.noise.v3.parse.Parser;
import togos.noise.v3.parse.ProgramTreeBuilder;
import togos.noise.v3.program.structure.Expression;

public class ExpressionEvaluationTest extends TestCase
{
	static BaseSourceLocation TEST_LOC = new BaseSourceLocation("test script", 1, 1);
	static ProgramTreeBuilder ptb = new ProgramTreeBuilder();
	
	static final long bigNum = (long)Integer.MAX_VALUE+1;
	
	static final Object[] literalTests = new Object[] {
		true, "true",
		false, "false",
		1d, "1",
		(double)bigNum, String.valueOf(bigNum),
		(double)bigNum, "0x"+Long.toHexString(bigNum),
		(double)0x1234FFFF, "0b00010010001101001111111111111111",
		(double)-3, "-0x3",
		(double)-3, "-0b11",
		(double)+3, "+0x3",
		(double)+3, "+0b11",
	};
	
	static final Object[] logicTests = new Object[] {
		true,  "true  || false",
		false, "true  && false",
		true,  "true  ^^ false",
		false, "true  ^^ true ",
		false, "false ^^ false",
	};

	public ExpressionEvaluationTest() {
		super();
	}
	
	protected Expression<?> parse( String source ) throws Exception {
		return ptb.parseExpression(Parser.parse(source, TEST_LOC));
	}
	
	protected Object eval( String source ) throws Exception {
		return parse(source).bind(MathFunctions.CONTEXT).getValue();
	}

	public void testEvaluateIntegerConstant() throws Exception {
		assertEquals( Long.valueOf(1), eval("1") );
	}
	
	public void testEvaluateStringConstant() throws Exception {
		assertEquals( "foo\n\"bar baz\"", eval("\"foo\\n\\\"bar baz\\\"\""));
	}
	
	public void testEvaluateAddition() throws Exception {
		assertEquals( Double.valueOf(3), eval("1 + 2") );
	}
	
	public void testEvaluateModeAddition() throws Exception {
		assertEquals( Double.valueOf(10), eval("1 + 2 + 3 + 4") );
	}
	
	public void testArithmetic() throws Exception {
		assertEquals( Double.valueOf(-3), eval("(1 + 2) * (3 - 4)") );
	}
	
	public void testBlock() throws Exception {
		assertEquals( Double.valueOf(9), eval("a = 1 + 2; a * a") );
	}
	
	public void testApplyFunction() throws Exception {
		assertEquals( Double.valueOf(15), eval("f(x) = x + 5; f(10)") );
	}

	public void testApplyZeroArgFunction() throws Exception {
		assertEquals( Double.valueOf(6), eval("x = 1 ; f = () -> x + 5; f()") );
	}

	public void testApplyZeroArgFunction2() throws Exception {
		assertEquals( Double.valueOf(6), eval("x = 1 ; f() = x + 5; f()") );
	}

	public void testApplyFunctionWithNamedArguments() throws Exception {
		assertEquals( Double.valueOf(7), eval("f(x, y) = x - y; f(x @ 10, y @ 3)") );
		assertEquals( Double.valueOf(3), eval("f(x, y) = x - y; f(y @ 7, x @ 10)") );
	}
	
	public void testSecondOrderFunction() throws Exception {
		assertEquals( Double.valueOf(5), eval("(x -> (y -> x + y))(2)(3)") );
	}
	
	public void testRecursiveFunction() throws Exception {
		assertEquals( Double.valueOf(8), eval("fib(i) = if(i == 0, 0, i == 1, 1, fib(i - 1) + fib(i - 2)); fib(6)"));
	}
	
	protected void assertEqualsish( String message, Object o1, Object o2 ) {
		if( o1 instanceof Number && o2 instanceof Number ) {
			super.assertEquals( message, ((Number)o1).doubleValue(), ((Number)o2).doubleValue() );
		} else {
			super.assertEquals( message, o1, o2 );
		}
	}
	
	public void testExpressions( Object[] tests ) throws Exception {
		assertEquals( 0, tests.length & 0x1 );
		for( int i=0; i<tests.length; i += 2 ) {
			Object expectedResult = tests[i];
			String expression = (String)tests[i+1];
			Object actualResult = eval(expression);
			assertEqualsish( "eval('" + expression + "') = " + actualResult + "; expected "+expectedResult, expectedResult, actualResult );
		}
	}
	
	public void testEvalVariousLiterals() throws Exception {
		testExpressions( literalTests );
	}
	
	public void testEvalVariousLogicExpressions() throws Exception {
		testExpressions( logicTests );
	}
}
