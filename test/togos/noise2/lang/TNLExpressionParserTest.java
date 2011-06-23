package togos.noise2.lang;

import java.io.IOException;
import java.io.StringReader;

import junit.framework.TestCase;

public class TNLExpressionParserTest extends TestCase
{
	static SourceLocation NSL = new Token("","test-script",1,1);
	
	public TNLExpression parse( String s ) throws IOException, ParseError {
		return new TNLExpressionParser( new TNLTokenizer(new StringReader(s), "test-script", 1, 1) ).readExpression(0,null);
	}
	
	public void testParseLiteralString() throws IOException, ParseError {
		assertEquals(
			new TNLLiteralExpression("Hello, world!\nMy name is Mike.", NSL, null ),
			parse("\"Hello, world!\\nMy name is Mike.\"")
		);
	}
}
