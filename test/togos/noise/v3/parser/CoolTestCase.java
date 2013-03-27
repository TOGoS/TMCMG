package togos.noise.v3.parser;

import junit.framework.TestCase;

public class CoolTestCase extends TestCase {
	protected void assertInstanceOf( Class<?> c, Object obj ) {
		assertNotNull( "Expected non-null value", obj );
		assertTrue( "Expected instance of "+c+", but got "+obj.getClass(), c.isAssignableFrom(obj.getClass()) );
	}
}
