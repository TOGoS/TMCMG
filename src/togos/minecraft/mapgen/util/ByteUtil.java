package togos.minecraft.mapgen.util;

import java.nio.charset.Charset;

public class ByteUtil
{
	public static final Charset UTF8 = Charset.forName("UTF-8");
	public static final byte[] EMPTY = new byte[0];
	
	public static final byte[] bytes( String str ) {
		return str.getBytes(UTF8);
	}
	
	public static final String string( byte[] buf, int begin, int length ) {
		return new String( buf, begin, length, UTF8 );
	}
	
	public static final byte[] slice( byte[] buf, int begin, int length ) {
		if( length <= 0 ) return EMPTY;
		
		byte[] r = new byte[length];
		for( int i=0; i<length; ++i ) {
			r[i] = buf[i+begin];
		}
		return r;
	}
	
	/**
	 * Should be compatible with Arrays.hashCode( byte[] data ),
	 * which is supposedly compatible with List<Byte>#hashCode.
	 */
	public static final int hashCode(byte[] data) {
		return hashCode(data,0,data.length);
	}
	
	public static final int hashCode(byte[] data, int offset, int length) {
		int hashCode = 1;
		for( int i=0; i<length; ++i ) {
			hashCode = 31*hashCode + data[i+offset];
		}
		return hashCode;
	}
	
	public static final boolean equals( byte[] b1, byte[] b2 ) {
		if( b1.length != b2.length ) {
			return false;
		}
		for( int i=0; i<b1.length; ++i ) {
			if( b1[i] != b2[i] ) return false;
		}
		return true;
	}
}
