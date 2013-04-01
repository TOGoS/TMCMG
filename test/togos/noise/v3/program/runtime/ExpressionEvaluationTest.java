package togos.noise.v3.program.runtime;

import junit.framework.TestCase;
import togos.noise.v1.lang.BaseSourceLocation;
import togos.noise.v3.functions.MathFunctions;
import togos.noise.v3.parser.Parser;
import togos.noise.v3.parser.ProgramTreeBuilder;
import togos.noise.v3.program.structure.Expression;

public class ExpressionEvaluationTest extends TestCase
{
	static BaseSourceLocation TEST_LOC = new BaseSourceLocation("test script", 1, 1);
	static ProgramTreeBuilder ptb = new ProgramTreeBuilder();
		
	public ExpressionEvaluationTest() {
		super();
	}
	
	protected Expression<?> parse( String source ) throws Exception {
		return ptb.parseExpression(Parser.parse(source, TEST_LOC));
	}
	
	protected Object eval( String source ) throws Exception {
		return parse(source).bind(MathFunctions.CONTEXT).getValue();
	}

	public void testEvaluateConstant() throws Exception {
		assertEquals( Long.valueOf(1), eval("1") );
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
}
