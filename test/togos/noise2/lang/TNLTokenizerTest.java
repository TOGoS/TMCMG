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
		String tokenizeThis = "foo(bar) + queue ** baz(quux ( radish,() ))";
		List tokens = new ArrayList();
		String token;
		StringReader sr = new StringReader(tokenizeThis);
		TNLTokenizer st = new TNLTokenizer(sr);
		try {
			while( (token = st.readToken()) != null ) {
				tokens.add(token);
			}
		} catch( IOException e ) {
			throw new RuntimeException(e);
		}
		
		List expectedTokens = new ArrayList();
		expectedTokens.add("foo");
		expectedTokens.add("(");
		expectedTokens.add("bar");
		expectedTokens.add(")");
		expectedTokens.add("+");
		expectedTokens.add("queue");
		expectedTokens.add("**");
		expectedTokens.add("baz");
		expectedTokens.add("(");
		expectedTokens.add("quux");
		expectedTokens.add("(");
		expectedTokens.add("radish");
		expectedTokens.add(",");
		expectedTokens.add("(");
		expectedTokens.add(")");
		expectedTokens.add(")");
		expectedTokens.add(")");
		assertEquals(expectedTokens, tokens);
	}
}
