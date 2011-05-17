package togos.noise2.data;

import java.security.MessageDigest;

public class DataDa extends Data
{
	public final double[] x;
	
	public DataDa( double[] x ) {
		this.x = x;
	}
	
	public int getLength() {
		return x.length;
	}
	
	public void digest( MessageDigest md ) {
		byte[] dbuf = new byte[8];
		for( int i=0; i<x.length; ++i ) {
			doubleBytes( x[i], dbuf,  0 );
			md.update(dbuf);
		}
	}
}
