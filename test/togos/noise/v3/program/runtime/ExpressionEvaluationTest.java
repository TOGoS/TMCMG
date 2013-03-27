package togos.noise.v3.program.runtime;

import togos.noise.v1.lang.BaseSourceLocation;
import togos.noise.v3.parser.Parser;
import togos.noise.v3.parser.ProgramTreeBuilder;
import togos.noise.v3.program.structure.Expression;
import junit.framework.TestCase;

public class ExpressionEvaluationTest extends TestCase
{
	BaseSourceLocation TEST_LOC = new BaseSourceLocation("test script", 1, 1);
	
	protected Expression<?> parse( String source ) throws Exception {
		ProgramTreeBuilder ptb = new ProgramTreeBuilder();
		return ptb.parseExpression(Parser.parse(source, TEST_LOC));
	}

	public void testEvaluateConstant() throws Exception {
		assertEquals( Long.valueOf(1), parse("1").evaluate(new Context()).getValue() );
	}
}
