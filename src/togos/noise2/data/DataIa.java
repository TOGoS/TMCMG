package togos.noise2.data;

import java.security.MessageDigest;

public class DataIa extends Data
{
	public int[] v;
	
	public DataIa( int[] v ) {
		this.v = v;
	}
	
	public int getLength() {
		return v.length;
	}
	
	public void digest( MessageDigest md ) {
		byte[] dbuf = new byte[8];
		for( int i=0; i<v.length; ++i ) {
			doubleBytes( v[i], dbuf,  0 );
			md.update(dbuf);
		}
	}
}
