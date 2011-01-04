package togos.noise2.data;

import java.security.MessageDigest;

import togos.noise2.DigestUtil;

public abstract class Data
{
	protected String urn = null;
	
	protected void intBytes( long l, byte[] b, int o ) {
		b[o+0] = (byte)((l >> 24) & 0xFF);
		b[o+1] = (byte)((l >> 16) & 0xFF);
		b[o+2] = (byte)((l >>  8) & 0xFF);
		b[o+3] = (byte)((l >>  0) & 0xFF);
	}
	
	protected void longBytes( long l, byte[] b, int o ) {
		b[o+0] = (byte)((l >> 56) & 0xFF);
		b[o+1] = (byte)((l >> 48) & 0xFF);
		b[o+2] = (byte)((l >> 40) & 0xFF);
		b[o+3] = (byte)((l >> 32) & 0xFF);
		b[o+4] = (byte)((l >> 24) & 0xFF);
		b[o+5] = (byte)((l >> 16) & 0xFF);
		b[o+6] = (byte)((l >>  8) & 0xFF);
		b[o+7] = (byte)((l >>  0) & 0xFF);
	}
	
	protected void doubleBytes( double d, byte[] b, int o ) {
		longBytes( Double.doubleToLongBits(d), b, o );
	}
	
	public abstract void digest( MessageDigest md );
	
	public synchronized String getSha1Urn() {
		if( urn == null ) {
			MessageDigest sha1 = DigestUtil.createSha1Digestor();
			digest( sha1 );
			urn = DigestUtil.getSha1Urn(sha1);
		}
		return urn;
	}
}
