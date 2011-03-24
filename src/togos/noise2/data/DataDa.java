package togos.noise2.data;

import java.security.MessageDigest;

public class DataDa extends Data
{
	public double[] v;
	
	public DataDa( double[] x ) {
		this.v = x;
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
