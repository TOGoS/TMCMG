package togos.minecraft.mapgen.util;

import junit.framework.TestCase;

public class IntCodecTest extends TestCase
{
	byte[] byf = new byte[16];
	
	public void testInt48Codec() {
		long orig = 0x000010B31C123D2Fl;
		Util.encodeInt48(orig, byf, 5);
		assertEquals( orig, Util.decodeInt48(byf, 5));

		long nOrig = 0xFFFF80B31C123D2Fl;
		Util.encodeInt48(nOrig, byf, 7);
		assertEquals( nOrig, Util.decodeInt48(byf, 7));
	}
	
	public void testInt32Codec() {
		int orig = 0x6013E80A;
		Util.encodeInt32(orig, byf, 5);
		assertEquals( orig, Util.decodeInt32(byf, 5));

		int nOrig = 0xEABC5319;
		Util.encodeInt32(nOrig, byf, 7);
		assertEquals( nOrig, Util.decodeInt32(byf, 7));
	}
	
	public void testInt16Codec() {
		short orig = 0x5319;
		Util.encodeInt16(orig, byf, 5);
		assertEquals( orig, Util.decodeInt16(byf, 5));
		
		short nOrig = -0x5319;
		Util.encodeInt16(nOrig, byf, 7);
		assertEquals( nOrig, Util.decodeInt16(byf, 7));
	}
}
