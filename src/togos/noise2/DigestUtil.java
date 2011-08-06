package togos.noise2;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.bitpedia.util.Base32;

public class DigestUtil
{
	public static MessageDigest createSha1Digestor() {
		try {
			return MessageDigest.getInstance("SHA-1");
		} catch( NoSuchAlgorithmException e ) {
			throw new RuntimeException(e);
		}
	}
	
	public static String getSha1Urn( MessageDigest md ) {
		return "urn:sha1:" + Base32.encode(md.digest());
	}
	
	public static String getSha1Urn( byte[] data ) {
		MessageDigest md = createSha1Digestor();
		md.update(data);
		return getSha1Urn(md);
	}
	
	public static String getSha1Urn( String s ) {
		try {
			return getSha1Urn(s.getBytes("UTF-8"));
        } catch( UnsupportedEncodingException e ) {
        	throw new RuntimeException(e);
        }
	}
}
