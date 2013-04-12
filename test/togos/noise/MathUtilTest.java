package togos.noise;

import junit.framework.TestCase;

public class MathUtilTest extends TestCase
{
	
	protected void assertFDM( double expected, double num, double den ) {
		double actual = MathUtil.safeFlooredDivisionModulus(num, den);
		assertTrue( expected + " != " + actual, expected == actual );
	}
	
	public void testSafeFlooredDivisionModulus() {
		// Different things that are zero
		assertFDM( 0, 0, 0 );
		assertFDM( 0, 0, 1 );
		assertFDM( 0, 0, -1 );
		assertFDM( 0, 1, 0 );
		assertFDM( 0, -1, 0 );
		
		// With fractions
		assertFDM( 0.5, 10.5, 10 );
		
		// With positive denominators
		assertFDM( 0,  110, 10 );
		assertFDM( 0, -110, 10 );
		assertFDM( 3,  113, 10 );
		assertFDM( 7, -113, 10 );
		
		// With negative denominators
		assertFDM(  0,  110, -10 );
		assertFDM(  0, -110, -10 );
		assertFDM( -7,  113, -10 );
		assertFDM( -3, -113, -10 );
	}
}
