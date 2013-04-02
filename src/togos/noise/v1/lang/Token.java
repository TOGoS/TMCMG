package togos.noise.v1.lang;

import togos.lang.BaseSourceLocation;

/**
 * A single 'word' in a TNL program.
 * '+', 'simplex', '(', ';', ',' each get a token.
 */
public class Token extends BaseSourceLocation
{
	public String value;
	public char quote = 0;
	
	public Token( String token, String filename, int lineNumber, int columnNumber ) {
		super( filename, lineNumber, columnNumber );
		this.value = token;
	}
	
	public String toSource() {
		if( quote == 0 ) {
			return value;
		} else {
			// @todo: should probably escape \r, \n, etc...
			return quote + value.replace("\\", "\\\\").replace(""+quote, "\\"+quote) + quote;
		}
	}
	
	public String toString() {
		return "token " + toSource() + " " + super.toString();
	}
	
	public boolean equals( Object oth ) {
		if( !(oth instanceof Token) ) return false;
		Token ott = (Token)oth;
		if( !value.equals(ott.value) ) return false;
		return super.equals( ott );
	}
}
