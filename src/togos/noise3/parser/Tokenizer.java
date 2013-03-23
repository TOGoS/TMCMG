package togos.noise3.parser;

import java.io.InputStreamReader;

import togos.noise3.asyncstream.BaseStreamSource;
import togos.noise3.asyncstream.StreamDestination;
import togos.noise3.asyncstream.StreamUtil;

public class Tokenizer extends BaseStreamSource<Token> implements StreamDestination<char[]>
{
	enum State {
		NO_TOKEN( null ),
		BAREWORD( Token.Type.SYMBOL ),
		QUOTED_STRING( Token.Type.QUOTED_STRING );
		
		public final Token.Type tokenType;
		State( Token.Type tokenType ) {
			this.tokenType = tokenType;
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
	
	protected boolean isWhitespaceToken( char c ) {
		switch( c ) {
		case ' ': case '\t': case '\r': case '\n':
			return true;
		default:
			return false;
		}
	}
	
	protected boolean isAutoToken( char c ) {
		switch( c ) {
		case '(': case ')': case '{': case '}': case ',': case '.': case ';':
			return true;
		default:
			return false;
		}
	}
	
	protected void data( char c ) throws Exception {
		if( isWhitespaceToken(c) ) {
			flushToken();
		} else if( isAutoToken(c) ) {
			flushToken();
			state = State.BAREWORD;
			tokenBuffer[length++] = c;
			flushToken();
		} else {
			state = State.BAREWORD;
			tokenBuffer[length++] = c;
		}
	}
	
	protected void flushToken() throws Exception {
		if( state.tokenType != null ) {
			_data( new Token( state.tokenType, new String(tokenBuffer,0,length), filename, lineNumber, columnNumber ) );
		}
		state = State.NO_TOKEN;
		length = 0;
	}
	
	@Override
    public void data( char[] value ) throws Exception {
		for( char c : value ) data(c);
    }
	
	@Override
    public void end() throws Exception {
		flushToken();
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
