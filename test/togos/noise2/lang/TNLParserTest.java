package togos.noise2.lang;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;

import togos.noise2.lang.ASTNode;
import togos.noise2.lang.TNLParser;

import junit.framework.TestCase;

public class TNLParserTest extends TestCase
{
	protected ASTNode parse(String str) {
		try {
			StringReader sr = new StringReader(str);
			return new TNLParser(new TNLTokenizer(sr,"parser-test",1,1)).readNode(0);
		} catch( IOException e ) {
			throw new RuntimeException(e);
		}
	}
	
	static SourceLocation NSL = new Token("","",0,0);
	
	public void testParseAtom() {
		ASTNode expected = new ASTNode("foo", Collections.EMPTY_LIST, NSL);
		assertEquals( expected, parse("foo") );
	}
	
	public void testParseSingleArgument() {
		ArrayList expectedArguments = new ArrayList();
		expectedArguments.add( new ASTNode("bar",NSL) );
		ASTNode expected = new ASTNode("foo", expectedArguments, NSL);
		assertEquals( expected, parse("foo( bar )") );
	}
	
	public void testParseMultiArguments() {
		ArrayList expectedArguments = new ArrayList();
		expectedArguments.add( new ASTNode("bar",NSL) );
		expectedArguments.add( new ASTNode("baz",NSL) );
		ASTNode expected = new ASTNode("foo", expectedArguments, NSL);
		assertEquals( expected, parse("foo( bar, baz )") );
	}
	
	public void testParseOperator() {
		ArrayList expectedArguments = new ArrayList();
		expectedArguments.add( new ASTNode("foo",NSL) );
		expectedArguments.add( new ASTNode("bar",NSL) );
		ASTNode expected = new ASTNode("+", expectedArguments,NSL);
		assertEquals( expected, parse("foo + bar") );
	}
	
	public void testParseMultiPrecedence() {
		ArrayList ea1 = new ArrayList();
		ea1.add( new ASTNode("foo",NSL) );
		ea1.add( new ASTNode("bar",NSL) );
		ASTNode e1 = new ASTNode("+", ea1,NSL);

		ArrayList ea2 = new ArrayList();
		ea2.add( new ASTNode("baz",NSL) );
		ea2.add( new ASTNode("quux",NSL) );
		ASTNode e2 = new ASTNode("*", ea2, NSL);

		ArrayList ea3 = new ArrayList();
		ea3.add( e1 );
		ea3.add( e2 );
		ASTNode e3 = new ASTNode("/", ea3, NSL);

		assertEquals( e3, parse("(foo + bar) / baz * quux") );
	}

	public void testParseMultiPrecedence2() {
		ArrayList ea1 = new ArrayList();
		ea1.add( new ASTNode("foo", NSL) );
		ea1.add( new ASTNode("bar", NSL) );
		ASTNode e1 = new ASTNode("**", ea1, NSL);

		ArrayList ea2 = new ArrayList();
		ea2.add( new ASTNode("baz", NSL) );
		ea2.add( new ASTNode("quux", NSL) );
		ea2.add( new ASTNode("grawr", NSL) );
		ASTNode e2 = new ASTNode("*", ea2, NSL);

		ArrayList ea3 = new ArrayList();
		ea3.add( e1 );
		ea3.add( e2 );
		ea3.add( new ASTNode("thing", NSL) );
		ASTNode e3 = new ASTNode("/", ea3, NSL);

		assertEquals( e3, parse("foo ** bar / baz * quux * grawr / thing") );
	}
	
	public void testParseEquals() {
		ArrayList ea1 = new ArrayList();
		ea1.add( new ASTNode("cow", NSL) );
		ea1.add( new ASTNode("pig", NSL) );
		ASTNode e1 = new ASTNode("=", ea1, NSL);
		
		assertEquals( e1, parse("cow = pig") );
	}
	
	public void testParseEqualsAndSemiColinz() {
		ArrayList ea1 = new ArrayList();
		ea1.add( new ASTNode("cow", NSL) );
		ea1.add( new ASTNode("pig", NSL) );
		ASTNode e1 = new ASTNode("=", ea1, NSL);
		
		ArrayList ea2 = new ArrayList();
		ea2.add( new ASTNode("horse", NSL) );
		ea2.add( new ASTNode("rabbit", NSL) );
		ASTNode e2 = new ASTNode("=", ea2, NSL);
		
		ArrayList ea3 = new ArrayList();
		ea3.add(e1);
		ea3.add(e2);
		ASTNode e3 = new ASTNode(";",ea3, NSL);
		
		assertEquals( e3, parse("cow = pig; horse = rabbit") );
	}
	
	public void testDifferentSyntax() {
		assertEquals(
			parse("peace = war; freedom = slavery; strength = ignorance"),
			parse(";(=(peace,war), =(freedom,slavery), =(strength,ignorance))")
		);
	}
}
