package togos.noise.v3.parser;

import java.io.InputStreamReader;

import togos.lang.BaseSourceLocation;
import togos.lang.ParseError;
import togos.lang.SourceLocation;
import togos.noise.v3.asyncstream.BaseStreamSource;
import togos.noise.v3.asyncstream.StreamDestination;
import togos.noise.v3.asyncstream.StreamUtil;

public class Tokenizer extends BaseStreamSource<Token> implements StreamDestination<char[]>
{
	enum State {
		NO_TOKEN,
		WORD_BOUNDARY, // Just read a bareword or quoted string
		SYMBOL( NO_TOKEN, Token.Type.SYMBOL ),
		BAREWORD( NO_TOKEN, Token.Type.BAREWORD ),
		SINGLE_QUOTED_STRING( NO_TOKEN, Token.Type.SINGLE_QUOTED_STRING ),
		SINGLE_QUOTED_STRING_ESCAPE( SINGLE_QUOTED_STRING ),
		DOUBLE_QUOTED_STRING( NO_TOKEN, Token.Type.DOUBLE_QUOTED_STRING ),
		DOUBLE_QUOTED_STRING_ESCAPE( DOUBLE_QUOTED_STRING ),
		LINE_COMMENT;
		
		public final State parentState;
		public final Token.Type tokenType;
		State( State parentState, Token.Type tokenType ) {
			this.parentState = parentState;
			this.tokenType = tokenType;
		}
		State( State parentState ) {
			this( parentState, null );
		}
		State() {
			this( null );
		}
	};
	
	public String filename = "unknown source";
	public int lineNumber = 1, columnNumber = 1;
	protected char[] tokenBuffer = new char[1024];
	protected int length = 0;
	protected State state = State.NO_TOKEN;
	
	public void setSourceLocation( String filename ) {
		setSourceLocation( filename, 1, 1 );
	}
	
	public void setSourceLocation( String filename, int l, int c ) {
		this.filename = filename;		
		this.lineNumber = l;
		this.columnNumber = c;
	}
	
	public void setSourceLocation( SourceLocation sLoc ) {
		setSourceLocation( sLoc.getSourceFilename(), sLoc.getSourceLineNumber(), sLoc.getSourceColumnNumber() );
	}
	
	public SourceLocation getSourceLocation() {
		return new BaseSourceLocation( filename, lineNumber, columnNumber );
	}
	
	protected static boolean isWhitespace( char c ) {
		switch( c ) {
		case ' ': case '\t': case '\r': case '\n':
			return true;
		default:
			return false;
		}
	}
	
	protected static boolean isSymbol( char c ) {
		switch( c ) {
		case '[': case ']': case '(': case ')': case '{': case '}': case ',': case ';': case ':':
			return true;
		default:
			return false;
		}
	}
	
	protected static boolean isQuote( char c ) {
		return c == '\'' || c == '"';
	}
	
	protected static boolean isComment( char c ) {
		return c == '#';
	}
	
	protected static boolean isWordChar( char c ) {
		return !isSymbol(c) && !isWhitespace(c) && !isQuote(c) && !isComment(c);
	}
	
	protected void data( char c ) throws Exception {
		switch( state ) {
		case SINGLE_QUOTED_STRING_ESCAPE:
		case DOUBLE_QUOTED_STRING_ESCAPE:
			switch( c ) {
			case '\\': case '\'': case '"':
				tokenBuffer[length++] = c;
				break;
			case 'n':
				tokenBuffer[length++] = '\n';
				break;
			case 'r':
				tokenBuffer[length++] = '\r';
				break;
			case 't':
				tokenBuffer[length++] = '\t';
				break;
			default:
				throw new ParseError("Invalid escape character: '"+c+"'", getSourceLocation());
			}
			state = state.parentState;
			break;
		case SINGLE_QUOTED_STRING:
			switch( c ) {
			case '\'':
				flushToken( State.WORD_BOUNDARY );
				break;
			case '\\':
				state = State.SINGLE_QUOTED_STRING_ESCAPE;
				break;
			default:
				tokenBuffer[length++] = c;
			}
			break;
		case DOUBLE_QUOTED_STRING:
			switch( c ) {
			case '"':
				flushToken( State.WORD_BOUNDARY );
				break;
			case '\\':
				state = State.DOUBLE_QUOTED_STRING_ESCAPE;
				break;
			default:
				tokenBuffer[length++] = c;
			}
			break;
		case LINE_COMMENT:
			if( c == '\n' ) state = State.NO_TOKEN;
			break;
		case BAREWORD:
			if( isQuote(c) ) {
				throw new ParseError("No quotes allowed here; add some whitespace!", getSourceLocation());
			} else if( isSymbol(c) ) {
				flushToken( State.SYMBOL );
				tokenBuffer[length++] = c;
				flushToken( State.NO_TOKEN );
			} else if( isWhitespace(c) ) {
				flushToken( State.NO_TOKEN );
			} else if( isComment(c) ) {
				flushToken( State.LINE_COMMENT );
			} else {
				tokenBuffer[length++] = c;
			}
			break;
		case WORD_BOUNDARY:
			if( isQuote(c) || isWordChar(c) ) {
				throw new ParseError("No quotes allowed here; add some whitespace!", getSourceLocation());
			}
			// Intentional fall-through!
		case NO_TOKEN:
			if( c == '#' ) {
				state = State.LINE_COMMENT;
			} else if( c == '"' ) {
				state = State.DOUBLE_QUOTED_STRING;
			} else if( c == '\'' ) {
				state = State.SINGLE_QUOTED_STRING;
			} else if( isSymbol(c) ) {
				state = State.SYMBOL;
				tokenBuffer[length++] = c;
				flushToken( State.NO_TOKEN );
			} else if( isComment(c) ) {
				flushToken( State.LINE_COMMENT );
			} else if( isWhitespace(c) ) {
				state = State.NO_TOKEN;
			} else {
				state = State.BAREWORD;
				tokenBuffer[length++] = c;
			}
			break;
		default:
			throw new ParseError("Invalid tokenizer state: "+state, getSourceLocation());
		}
		
		if( c == '\n' ) {
			lineNumber += 1;
			columnNumber = 1;
		} else {
			++columnNumber;
		}
	}
	
	protected void flushToken( State newState ) throws Exception {
		if( state.tokenType != null ) {
			_data( new Token( state.tokenType, new String(tokenBuffer,0,length), filename, lineNumber, columnNumber ) );
		}
		state = newState;
		length = 0;
	}
	
	@Override
    public void data( char[] value ) throws Exception {
		for( char c : value ) data(c);
    }
	
	@Override
    public void end() throws Exception {
		flushToken( State.NO_TOKEN );
		_end();
    }
	
	public static void main(String[] args) throws Exception {
		Tokenizer t = new Tokenizer();
		t.pipe(new StreamDestination<Token>() {
			@Override
            public void data( Token value ) throws Exception {
				System.out.println("Token: '"+value.text+"'");
            }

			@Override
            public void end() throws Exception {
            }
		});
		StreamUtil.pipe( new InputStreamReader(System.in), t );
	}
}
