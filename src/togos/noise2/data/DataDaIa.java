package togos.noise2.data;

import java.security.MessageDigest;

public class DataDaIa extends Data
{
	public final double[] d;
	public final int[] i;
	
	public DataDaIa( double[] d, int[] i ) {
		this.d = d;
		this.i = i;
	}
	
	public int getLength() {
		return d.length;
	}
	
	public void digest( MessageDigest md ) {
		byte[] dbuf = new byte[12];
		for( int j=0; j<d.length; ++j ) {
			doubleBytes( d[j], dbuf,  0 );
			intBytes( i[j], dbuf, 8 );
			md.update(dbuf);
		}
	}
}
