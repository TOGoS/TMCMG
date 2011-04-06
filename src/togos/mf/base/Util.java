package togos.mf.base;

import togos.mf.api.Request;
import togos.mf.api.Response;

public class Util
{
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
	
	public static final int contentHashCode( Object c ) {
		if( c == null ) return 0;
		if( c instanceof byte[] ) return hashCode( (byte[])c );
		return c.hashCode();
	}
	
	public static int hashCode( Request req ) {
		int hashCode = 1;
		hashCode = hashCode*31 + req.getVerb().hashCode();
		hashCode = hashCode*31 + req.getResourceName().hashCode();
		hashCode = hashCode*31 + req.getMetadata().hashCode();
		hashCode = hashCode*31 + req.getContentMetadata().hashCode();
		hashCode = hashCode*31 + contentHashCode(req.getContent());
		return hashCode;
	}

	public static int hashCode( Response res ) {
		int hashCode = 1;
		hashCode = hashCode*31 + res.getStatus();
		hashCode = hashCode*31 + res.getMetadata().hashCode();
		hashCode = hashCode*31 + res.getContentMetadata().hashCode();
		hashCode = hashCode*31 + contentHashCode(res.getContent());
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
}
