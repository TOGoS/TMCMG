package togos.noise.v3.parser;

import togos.noise.v1.lang.BaseSourceLocation;
import togos.noise.v3.program.structure.FunctionApplication;
import togos.noise.v3.program.structure.Expression;

public class ProgramTreeBuilderTest extends CoolTestCase
{
	BaseSourceLocation TEST_LOC = new BaseSourceLocation("test script", 1, 1);
	
	protected void assertStringification( String expected, String input ) throws Exception {
		ProgramTreeBuilder ptb = new ProgramTreeBuilder();
		Expression<Object> expr = ptb.parseExpression(Parser.parse(input, TEST_LOC));
		assertEquals( expected, expr.toString() );
	}
	
	public void testParseInfix() throws Exception {
		ProgramTreeBuilder ptb = new ProgramTreeBuilder();
		Expression<Object> b = ptb.parseExpression(Parser.parse("2 + 3", BaseSourceLocation.NONE));
		assertInstanceOf( FunctionApplication.class, b );
		assertEquals("+(2, 3)", b.toString());
	}
	
	public void testParseFunctionApplication() throws Exception {
		assertStringification("foo(bar, baz)", "foo(bar, baz)");
	}
}
