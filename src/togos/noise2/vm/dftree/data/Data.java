package togos.noise2.vm.dftree.data;

import java.security.MessageDigest;
import java.util.Random;

import togos.noise2.DigestUtil;

public abstract class Data
{
	static String guidPfx;
	static {
		long d = System.currentTimeMillis();
		Random r = new Random(123123);
		r.nextLong(); r.nextLong(); r.nextLong();
		guidPfx = Long.toString(d ^ r.nextLong(),16) + "-";
	}
	static long guidIncr = 1;
	protected static synchronized String nextGuid() {
		return guidPfx + Long.toString(guidIncr++,16);
	}
	
	public String dataId = null;
	
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
	
	protected String getSha1Urn() {
		MessageDigest sha1 = DigestUtil.createSha1Digestor();
		digest( sha1 );
		return DigestUtil.getSha1Urn(sha1);
	}
	
	protected String generateDataId() {
		return nextGuid();
		// If hashing wasn't so expensive this would be way better...
		// return getSha1Urn();
	}
	
	public synchronized String getDataId() {
		if( dataId == null ) {
			dataId = generateDataId();
		}
		return dataId;
	}
}
