package togos.noise2.lang;

import java.io.IOException;
import java.io.Reader;

import togos.lang.SourceLocation;

public class TNLTokenizer
{
	Reader r;
	int lastChar = -2;
	// of most recently read character, these usually mean:
	String filename;
	int lineNumber;
	int columnNumber;
	int lastLineNumber;
	int lastColumnNumber;
	
	public TNLTokenizer( Reader r, String filename, int lineNumber, int columnNumber ) {
		this.r = r;
		this.filename = filename;
		this.columnNumber = columnNumber;
		this.lineNumber = lineNumber;
	}
	
	public TNLTokenizer( Reader r, SourceLocation sloc ) {
		this( r, sloc.getSourceFilename(), sloc.getSourceLineNumber(), sloc.getSourceColumnNumber() );
	}
	
	protected int readChar() throws IOException {
		int c;
		if( lastChar == -2 ) {
			c = r.read();
		} else {
			c = lastChar;
			lastChar = -2;
		}
		
		lastLineNumber = lineNumber;
		lastColumnNumber = columnNumber;
		if( c == '\t' ) {
			while( (columnNumber-1) % 8 == 0 ) {
				++columnNumber;
			}
		} else if( c == '\n' ) {
			lineNumber += 1;
			columnNumber = 1;
		} else {
			++columnNumber;
		}
		
		return c;
	}
	
	protected void unreadChar( int c ) {
		lastChar = c;
		lineNumber = lastLineNumber;
		columnNumber = lastColumnNumber;
	}
	
	// Characters that force a new token, even if they are not surrounded by whitespace
	protected boolean isDelimiter( int c ) {
		switch( c ) {
		case('('): case(')'): case('['): case(']'): case(','): case(';'): case('@'):
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
	
	public SourceLocation getCurrentLocation() {
		return new Token(null,filename,lastLineNumber,lastColumnNumber);
	}
	
	public Token readToken() throws IOException {
		int c = readChar();
		while( true ) {
			if( c == -1 ) {
				return null;
			} else if( c == '"' || c == '`') {
				char quoteChar = (char)c;
				StringBuilder sb = new StringBuilder();
				Token t = new Token(null,filename,lastLineNumber,lastColumnNumber);
				c = readChar();
				eachChar: while( c != -1 && c != quoteChar ) {
					if( c == '\\' ) {
						c = readChar();
						switch( c ) {
						case('n'): sb.append('\n'); break;
						case('r'): sb.append('\r'); break;
						case('t'): sb.append('\t'); break;
						case('\n'):
							// Don't add newline to word
							break;
						case('\r'):
							c = readChar();
							if( c == '\n' ) break;
							continue eachChar;
						default:
							sb.append( (char)c );
						}
					} else {
						sb.append( (char)c );
					}
					c = readChar();
				}
				t.quote = quoteChar;
				t.value = sb.toString();
				return t;
			} else if( c == '#' ) {
				while( c != '\n' && c != -1 ) c = readChar();
			} else if( c == '\n' ) {
				c = readChar();
			} else if( c == '\t' ) {
				c = readChar();
			} else if( isWhitespace((char)c) ) {
				c = readChar();
			} else if( isDelimiter((char)c) ) {
				return new Token(String.valueOf((char)c),filename,lastLineNumber,lastColumnNumber);
			} else {
				Token t = new Token(null,filename,lastLineNumber,lastColumnNumber);
				String word = "";
				while( isWordChar(c) ) {
					word += (char)c;
					c = readChar();
				}
				unreadChar(c);
				t.value = word;
				return t;
			}
		}
	}
}
