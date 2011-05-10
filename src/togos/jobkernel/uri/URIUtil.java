package togos.jobkernel.uri;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import togos.jobkernel.util.Base64;

public class URIUtil {
	public static final char[] HEXCHARS = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
	
	protected static final boolean bac( byte[] arr, byte b ) {
		for( int i=arr.length-1; i>=0; --i ) if(arr[i] == b) return true;
		return false;
	}
	protected static byte[] join( byte[] x, byte[] y ) {
		byte[] r = new byte[x.length + y.length];
		for( int i=0; i<x.length; ++i ) r[i] = x[i];
		for( int i=0; i<y.length; ++i ) r[i+x.length] = y[i];
		return r;
	}
	protected static byte[] filter( byte[] arr, byte[] remove ) {
		int length = arr.length;
		for( int i=0; i<arr.length; ++i ) {
			if( bac(remove,arr[i]) ) --length;
		}
		byte[] res = new byte[length];
		int j=0;
		for( int i=0; i<arr.length; ++i ) {
			if( !bac(remove,arr[i]) ) res[j++] = arr[i];
		}
		return res;
	}
	
	protected static byte[] EMPTY = new byte[0];
	
	/*
	 * Based on info from
	 * http://labs.apache.org/webarch/uri/rfc/rfc3986.html#characters
	 */

	/** Non-alphanumeric characters that do not normally have special
	 * meaning in URIs but that are normally escaped anyway */
	public static byte[] UNRESERVED_CHARS = new byte[] {
		'-','.','_','~'
	};
	public static byte[] GEN_DELIMS = new byte[] {
		':','/','?','#','[',']','@'
	};
	public static byte[] SUB_DELIMS = new byte[] {
		'!','$','&','\'','(',')','*','+',',',';','='
	};
	
	/** It is generally safe to include these unescaped within a path component */
	public static byte[] PATH_SEGMENT_SAFE = join(
		UNRESERVED_CHARS,
		new byte[] { ':','!','$','\'','(',')','*',',' }
	);

	public static byte[] PATH_SAFE = join(
		PATH_SEGMENT_SAFE,
		new byte[] { '/' }
	);
	
	/** Characters that have special meaning in URIs */
	public static byte[] RESERVED_CHARS = join(GEN_DELIMS,SUB_DELIMS);

	/** Non-alphanumeric characters that are valid in URIs */
	public static byte[] VALID_CHARS = join(new byte[]{'%'},join(RESERVED_CHARS,UNRESERVED_CHARS));
	
	public static String uriEncode( byte[] inbytes, byte[] doNotEscape ) {
		char[] outchars = new char[inbytes.length*3];
		int inidx=0, outidx=0;
		while( inidx < inbytes.length ) {
			byte c = inbytes[inidx++];
			if( (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') ||
			    (c >= '0' && c <= '9') || bac(doNotEscape,c) )
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
	
	public static String uriEncode( byte[] inbytes ) {
		return uriEncode( inbytes, EMPTY );
	}
	
	public static String uriEncode( String text, byte[] doNotEncode ) {
		if( text == null ) return null;
		byte[] inbytes;
		try {
			inbytes = text.getBytes("UTF-8");
		} catch( UnsupportedEncodingException e ) {
			throw new RuntimeException(e);
		}
		return uriEncode( inbytes, doNotEncode );
	}
	
	public static String uriEncode( String text ) {
		return uriEncode( text, UNRESERVED_CHARS );
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
	
	//// Data URI stuff
	
	public static String makeDataUri( byte[] data ) {
		return "data:," + uriEncode(data);
	}

	public static String makeDataUri( String data ) {
		try {
			return makeDataUri(data.getBytes("UTF-8"));
		} catch( UnsupportedEncodingException e ) {
			throw new RuntimeException(e);
		}
	}

	public static byte[] decodeDataUri(String uri, Map metadataDest) {
		if( !uri.startsWith("data:") ) return null;
		int commaPos = uri.indexOf(',');
		if( commaPos == -1 ) throw new RuntimeException("Invalid data URI has no comma: " + uri);
		String[] junk = uri.substring("data:".length(),commaPos).split(";");
		boolean base64Encoded = false;
		for( int i=0; i<junk.length; ++i ) {
			String junkPart = junk[i];
			if( "base64".equals(junkPart) ) {
				base64Encoded = true;
				// Someday I might want to handle different charsets and things...
			} else {
				metadataDest.put("http://purl.org/dc/terms/format", junkPart);
			}
		}
		byte[] data;
		if( base64Encoded ) {
			data = Base64.decode(uri.substring(commaPos+1));
		} else {
			data = uriDecodeBytes( uri.substring(commaPos+1), false );
		}
		return data;
	}

}
