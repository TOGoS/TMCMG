package togos.noise2.lang;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import togos.noise2.lang.TNLTokenizer;

import junit.framework.TestCase;

public class TNLTokenizerTest extends TestCase
{
	public void testTokenize() {
		String tokenizeThis = "foo(bar) + queue **\n"
		                    + "  baz(quux ( radish,() ))";
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
		expectedTokens.add(new Token("baz","test-script",2,3));
		expectedTokens.add(new Token("(","test-script",2,6));
		expectedTokens.add(new Token("quux","test-script",2,7));
		expectedTokens.add(new Token("(","test-script",2,12));
		expectedTokens.add(new Token("radish","test-script",2,14));
		expectedTokens.add(new Token(",","test-script",2,20));
		expectedTokens.add(new Token("(","test-script",2,21));
		expectedTokens.add(new Token(")","test-script",2,22));
		expectedTokens.add(new Token(")","test-script",2,24));
		expectedTokens.add(new Token(")","test-script",2,25));
		assertEquals(expectedTokens, tokens);
	}
}
