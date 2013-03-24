package togos.noise.v3.parser;

import togos.noise.v1.lang.BaseSourceLocation;
import togos.noise.v3.parser.ast.ASTNode;
import togos.noise.v3.parser.ast.BlockNode;
import togos.noise.v3.parser.ast.OperatorApplication;
import togos.noise.v3.parser.ast.SymbolNode;
import junit.framework.TestCase;

public class ParserTest extends TestCase
{
	BaseSourceLocation TEST_LOC = new BaseSourceLocation("test script", 1, 1);
	
	protected void assertInstanceOf( Class<?> c, Object obj ) {
		assertNotNull( "Expected non-null value", obj );
		assertTrue( "Expected instance of "+c+", but got "+obj.getClass(), c.isAssignableFrom(obj.getClass()) );
	}
	
	public void testVoid() throws Exception {
		ASTNode n = Parser.parse("", TEST_LOC);
		assertInstanceOf( BlockNode.class, n );
		assertEquals( 0, ((BlockNode)n).statements.size() );
	}
	
	public void testSymbol() throws Exception {
		ASTNode n = Parser.parse("hello", TEST_LOC);
		assertInstanceOf( SymbolNode.class, n );
		assertEquals( "hello", ((SymbolNode)n).text );
	}
	
	public void testOperator() throws Exception {
		ASTNode n = Parser.parse("1 + 2", TEST_LOC);
		assertEquals( "(1 + 2)", n.toString() );
		
		assertInstanceOf( OperatorApplication.class, n );
		assertEquals("+", ((OperatorApplication)n).operator.text );
		assertInstanceOf( SymbolNode.class, ((OperatorApplication)n).n1 );
		assertEquals("1", ((SymbolNode)((OperatorApplication)n).n1).text );
		assertInstanceOf( SymbolNode.class, ((OperatorApplication)n).n2 );
		assertEquals("2", ((SymbolNode)((OperatorApplication)n).n2).text );
	}
	
	public void testSamePrecedenceOperators() throws Exception {
		ASTNode n = Parser.parse("1 + 2 - 3 + 4", TEST_LOC); // (((1 + 2) - 3) + 4)
		assertEquals( "(((1 + 2) - 3) + 4)", n.toString() );
		
		assertInstanceOf( OperatorApplication.class, n );
		assertEquals("+", ((OperatorApplication)n).operator.text );
		assertInstanceOf( SymbolNode.class, ((OperatorApplication)n).n2 );
		ASTNode o = ((OperatorApplication)n).n1;
		assertInstanceOf( OperatorApplication.class, o );
		// Blah blah blah
	}
	
	protected void assertParseAndEmit( String expected, String input ) throws Exception {
		assertEquals( expected, Parser.parse(input, TEST_LOC).toString() );
	}
	
	public void testMoreInfixOperators() throws Exception {
		assertParseAndEmit("((1 * 2) + (3 % 4))", "1 * 2 + 3 % 4");
		assertParseAndEmit("((1 + 2) * (3 - 4))", "(1 + 2) * (3 - 4)");
	}
}
