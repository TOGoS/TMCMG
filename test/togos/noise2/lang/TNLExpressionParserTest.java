package togos.noise2.lang;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;

import junit.framework.TestCase;

public class TNLExpressionParserTest extends TestCase
{
	static SourceLocation NSL = new Token(null,"test-script",1,1);
	
	public TNLExpression parse( String s ) throws IOException, ParseError {
		return new TNLExpressionParser( new TNLTokenizer(new StringReader(s), "test-script", 1, 1) ).readExpression(0,null);
	}
	
	protected void assertEquals( TNLExpression e1, TNLExpression e2 ) {
		assertEquals( e1.toString(true) + " =/= " + e2.toString(true), (Object)e1, (Object)e2 );
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

	public void testParseSimpleOperator() throws IOException, ParseError {
		TNLSymbolExpression plus = new TNLSymbolExpression("+", new Token(null,"test-script",1,3), null);
		ArrayList operands = new ArrayList(); 
		TNLApplyExpression add = new TNLApplyExpression(plus, operands, Collections.EMPTY_LIST, plus, null );
		operands.add( new TNLSymbolExpression("x", new Token(null,"test-script",1,1), add) );
		operands.add( new TNLSymbolExpression("x", new Token(null,"test-script",1,5), add) );
		operands.add( new TNLSymbolExpression("x", new Token(null,"test-script",1,9), add) );
		
		assertEquals( add, parse("x + x + x") );
	}

	public void testParseNestedOperator() throws IOException, ParseError {
		TNLSymbolExpression plus = new TNLSymbolExpression("+", new Token(null,"test-script",1,7), null);
		TNLSymbolExpression times1 = new TNLSymbolExpression("*", new Token(null,"test-script",1,3), null);
		TNLSymbolExpression times2 = new TNLSymbolExpression("*", new Token(null,"test-script",1,11), null);
		TNLSymbolExpression expo = new TNLSymbolExpression("**", new Token(null,"test-script",1,15), null);
		TNLSymbolExpression over = new TNLSymbolExpression("/", new Token(null,"test-script",1,24), null);
		
		ArrayList terms = new ArrayList(); 
		TNLApplyExpression add = new TNLApplyExpression(plus, terms, Collections.EMPTY_LIST, plus, null );

		ArrayList factors1 = new ArrayList(); 
		TNLApplyExpression mult1 = new TNLApplyExpression(times1, factors1, Collections.EMPTY_LIST, times1, add );
		factors1.add( new TNLSymbolExpression("x", new Token(null,"test-script",1,1), mult1) );
		factors1.add( new TNLSymbolExpression("y", new Token(null,"test-script",1,5), mult1) );
		
		ArrayList factors2 = new ArrayList(); 
		TNLApplyExpression mult2 = new TNLApplyExpression(times2, factors2, Collections.EMPTY_LIST, times2, add );

		ArrayList powArgs = new ArrayList(); 
		TNLApplyExpression pow = new TNLApplyExpression(expo, powArgs, Collections.EMPTY_LIST, expo, mult2 );
		powArgs.add( new TNLSymbolExpression("y", new Token(null,"test-script",1,13), pow) );
		powArgs.add( new TNLSymbolExpression("z", new Token(null,"test-script",1,18), pow) );
		
		factors2.add( new TNLSymbolExpression("x", new Token(null,"test-script",1,9), pow) );
		factors2.add( pow );
		
		ArrayList divArgs = new ArrayList();
		TNLApplyExpression div = new TNLApplyExpression(over, divArgs, Collections.EMPTY_LIST, over, add );
		divArgs.add( new TNLSymbolExpression("x", new Token(null,"test-script",1,22), div) );
		divArgs.add( new TNLSymbolExpression("y", new Token(null,"test-script",1,26), div) );
		
		terms.add( mult1 );
		terms.add( mult2 );
		terms.add( div );
		
		assertEquals( add, parse("x * y + x * y ** z + x / y") );
	}
	
	public void testParseSimpleApply() throws IOException, ParseError {
		TNLSymbolExpression func = new TNLSymbolExpression("some-function", new Token(null,"test-script",1,1), null);
		TNLApplyExpression apply = new TNLApplyExpression(func, Collections.EMPTY_LIST, Collections.EMPTY_LIST, new Token(null,"test-script",1,1), null );
		func.parent = apply;
		
		assertEquals( apply, parse("some-function()") );
	}
}
