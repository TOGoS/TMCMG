package togos.noise.v3.parser;

import togos.noise.v1.lang.BaseSourceLocation;
import togos.noise.v3.program.structure.FunctionApplication;
import togos.noise.v3.program.structure.Expression;

public class ProgramTreeBuilderTest extends CoolTestCase
{
	public void testParseProgram() throws Exception {
		ProgramTreeBuilder ptb = new ProgramTreeBuilder();
		Expression<Object> b = ptb.parseExpression(Parser.parse("2 + 3", BaseSourceLocation.NONE));
		assertInstanceOf( FunctionApplication.class, b );
	}
}
