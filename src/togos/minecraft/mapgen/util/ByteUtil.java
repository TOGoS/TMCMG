package togos.minecraft.mapgen.util;

import java.nio.charset.Charset;

public class ByteUtil
{
	static final Charset UTF8 = Charset.forName("UTF-8");
	
	public static byte[] bytes( String str ) {
		return str.getBytes(UTF8);
	}
	
	public static String string( byte[] buf, int begin, int length ) {
		return new String( buf, begin, length, UTF8 );
	}
	
	public static byte[] slice( byte[] buf, int begin, int length ) {
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
		int hashCode = 1;
		for( int i=0; i<data.length; ++i ) {
			hashCode = 31*hashCode + data[i];
		}
		return hashCode;
	}

	public static boolean equals( byte[] b1, byte[] b2 ) {
		if( b1.length != b2.length ) {
			return false;
		}
		for( int i=0; i<b1.length; ++i ) {
			if( b1[i] != b2[i] ) return false;
		}
		return true;
	}
}
