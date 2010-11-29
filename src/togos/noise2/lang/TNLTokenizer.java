package togos.noise2.lang;

import java.io.IOException;
import java.io.Reader;

public class TNLTokenizer
{
	Reader r;
	int lastChar = -2;
	String filename;
	int lineNumber;
	int columnNumber;
	
	public TNLTokenizer( Reader r, String filename, int lineNumber, int columnNumber ) {
		this.r = r;
		this.filename = filename;
		this.columnNumber = columnNumber;
		this.lineNumber = lineNumber;
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
	
	public Token readToken() throws IOException {
		int c = readChar();
		while( true ) {
			if( c == -1 ) {
				return null;
			} else if( c == '#' ) {
				while( c != '\n' && c != -1 ) c = readChar();
			} else if( c == '\n' ) {
				++lineNumber;
				columnNumber = 1;
				c = readChar();
			} else if( c == '\t' ) {
				++columnNumber;
				while( (columnNumber-1) % 8 == 0 ) {
					++columnNumber;
				}
				c = readChar();
			} else if( isWhitespace((char)c) ) {
				++columnNumber;
				c = readChar();
			} else if( isDelimiter((char)c) ) {
				return new Token(String.valueOf((char)c),filename,lineNumber,columnNumber++);
			} else {
				String word = "";
				while( isWordChar(c) ) {
					word += (char)c;
					c = readChar();
				}
				unreadChar(c);
				Token t = new Token(word,filename,lineNumber,columnNumber);
				columnNumber += word.length();
				return t;
			}
		}
	}
}
