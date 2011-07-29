package togos.noise2.lang;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

public class TNLTokenizerTest extends TestCase
{
	public void testTokenize() {
		String tokenizeThis = "foo(bar) + queue **\n"
			                + "  # this is a comment\n"
		                    + "  baz(quux ( radish,() )) # this is another comment\n"
			                + "# and another";
		List tokens = new ArrayList();
		Token token;
		StringReader sr = new StringReader(tokenizeThis);
		TNLTokenizer st = new TNLTokenizer(sr,"test-script",1,1);
		try {
			while( (token = st.readToken()) != null ) {
				tokens.add(token);
			}
		} catch( IOException e ) {
			throw new RuntimeException(e);
		}
		
		List expectedTokens = new ArrayList();
		expectedTokens.add(new Token("foo","test-script",1,1));
		expectedTokens.add(new Token("(","test-script",1,4));
		expectedTokens.add(new Token("bar","test-script",1,5));
		expectedTokens.add(new Token(")","test-script",1,8));
		expectedTokens.add(new Token("+","test-script",1,10));
		expectedTokens.add(new Token("queue","test-script",1,12));
		expectedTokens.add(new Token("**","test-script",1,18));
		expectedTokens.add(new Token("baz","test-script",3,3));
		expectedTokens.add(new Token("(","test-script",3,6));
		expectedTokens.add(new Token("quux","test-script",3,7));
		expectedTokens.add(new Token("(","test-script",3,12));
		expectedTokens.add(new Token("radish","test-script",3,14));
		expectedTokens.add(new Token(",","test-script",3,20));
		expectedTokens.add(new Token("(","test-script",3,21));
		expectedTokens.add(new Token(")","test-script",3,22));
		expectedTokens.add(new Token(")","test-script",3,24));
		expectedTokens.add(new Token(")","test-script",3,25));
		assertEquals(expectedTokens, tokens);
	}
	
	public void testTokenizeQuoted() throws IOException {
		String tokenizeThis = "`hello\\nworld` \"string with \\\nignored newline\" ;";
		StringReader sr = new StringReader(tokenizeThis);
		TNLTokenizer st = new TNLTokenizer(sr,"test-script",1,1);
		Token t;
		
		t = st.readToken();
		assertNotNull(t);
		assertEquals( '`', t.quote );
		assertEquals( "hello\nworld", t.value );
		
		t = st.readToken();
		assertNotNull(t);
		assertEquals( '"', t.quote );
		assertEquals( "string with ignored newline", t.value );
		
		t = st.readToken();
		assertNotNull(t);
		assertEquals( 0, t.quote );
		assertEquals( ";", t.value );
	}
}
