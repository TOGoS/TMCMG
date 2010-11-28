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
			return new TNLParser(sr).readNode(0);
		} catch( IOException e ) {
			throw new RuntimeException(e);
		}
	}
	
	public void testParseAtom() {
		ASTNode expected = new ASTNode("foo", Collections.EMPTY_LIST);
		assertEquals( expected, parse("foo") );
	}
	
	public void testParseSingleArgument() {
		ArrayList expectedArguments = new ArrayList();
		expectedArguments.add( new ASTNode("bar") );
		ASTNode expected = new ASTNode("foo", expectedArguments);
		assertEquals( expected, parse("foo( bar )") );
	}
	
	public void testParseMultiArguments() {
		ArrayList expectedArguments = new ArrayList();
		expectedArguments.add( new ASTNode("bar") );
		expectedArguments.add( new ASTNode("baz") );
		ASTNode expected = new ASTNode("foo", expectedArguments);
		assertEquals( expected, parse("foo( bar, baz )") );
	}
	
	public void testParseOperator() {
		ArrayList expectedArguments = new ArrayList();
		expectedArguments.add( new ASTNode("foo") );
		expectedArguments.add( new ASTNode("bar") );
		ASTNode expected = new ASTNode("+", expectedArguments);
		assertEquals( expected, parse("foo + bar") );
	}
	
	public void testParseMultiPrecedence() {
		ArrayList ea1 = new ArrayList();
		ea1.add( new ASTNode("foo") );
		ea1.add( new ASTNode("bar") );
		ASTNode e1 = new ASTNode("+", ea1);

		ArrayList ea2 = new ArrayList();
		ea2.add( new ASTNode("baz") );
		ea2.add( new ASTNode("quux") );
		ASTNode e2 = new ASTNode("*", ea2);

		ArrayList ea3 = new ArrayList();
		ea3.add( e1 );
		ea3.add( e2 );
		ASTNode e3 = new ASTNode("/", ea3);

		assertEquals( e3, parse("(foo + bar) / baz * quux") );
	}

	public void testParseMultiPrecedence2() {
		ArrayList ea1 = new ArrayList();
		ea1.add( new ASTNode("foo") );
		ea1.add( new ASTNode("bar") );
		ASTNode e1 = new ASTNode("**", ea1);

		ArrayList ea2 = new ArrayList();
		ea2.add( new ASTNode("baz") );
		ea2.add( new ASTNode("quux") );
		ea2.add( new ASTNode("grawr") );
		ASTNode e2 = new ASTNode("*", ea2);

		ArrayList ea3 = new ArrayList();
		ea3.add( e1 );
		ea3.add( e2 );
		ea3.add( new ASTNode("thing") );
		ASTNode e3 = new ASTNode("/", ea3);

		assertEquals( e3, parse("foo ** bar / baz * quux * grawr / thing") );
	}
	
	public void testParseEquals() {
		ArrayList ea1 = new ArrayList();
		ea1.add( new ASTNode("cow") );
		ea1.add( new ASTNode("pig") );
		ASTNode e1 = new ASTNode("=", ea1);
		
		assertEquals( e1, parse("cow = pig") );
	}
	
	public void testParseEqualsAndSemiColinz() {
		ArrayList ea1 = new ArrayList();
		ea1.add( new ASTNode("cow") );
		ea1.add( new ASTNode("pig") );
		ASTNode e1 = new ASTNode("=", ea1);
		
		ArrayList ea2 = new ArrayList();
		ea2.add( new ASTNode("horse") );
		ea2.add( new ASTNode("rabbit") );
		ASTNode e2 = new ASTNode("=", ea2);
		
		ArrayList ea3 = new ArrayList();
		ea3.add(e1);
		ea3.add(e2);
		ASTNode e3 = new ASTNode(";",ea3);
		
		assertEquals( e3, parse("cow = pig; horse = rabbit") );
	}
	
	public void testDifferentSyntax() {
		assertEquals(
			parse("peace = war; freedom = slavery; strength = ignorance"),
			parse(";(=(peace,war), =(freedom,slavery), =(strength,ignorance))")
		);
	}
}
