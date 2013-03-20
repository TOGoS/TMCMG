package togos.noise2.vm.dftree.data;

import java.security.MessageDigest;

public class DataDaDaDa extends DataDaDa
{
	public double[] z;
	
	public DataDaDaDa( int vectorSize, double[] x, double[] y, double[] z, String dataId ) {
		super( vectorSize, x, y, dataId );
		this.z = z;
	}
	
	public DataDaDaDa( int vectorSize, double[] x, double[] y, double[] z ) {
		this( vectorSize, x, y, z, null );
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
