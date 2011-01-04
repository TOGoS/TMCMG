package togos.noise2.data;

import java.security.MessageDigest;

public class DataDaDaDa extends Data
{
	public double[] x;
	public double[] y;
	public double[] z;
	
	public DataDaDaDa( double[] x, double[] y, double[] z ) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public int getLength() {
		return x.length;
	}
	
	public void digest( MessageDigest md ) {
		byte[] dbuf = new byte[24];
		for( int i=0; i<x.length; ++i ) {
			doubleBytes( x[i], dbuf,  0 );
			doubleBytes( y[i], dbuf,  8 );
			doubleBytes( z[i], dbuf, 16 );
			md.update(dbuf);
		}
	}
}
