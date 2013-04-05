package togos.minecraft.mapgen.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

public class Util
{
	public static boolean isHelpArgument( String arg ) {
		return "-?".equals(arg) || "-h".equals(arg) || "-help".equals(arg) || "--help".equals(arg);
	}
	
	public static final String string( byte[] b, int offset, int length ) {
		try {
			return new String( b, offset, length, "UTF-8" );
		} catch( UnsupportedEncodingException e ) {
			throw new RuntimeException(e);
		}
	}
	
	public static final byte[] bytes( String s ) {
		try {
			return s.getBytes("UTF-8");
		} catch( UnsupportedEncodingException e ) {
			throw new RuntimeException(e);
		}
	}
	
	public static final String string( Object o ) {
		if( o instanceof String ) {
			return (String)o;
		} else if( o instanceof byte[] ) {
			return string( (byte[])o, 0, ((byte[])o).length );
		} else if( o == null ) {
			return null;
		} else {
			throw new RuntimeException("Don't know how to turn "+o.getClass()+" into a String");
		}
	}
	
	public static final void readFully( InputStream is, byte[] buf, int offset, int length )
		throws IOException
	{
		while( length > 0 ) {
			int read = is.read( buf, offset, length );
			if( read < 1 ) {
				throw new IOException("Encountered end of stream while expecting to read "+length+" bytes");
			}
			offset += read;
			length -= read;
		}
	}
	
	public static final byte[] readFully( InputStream is, int length ) throws IOException {
		byte[] buf = new byte[length];
		readFully( is, buf, 0, length );
		return buf;
	}
	
	public static final byte[] readFile( File f ) throws IOException {
		long length = f.length();
		if( length > 1<<24 ) { // Arbitrary limit
			throw new RuntimeException("File "+f+" is too big to load into memory");
		}
		FileInputStream fis = new FileInputStream(f);
		byte[] buffer = new byte[(int)length];
		readFully( fis, buffer, 0, (int)length );
		return buffer;
	}
	
	/**
	 * Should be compatible with Arrays.hashCode( byte[] data ),
	 * which is supposedly compatible with List<Byte>#hashCode.
	 */
	public static final int hashCode( byte[] data, int offset, int length ) {
		int hashCode = 1;
		for( int i=0; i<length; ++i ) {
			hashCode = 31*hashCode + data[i+offset];
		}
		return hashCode;
	}
	
	//// Serialization help ////
	
	public static final void encodeInt16( short i, byte[] dest, int offset ) {
		dest[offset+0] = (byte)(i >> 8);
		dest[offset+1] = (byte)(i >> 0);
	}
	
	public static final void encodeInt32( int i, byte[] dest, int offset ) {
		dest[offset+0] = (byte)(i >> 24);
		dest[offset+1] = (byte)(i >> 16);
		dest[offset+2] = (byte)(i >>  8);
		dest[offset+3] = (byte)(i >>  0);
	}
	
	public static final void encodeInt48( long i, byte[] dest, int offset ) {
		dest[offset+0] = (byte)(i >> 40);
		dest[offset+1] = (byte)(i >> 32);
		dest[offset+2] = (byte)(i >> 24);
		dest[offset+3] = (byte)(i >> 16);
		dest[offset+4] = (byte)(i >>  8);
		dest[offset+5] = (byte)(i >>  0);
	}
	
	public static final short decodeInt16( byte[] buf, int offset ) {
		return (short)(
			((short)(buf[offset+0]&0xFF)<<8) |
			((short)(buf[offset+1]&0xFF)<<0)
		);
	}
	
	public static final int decodeInt32( byte[] buf, int offset ) {
		return (
			((int)(buf[offset+0]&0xFF)<<24) |
			((int)(buf[offset+1]&0xFF)<<16) |
			((int)(buf[offset+2]&0xFF)<< 8) |
			((int)(buf[offset+3]&0xFF)<< 0)
		);
	}
	
	public static final long decodeInt48( byte[] buf, int offset ) {
		return (long)(
			((long)(buf[offset+0]&0xFF)<<56) |
			((long)(buf[offset+1]&0xFF)<<48) |
			((long)(buf[offset+2]&0xFF)<<40) |
			((long)(buf[offset+3]&0xFF)<<32) |
			((long)(buf[offset+4]&0xFF)<<24) |
			((long)(buf[offset+5]&0xFF)<<16)
		) >> 16;
	}
}
