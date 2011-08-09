package togos.noise2.vm.dftree.data;

import java.security.MessageDigest;

public class DataDa extends Data
{
	public final int length;
	public final double[] x;
	
	public DataDa( int length, double[] x ) {
		this.length = length;
		this.x = x;
	}
	
	public int getLength() {
		return length;
	}
	
	public void digest( MessageDigest md ) {
		byte[] dbuf = new byte[8];
		for( int i=0; i<x.length; ++i ) {
			doubleBytes( x[i], dbuf,  0 );
			md.update(dbuf);
		}
	}
}
