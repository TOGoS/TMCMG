package togos.noise2.lang;

import java.io.IOException;
import java.io.StringReader;

import junit.framework.TestCase;

public class TNLExpressionParserTest extends TestCase
{
	static SourceLocation NSL = new Token(null,"test-script",1,1);
	
	public TNLExpression parse( String s ) throws IOException, ParseError {
		return new TNLExpressionParser( new TNLTokenizer(new StringReader(s), "test-script", 1, 1) ).readExpression(0,null);
	}
	
	public void testParseLiteralString() throws IOException, ParseError {
		assertEquals(
			new TNLLiteralExpression("Hello, world!\nMy name is Mike.", NSL, null ),
			parse("\"Hello, world!\\nMy name is Mike.\"")
		);
	}
	
	public void testParseSymbol() throws IOException, ParseError {
		assertEquals(
			new TNLSymbolExpression("yaddah waddah", NSL, null ),
			parse("`yaddah waddah`")
		);
	}
	
	public void testParseBlock() throws IOException, ParseError {
		TNLBlockExpression block = new TNLBlockExpression(new Token(null,"test-script",1,1), null);
		block.definitions.put("foo", new TNLSymbolExpression("1", new Token(null,"test-script",1,8), block) );
		block.definitions.put("bar", new TNLSymbolExpression("2", new Token(null,"test-script",1,17), block) );
		block.value = new TNLSymbolExpression("3", new Token(null,"test-script",1,20), block);
		
		assertEquals( block, parse("(foo = 1; bar = 2; 3)") );
	}
}
