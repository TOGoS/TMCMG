package togos.minecraft.mapgen.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import togos.jobkernel.uri.BaseRef;
import togos.jobkernel.uri.URIUtil;
import togos.mf.value.URIRef;

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
	
	public static final String string( Object o ) {
		if( o instanceof String ) {
			return (String)o;
		} else if( o instanceof byte[] ) {
			return new String( (byte[])o, UTF8 );
		} else if( o == null ) {
			return null;
		} else {
			throw new RuntimeException("Don't know how to turn "+o.getClass()+" into a string");
		}
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
	
	public static final byte[] readFile( File f ) throws IOException {
		FileInputStream fis = new FileInputStream(f);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int r;
		while( (r = fis.read(buffer)) > 0 ) {
			baos.write(buffer, 0, r);
		}
		fis.close();
		return baos.toByteArray();
	}
	
	public static final URIRef readFileToDataRef( File f ) throws IOException {
		return new BaseRef(URIUtil.makeDataUri(readFile(f)));
	}
}
