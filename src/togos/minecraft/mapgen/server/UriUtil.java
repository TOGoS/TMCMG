package togos.minecraft.mapgen.server;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class UriUtil {
	public static final char[] HEXCHARS = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};

	public static String uriEncode( byte[] inbytes, boolean keepUriSpecialChars ) {
		char[] outchars = new char[inbytes.length*3];
		int inidx=0, outidx=0;
		while( inidx < inbytes.length ) {
			byte c = inbytes[inidx++];
			if( (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') ||
			    (c >= '0' && c <= '9') || (c == '.') || (c == ',') ||
			    (c == '/') || (c == '-') || (c == '_') ||
			    (keepUriSpecialChars && (
			    	(c == '%') || (c == '+') || (c == ':') || (c == ';') ||
			    	(c == '?') || (c == '&') || (c == '=') || (c == '#')
			    )))
			{
				outchars[outidx++] = (char)c;
			} else {
				outchars[outidx++] = '%';
				outchars[outidx++] = HEXCHARS[(c>>4)&0xF];
				outchars[outidx++] = HEXCHARS[ c    &0xF];
			}
		}
		return new String(outchars,0,outidx);
	}
	
	public static String uriEncode( String text, boolean keepUriSpecialChars ) {
		if( text == null ) return null;
		byte[] inbytes;
		try {
			inbytes = text.getBytes("UTF-8");
		} catch( UnsupportedEncodingException e ) {
			throw new RuntimeException(e);
		}
		return uriEncode( inbytes, keepUriSpecialChars );
	}
	
	public static String uriEncode( String text ) {
		return uriEncode( text, false );
	}

	public static String sanitizeUri( String text ) {
		return uriEncode( text, true );
	}

	protected static final int hexValue( char digit ) {
		return digit <= '9' ? digit - '0' : digit <= 'F' ? digit - 'A' + 10 : digit - 'a' + 10;
	}
	
	protected static final int hexValue( char hiDigit, char loDigit ) {
		return (hexValue(hiDigit) << 4) | hexValue(loDigit);
	}
	
	public static byte[] uriDecodeBytes( String text, boolean plusAsSpace ) {
		char[] inchars = text.toCharArray();
		int escapecount = 0;
		for( int i=inchars.length-1; i>=0; --i ) {
			if( inchars[i] == '%' ) ++escapecount;
		}
		byte[] outbytes = new byte[inchars.length - (escapecount<<1)];
		int inidx=0, outidx=0;
		while( inidx < inchars.length ) {
			char c = inchars[inidx++];
			if( c == '%' ) {
				char hiDigit = inchars[inidx++];
				char loDigit = inchars[inidx++];
				outbytes[outidx++] = (byte)hexValue(hiDigit, loDigit);
			} else if( plusAsSpace && c == '+' ) {
				outbytes[outidx++] = (byte)' ';
			} else {
				outbytes[outidx++] = (byte)c;
			}
		}
		return outbytes;
	}
	
	public static String uriDecode( String text, boolean plusAsSpace ) {
		try {
			return new String(uriDecodeBytes(text, plusAsSpace), "UTF-8");
		} catch( UnsupportedEncodingException e ) {
			throw new RuntimeException(e);
		}
	}
	
	public static String uriDecode( String text ) {
		return uriDecode( text, false );
	}
	
	public static Map parseQSArgs( String qs ) {
		HashMap args = new HashMap();
		
		String[] pairs = qs.split("&");
		for( int i=0; i<pairs.length; ++i ) {
			String[] pair = pairs[i].split("=",2);
			for( int j=0; j<pair.length; ++j ) {
				pair[j] = uriDecode(pair[j], true);
			}
			if( pair.length == 1 ) {
				args.put(pair[0],pair[0]);
			} else {
				args.put(pair[0],pair[1]);
			}
		}
		return args;
	}
}
