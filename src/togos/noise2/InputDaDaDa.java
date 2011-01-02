package togos.noise2;

import java.security.MessageDigest;

public class InputDaDaDa
{
	public int count;
	public double[] x;
	public double[] y;
	public double[] z;
	protected String urn;
	
	public InputDaDaDa( int count, double[] x, double[] y, double[] z ) {
		this.count = count;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	protected void doubleBytes( double d, byte[] b, int o ) {
		long l = Double.doubleToLongBits(d);
		b[o+0] = (byte)((l >> 56) & 0xFF);
		b[o+1] = (byte)((l >> 48) & 0xFF);
		b[o+2] = (byte)((l >> 40) & 0xFF);
		b[o+3] = (byte)((l >> 32) & 0xFF);
		b[o+4] = (byte)((l >> 24) & 0xFF);
		b[o+5] = (byte)((l >> 16) & 0xFF);
		b[o+6] = (byte)((l >>  8) & 0xFF);
		b[o+7] = (byte)((l >>  0) & 0xFF);
	}
	
	public synchronized String getDataUrn() {
		if( urn == null ) {
			MessageDigest sha1 = DigestUtil.createSha1Digestor();
			byte[] dbuf = new byte[24];
			for( int i=0; i<count; ++i ) {
				doubleBytes( x[i], dbuf,  0 );
				doubleBytes( y[i], dbuf,  8 );
				doubleBytes( z[i], dbuf, 16 );
				sha1.update(dbuf);
			}
			urn = DigestUtil.getSha1Urn(sha1);
		}
		return urn;
	}
}
