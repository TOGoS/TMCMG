package togos.genfs;

import junit.framework.TestCase;

public class TokenizerTest extends TestCase
{
	protected void assertEquals( String[] expected, String[] aktual ) {
		assertEquals( expected.length, aktual.length );
		for( int i=0; i<expected.length; ++i ) {
			assertEquals(expected[i],aktual[i]);
		}
	}
	
	public void testTokenize() {
		assertEquals( new String[]{"1","2","3"}, Tokenizer.tokenize("1 2 3"));
		assertEquals( new String[]{"hello","good bye","shucks"}, Tokenizer.tokenize("hello \"good bye\" shucks"));
		assertEquals( new String[]{"1","escape \" \\ \r\n\t","3","oh & and"}, Tokenizer.tokenize("1 \"escape \\\" \\\\ \\r\\n\\t\" 3 \"oh & and\""));
	}
	
	public void testDeokenize() {
		assertEquals( "1 2 3",
			Tokenizer.detokenize(new String[]{"1","2","3"}));
		assertEquals( "hello \"good bye\" shucks",
			Tokenizer.detokenize(new String[]{"hello","good bye","shucks"}));
		assertEquals( "1 \"escape \\\" \\\\ \\r\\n\\t\" 3 \"oh & and\"",
			Tokenizer.detokenize(new String[]{"1","escape \" \\ \r\n\t","3","oh & and"}));
	}

}
