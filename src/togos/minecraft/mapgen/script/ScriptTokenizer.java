package togos.minecraft.mapgen.script;

import java.io.IOException;
import java.io.Reader;

public class ScriptTokenizer
{
	Reader r;
	int lastChar = -2;
	
	public ScriptTokenizer( Reader r ) {
		this.r = r;
	}
	
	protected int readChar() throws IOException {
		if( lastChar == -2 ) {
			return r.read();
		}
		int c = lastChar;
		lastChar = -2;
		return c;
	}
	
	protected void unreadChar( int c ) {
		lastChar = c;
	}
	
	// Characters that force a new token, even if they are not surrounded by whitespace
	protected boolean isDelimiter( int c ) {
		switch( c ) {
		case('('): case(')'): case('['): case(']'): case(','): case(';'):
			return true;
		default:
			return false;
		}
	}
	
	protected boolean isWhitespace( int c ) {
		switch( c ) {
		case(' '): case('\t'): case('\n'): case('\r'):
			return true;
		default:
			return false;
		}
	}
	
	protected boolean isWordChar( int c ) {
		return !isWhitespace(c) && !isDelimiter(c) && c != -1;
	}
	
	public String readToken() throws IOException {
		int c = readChar();
		while( true ) {
			if( c == -1 ) {
				return null;
			} else if( isWhitespace((char)c) ) {
				c = readChar();
			} else if( isDelimiter((char)c) ) {
				return String.valueOf((char)c);
			} else {
				String word = "";
				while( isWordChar(c) ) {
					word += (char)c;
					c = readChar();
				}
				unreadChar(c);
				return word;
			}
		}
	}
}
