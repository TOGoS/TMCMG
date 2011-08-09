package togos.noise2.vm.dftree.data;

import java.security.MessageDigest;

public class DataDaDa extends DataDa
{
	public double[] y;
	
	public DataDaDa( int length, double[] x, double[] y ) {
		super(length, x);
		this.y = y;
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
