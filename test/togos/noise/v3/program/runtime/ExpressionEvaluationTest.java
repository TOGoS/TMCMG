package togos.noise.v3.program.runtime;

import junit.framework.TestCase;
import togos.noise.v3.functions.ListFunctions;
import togos.noise.v3.functions.MathFunctions;
import togos.noise.v3.parser.Parser;
import togos.noise.v3.parser.ProgramTreeBuilder;
import togos.noise.v3.parser.TokenizerSettings;
import togos.noise.v3.program.structure.Expression;

public class ExpressionEvaluationTest extends TestCase
{
	static TokenizerSettings TEST_LOC = TokenizerSettings.forBuiltinFunctions(ExpressionEvaluationTest.class);
	static ProgramTreeBuilder ptb = new ProgramTreeBuilder();
	static Context CONTEXT = new Context();
	static {
		CONTEXT.putAll(MathFunctions.CONTEXT);
		CONTEXT.putAll(ListFunctions.CONTEXT);
	}
	
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
	
	static final Object[] conditionalTests = new Object[] {
		true,  "if( true, true, false )",
		false, "if( true, false, true )",
		false, "if( false, true, false )",
		true,  "if( false, false, true )",
		false, "if( false, false, false )",
		false, "if( true, false, false )",
	};
	
	public ExpressionEvaluationTest() {
		super();
	}
	
	protected Expression<?> parse( String source ) throws Exception {
		return ptb.parseExpression(Parser.parse(source, TEST_LOC));
	}
	
	protected Object eval( String source ) throws Exception {
		return parse(source).bind(CONTEXT).getValue();
	}
	
	protected void assertEvalInt( int v, String s ) throws Exception {
		assertEquals( Integer.valueOf(v), eval(s) );
	}
	
	protected void assertEvalLong( long v, String s ) throws Exception {
		assertEquals( Long.valueOf(v), eval(s) );
	}
	
	public void testEvaluateIntegerConstants() throws Exception {
		assertEvalInt( 1, "1" );
		
		assertEvalInt(  15, " 0b1111" );
		assertEvalInt(  15, "+0b1111" );
		assertEvalInt( -15, "-0b1111" );
		
		assertEvalInt(  16, " 0x10" );
		assertEvalInt(  16, "+0x10" );
		assertEvalInt( -16, "-0x10" );
		
		// Test outer bounds
		
		assertEvalInt( 0x7FFFFFFF, " 2147483647" );
		assertEvalInt( 0x7FFFFFFF, "+2147483647" );
		assertEvalInt(-0x80000000, "-2147483648" );
		
		assertEvalInt( 0x7FFFFFFF, " 0x7FFFFFFF" );
		assertEvalInt( 0x7FFFFFFF, "+0x7FFFFFFF" );
		assertEvalInt(-0x80000000, "-0x80000000" );
		
		assertEvalInt( 0x7FFFFFFF, " 0b01111111111111111111111111111111" );
		assertEvalInt( 0x7FFFFFFF, "+0b01111111111111111111111111111111" );
		assertEvalInt(-0x80000000, "-0b10000000000000000000000000000000" );
	}
	
	public void testEvaluateLongConstant() throws Exception {
		// Test inner bounds
		
		assertEvalLong( 0x80000000l, " 2147483648" );
		assertEvalLong( 0x80000000l, "+2147483648" );
		assertEvalLong(-0x80000001l, "-2147483649" );
		
		assertEvalLong( 0x80000000l, " 0x80000000" );
		assertEvalLong( 0x80000000l, "+0x80000000" );
		assertEvalLong(-0x80000001l, "-0x80000001" );
		
		assertEvalLong( 0x80000000l, " 0b10000000000000000000000000000000" );
		assertEvalLong( 0x80000000l, "+0b10000000000000000000000000000000" );
		assertEvalLong(-0x80000001l, "-0b10000000000000000000000000000001" );
		
		// Test outer bounds
		
		assertEvalLong( 0x7FFFFFFFFFFFFFFFl, " 9223372036854775807" );
		assertEvalLong( 0x7FFFFFFFFFFFFFFFl, "+9223372036854775807" );
		assertEvalLong(-0x8000000000000000l, "-9223372036854775808" );
		
		assertEvalLong( 0x7FFFFFFFFFFFFFFFl, " 0x7FFFFFFFFFFFFFFF" );
		assertEvalLong( 0x7FFFFFFFFFFFFFFFl, "+0x7FFFFFFFFFFFFFFF" );
		assertEvalLong(-0x8000000000000000l, "-0x8000000000000000" );
		
		assertEvalLong( 0x7FFFFFFFFFFFFFFFl, " 0b0111111111111111111111111111111111111111111111111111111111111111" );
		assertEvalLong( 0x7FFFFFFFFFFFFFFFl, "+0b0111111111111111111111111111111111111111111111111111111111111111" );
		assertEvalLong(-0x8000000000000000l, "-0b1000000000000000000000000000000000000000000000000000000000000000" );
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
	
	public void testConditionalExpression() throws Exception {
		testExpressions( conditionalTests );
	}
	
	public void testListLiteral() throws Exception {
		Object obj = eval("list(1, 2, 3)");
		assertTrue( obj instanceof LinkedListNode );
		LinkedListNode<?> list = LinkedListNode.class.cast(obj); 
		assertEquals( 3, list.length );
		assertEquals( Integer.valueOf(1), list.head );
		assertEquals( Integer.valueOf(2), list.tail.head );
		assertEquals( Integer.valueOf(3), list.tail.tail.head );
	}
}
