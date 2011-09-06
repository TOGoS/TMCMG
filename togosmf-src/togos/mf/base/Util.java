package togos.mf.base;

import java.io.UnsupportedEncodingException;

import togos.mf.api.Message;
import togos.mf.api.Request;
import togos.mf.api.Response;
import togos.mf.value.ByteChunk;

public class Util
{
	public static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
	
	public static final byte[] bytes( String str ) {
		try {
			return str.getBytes("UTF-8");
		} catch( UnsupportedEncodingException e ) {
			throw new RuntimeException(e);
		}
	}
	
	public static final String string( byte[] arr, int offset, int len ) {
		try {
			return new String( arr, offset, len, "UTF-8" );
		} catch( UnsupportedEncodingException e ) {
			throw new RuntimeException(e);
		}
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
	
	public static final boolean equals( ByteChunk c1, ByteChunk c2 ) {
		if( c1.getSize() != c2.getSize() ) return false;
		
		int o1 = c1.getOffset(), o2 = c2.getOffset();
		byte[] b1 = c1.getBuffer(), b2 = c2.getBuffer();
		for( int j=c1.getSize()-1; j>=0; --j ) {
			if( b1[j+o1] != b2[j+o2] ) return false;
		}
		return true;
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
	
	public static final byte[] slice( byte[] buf, int begin, int length ) {
		if( length <= 0 ) return EMPTY_BYTE_ARRAY;
		
		byte[] r = new byte[length];
		for( int i=0; i<length; ++i ) {
			r[i] = buf[i+begin];
		}
		return r;
	}
	
	public static final int contentHashCode( Object c ) {
		if( c == null ) return 0;
		if( c instanceof byte[] ) return hashCode( (byte[])c );
		return c.hashCode();
	}
	
	public static final int hashCode( Request req ) {
		int hashCode = 1;
		hashCode = hashCode*31 + req.getVerb().hashCode();
		hashCode = hashCode*31 + req.getResourceName().hashCode();
		hashCode = hashCode*31 + req.getMetadata().hashCode();
		hashCode = hashCode*31 + req.getContentMetadata().hashCode();
		hashCode = hashCode*31 + contentHashCode(req.getContent());
		return hashCode;
	}

	public static final int hashCode( Response res ) {
		int hashCode = 1;
		hashCode = hashCode*31 + res.getStatus();
		hashCode = hashCode*31 + res.getMetadata().hashCode();
		hashCode = hashCode*31 + res.getContentMetadata().hashCode();
		hashCode = hashCode*31 + contentHashCode(res.getContent());
		return hashCode;
	}
	
	public static final int hashCode( Message m ) {
		int hashCode = 1;
		hashCode = hashCode*31 + m.getMessageType();
		hashCode = hashCode*31 + (int)(m.getSessionId());
		hashCode = hashCode*31 + (int)(m.getSessionId() >> 32);
		hashCode = hashCode*31 + m.getPayload().hashCode();
		return hashCode;
	}
	
	public static final boolean contentEquals( Object c1, Object c2 ) {
		if( c1 == null && c2 == null ) return true;
		if( c1 == null || c2 == null ) return false;
		if( c1 instanceof byte[] && c2 instanceof byte[] ) {
			return equals( (byte[])c1, (byte[])c2 );
		}
		return c1.equals(c2);
	}
	
	public static final boolean equals( Request r1, Request r2 ) {
		if( !r1.getVerb().equals(r2.getVerb()) ) return false;
		if( !r1.getResourceName().equals(r2.getResourceName()) ) return false;
		if( !r1.getMetadata().equals(r2.getMetadata()) ) return false;
		if( !r1.getContentMetadata().equals(r2.getContentMetadata()) ) return false;
		if( !contentEquals(r1.getContent(), r2.getContent()) ) return false;
		return true;
	}
	
	public static final boolean equals( Response r1, Response r2 ) {
		if( r1.getStatus() != r2.getStatus() ) return false;
		if( !r1.getMetadata().equals(r2.getMetadata()) ) return false;
		if( !r1.getContentMetadata().equals(r2.getContentMetadata()) ) return false;
		if( !contentEquals(r1.getContent(), r2.getContent()) ) return false;
		return true;
	}
	
	public static final boolean equals( Message m1, Message m2 ) {
		return
			m1.getMessageType() == m2.getMessageType() &&
			m1.getSessionId() == m2.getSessionId() &&
			m1.getPayload().equals(m2.getPayload());
	}
}
