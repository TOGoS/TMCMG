package togos.noise3;

import togos.noise3.asyncstream.Collector;
import junit.framework.TestCase;

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
}
