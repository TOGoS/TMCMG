package togos.noise.v3.parser;

import junit.framework.TestCase;
import togos.lang.ParseError;
import togos.noise.v3.asyncstream.Collector;

public class TokenizerTest extends TestCase
{
	public void testTokenizer() throws Exception {
		Tokenizer t = new Tokenizer();
		Collector<Token> c = new Collector<Token>();
		t.pipe(c);
		t.data("foo(bar, baz, quux);".toCharArray());
		
		String[] expectedTokenText = new String[] {
			"foo", "(", "bar", ",", "baz", ",", "quux", ")", ";"
		};
		
		assertEquals( expectedTokenText.length, c.collection.size() );
		
		int i=0;
		for( Token token : c.collection ) {
			assertEquals( expectedTokenText[i], token.text );
			++i;
		}
	}
	
	protected void assertTokenization( String expected, String input ) throws Exception {
		Collector<Token> c = new Collector<Token>();
		Tokenizer t = new Tokenizer();
		t.pipe(c);
		t.data( input.toCharArray() );
		t.end();
		String[] eparts = expected.split("\\|");
		assertEquals( eparts.length, c.collection.size() );
	}
	
	protected void assertParseError( String input ) throws Exception {
		try {
			Tokenizer t = new Tokenizer();
			t.data( input.toCharArray() );
			t.end();
			fail("Parsing <"+input+"> should have caused a ParseError, but did not!");
		} catch( ParseError e ) {
		}
	}
	
	public void testTokenizeStuff() throws Exception {
		assertTokenization("foo", "foo");
	}
	
	public void testTokenizeBarewoirds() throws Exception {
		assertTokenization("foo|bar|baz|quux", "foo bar  baz  quux");
	}
	
	public void testTokenizeStrings() throws Exception {
		assertTokenization("foo bar baz|quux xyzzy|radsauce", "'foo bar baz' \"quux xyzzy\" radsauce");
	}
	
	public void testTokenizeThang() throws Exception {
		assertTokenization("foo'bar\r\n|{|}|quux\"xyzzy\t", "'foo\\'bar\\r\\n'{}\"quux\\\"xyzzy\\t\"");
	}
	
	public void testNoSqushedWords() throws Exception {
		assertParseError("foo'foo'");
		assertParseError("foo\"foo'");
		assertParseError("'foo'foo");
	}
}
