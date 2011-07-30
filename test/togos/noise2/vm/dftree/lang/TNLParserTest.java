package togos.noise2.vm.dftree.lang;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;

import junit.framework.TestCase;
import togos.lang.SourceLocation;
import togos.noise2.lang.ParseError;
import togos.noise2.lang.TNLTokenizer;
import togos.noise2.lang.Token;
import togos.noise2.vm.dftree.lang.ASTNode;
import togos.noise2.vm.dftree.lang.TNLParser;

public class TNLParserTest extends TestCase
{
	protected ASTNode parse(String str) throws ParseError {
		try {
			StringReader sr = new StringReader(str);
			return new TNLParser(new TNLTokenizer(sr,"parser-test",1,1)).readNode(0);
		} catch( IOException e ) {
			throw new RuntimeException(e);
		}
	}
	
	static SourceLocation NSL = new Token("","",0,0);
	
	public void testParseAtom() throws ParseError {
		ASTNode expected = new ASTNode("foo", Collections.EMPTY_LIST, NSL);
		assertEquals( expected, parse("foo") );
	}

	public void testParseQuotedString() throws ParseError {
		ASTNode expected = new ASTNode("\"foo", Collections.EMPTY_LIST, NSL);
		assertEquals( expected, parse("\"foo\"") );
		expected = new ASTNode("\"foo bar\\\n\\", Collections.EMPTY_LIST, NSL);
		assertEquals( expected, parse("\"foo bar\\\\\n\\\\\"") ); // newline not ignored
		expected = new ASTNode("\"foo bar\\", Collections.EMPTY_LIST, NSL);
		assertEquals( expected, parse("\"foo bar\\\n\\\\\"") ); // newline ignored
	}
	
	public void testParseQuotedIdentifier() throws ParseError {
		ASTNode expected = new ASTNode("foo\nbar", Collections.EMPTY_LIST, NSL);
		assertEquals( expected, parse("`foo\nbar`") );
		ArrayList addArgs = new ArrayList();
		addArgs.add( new ASTNode("a c",Collections.EMPTY_LIST, NSL) );
		addArgs.add( new ASTNode("b d",Collections.EMPTY_LIST, NSL) );
		expected = new ASTNode("+", addArgs, NSL);
		assertEquals( expected, parse("`a c` `+` `b d`") );
		assertEquals( expected, parse("`+`(`a c`,`b d`)") );
	}
	
	public void testParseSingleArgument() throws ParseError {
		ArrayList expectedArguments = new ArrayList();
		expectedArguments.add( new ASTNode("bar",NSL) );
		ASTNode expected = new ASTNode("foo", expectedArguments, NSL);
		assertEquals( expected, parse("foo( bar )") );
	}
	
	public void testParseMultiArguments() throws ParseError {
		ArrayList expectedArguments = new ArrayList();
		expectedArguments.add( new ASTNode("bar",NSL) );
		expectedArguments.add( new ASTNode("baz",NSL) );
		ASTNode expected = new ASTNode("foo", expectedArguments, NSL);
		assertEquals( expected, parse("foo( bar, baz )") );
	}
	
	public void testParseOperator() throws ParseError {
		ArrayList expectedArguments = new ArrayList();
		expectedArguments.add( new ASTNode("foo",NSL) );
		expectedArguments.add( new ASTNode("bar",NSL) );
		ASTNode expected = new ASTNode("+", expectedArguments,NSL);
		assertEquals( expected, parse("foo + bar") );
	}
	
	public void testParseMultiPrecedence() throws ParseError {
		ArrayList ea1 = new ArrayList();
		ea1.add( new ASTNode("foo",NSL) );
		ea1.add( new ASTNode("bar",NSL) );
		ASTNode e1 = new ASTNode("+", ea1,NSL);

		ArrayList ea2 = new ArrayList();
		ea2.add( e1 );
		ea2.add( new ASTNode("baz",NSL) );
		ASTNode e2 = new ASTNode("/", ea2, NSL);

		ArrayList ea3 = new ArrayList();
		ea3.add( e2 );
		ea3.add( new ASTNode("quux",NSL) );
		ASTNode e3 = new ASTNode("*", ea3, NSL);

		assertEquals( e3, parse("(foo + bar) / baz * quux") );
	}

	public void testParseMultiPrecedence2() throws ParseError {
		ArrayList ea1 = new ArrayList();
		ea1.add( new ASTNode("foo", NSL) );
		ea1.add( new ASTNode("bar", NSL) );
		ASTNode e1 = new ASTNode("**", ea1, NSL);

		ArrayList ea2 = new ArrayList();
		ea2.add( e1 );
		ea2.add( new ASTNode("baz", NSL) );
		ASTNode e2 = new ASTNode("/", ea2, NSL);
		
		ArrayList ea3 = new ArrayList();
		ea3.add( e2 );
		ea3.add( new ASTNode("quux", NSL) );
		ea3.add( new ASTNode("grawr", NSL) );
		ASTNode e3 = new ASTNode("*", ea3, NSL);
		
		ArrayList ea4 = new ArrayList();
		ea4.add( e3 );
		ea4.add( new ASTNode("thing", NSL) );
		ASTNode e4 = new ASTNode("/", ea4, NSL);

		assertEquals( e4, parse("foo ** bar / baz * quux * grawr / thing") );
		// /( *( /( **(foo,bar), baz), quux, grawr), thing );
	}
	
	public void testParseEquals() throws ParseError {
		ArrayList ea1 = new ArrayList();
		ea1.add( new ASTNode("cow", NSL) );
		ea1.add( new ASTNode("pig", NSL) );
		ASTNode e1 = new ASTNode("=", ea1, NSL);
		
		assertEquals( e1, parse("cow = pig") );
	}
	
	public void testParseEqualsAndSemiColinz() throws ParseError {
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
	
	public void testDifferentSyntax() throws ParseError {
		assertEquals(
			parse("peace = war; freedom = slavery; strength = ignorance"),
			parse(";(=(peace,war), =(freedom,slavery), =(strength,ignorance))")
		);
	}
	
	public void testTrailingSemicolonsOkay() throws ParseError {
		assertEquals(
			parse("a = b; c = d;"),
			parse("a = b; c = d")
		);
	}
	
	public void testTrailingSemicolonsInParenthesesOkay() throws ParseError {
		assertEquals(
			parse("(a = b; c = d;)"),
			parse("(a = b; c = d)")
		);
	}

	public void testTrailingOperatorsOkay() throws ParseError {
		assertEquals(
			parse("((1 + 2 +) / (3 - 4 -) /); x; "),
			parse("((1 + 2) / (3 - 4)); x")
		);
		assertEquals(
			parse("a(b, c, d,); (e, f, g,)"),
			parse("a(b, c, d); (e, f, g)")
		);
	}
}
