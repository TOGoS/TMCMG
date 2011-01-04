package togos.noise2.data;

import java.security.MessageDigest;

public class DataDaDa extends Data
{
	public double[] x;
	public double[] y;
	
	public DataDaDa( double[] x, double[] y ) {
		this.x = x;
		this.y = y;
	}
	
	public int getLength() {
		return x.length;
	}
	
	public void digest( MessageDigest md ) {
		byte[] dbuf = new byte[16];
		for( int i=0; i<x.length; ++i ) {
			doubleBytes( x[i], dbuf,  0 );
			doubleBytes( y[i], dbuf,  8 );
			md.update(dbuf);
		}
	}
}
