package togos.noise2.vm.dftree.data;

import java.security.MessageDigest;

public class DataDaDaDa extends DataDaDa
{
	public double[] z;
	
	public DataDaDaDa( double[] x, double[] y, double[] z, String urn ) {
		super( x, y );
		this.z = z;
		this.dataId = urn;
	}
	
	public DataDaDaDa( double[] x, double[] y, double[] z ) {
		this( x, y, z, null );
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
